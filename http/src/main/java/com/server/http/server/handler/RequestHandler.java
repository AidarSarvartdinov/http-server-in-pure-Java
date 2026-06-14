package com.server.http.server.handler;

import com.server.http.server.bind.HandlerMethod;
import com.server.http.server.common.HttpStatus;
import com.server.http.server.exception.HandlerException;
import com.server.http.server.interceptor.InterceptorHolder;
import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;
import com.server.http.server.service.HandlerMethodResolver;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles a single HTTP request from a client socket.
 * <p>
 * Reads the HTTP request, parses it into a {@link RequestContext}, resolves a matching
 * {@link HandlerMethod}, invokes the handler, and writes the response back.
 * </p>
 * <p>
 * Error handling:
 * <ul>
 *   <li>Malformed request → {@code 400 Bad Request}</li>
 *   <li>No matching handler → {@code 404 Not Found}</li>
 *   <li>Handler throws {@code HandlerException} → {@code 500 Internal Server Error}</li>
 *   <li>Handler returns an error status → that error response is sent</li>
 * </ul>
 * </p>
 */
public class RequestHandler implements Runnable {
    private final Socket clientSocket;
    private static final HandlerMethodResolver handlerMethodResolver = new HandlerMethodResolver();
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            RequestContext context = RequestContext.buildContext(reader);
            OutputStream outputStream = clientSocket.getOutputStream();

            if (context == null) {
                log.warning("Context is null, sending 400");
                outputStream.write(ResponseContext.build(HttpStatus.BAD_REQUEST).getResponseAsBytes());
                outputStream.flush();
                return;
            }

            log.log(Level.INFO, "Received request {0} {1}",
                    new Object[] { context.getMethod().toString(), context.getPath() });
            
            HandlerMethod handlerMethod = handlerMethodResolver.resolve(context);

            if (handlerMethod == null) {
                outputStream.write(ResponseContext.build(HttpStatus.NOT_FOUND).getResponseAsBytes());
                outputStream.flush();
            } else {
                ResponseContext responseContext;
                try {
                    responseContext = handlerMethod.invoke(context);
                } catch (HandlerException e) {
                    log.log(Level.SEVERE, "Handler invocation failed", e);
                    responseContext = ResponseContext.build(HttpStatus.INTERNAL_SERVER_ERROR);
                }

                if (responseContext.getStatus().isError()) {
                    outputStream.write(responseContext.getResponseAsBytes());
                    outputStream.flush();
                } else {
                    InterceptorHolder.getInstance().beforeSendResponse(context, responseContext);
                    outputStream.write(responseContext.getResponseAsBytes());
                    outputStream.flush();
                }
            }

        } catch (IOException e) {
            log.log(Level.WARNING, "IO error processing request", e);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unexpected error in request handler", e);
        } finally {
            try {
                clientSocket.close();
                log.log(Level.FINE, "Socket closed for {0}", clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                log.log(Level.WARNING, "Exception trying to close socket", e);
            }
        }
    }

}
