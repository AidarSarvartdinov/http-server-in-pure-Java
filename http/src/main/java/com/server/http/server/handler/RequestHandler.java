package com.server.http.server.handler;

import com.server.http.server.bind.HandlerMethod;
import com.server.http.server.common.HttpStatus;
import com.server.http.server.interceptor.InterceptorHolder;
import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;
import com.server.http.server.service.HandlerMethodResolver;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private final Socket clientSocket;
    private final HandlerMethodResolver handlerMethodResolver;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.handlerMethodResolver = new HandlerMethodResolver();
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            RequestContext context = RequestContext.buildContext(reader);

            if (context == null) {
                log.warning("Context is null");
                clientSocket.close();
                return;
            }

            log.log(Level.INFO, "Received request {0} {1}",
                    new Object[] { context.getMethod().toString(), context.getPath() });
            OutputStream outputStream = clientSocket.getOutputStream();
            HandlerMethod handlerMethod = handlerMethodResolver.resolve(context);

            if (handlerMethod == null) {
                outputStream.write(ResponseContext.build(HttpStatus.NOT_FOUND).getResponseAsBytes());
                outputStream.flush();
            } else {
                ResponseContext responseContext = handlerMethod.invoke(context);
                if (responseContext.getStatus().isError()) {
                    outputStream.write(ResponseContext.build(responseContext.getStatus()).getResponseAsBytes());
                    outputStream.flush();
                } else {
                    InterceptorHolder.getInstance().beforeSendResponse(context, responseContext);
                    outputStream.write(responseContext.getResponseAsBytes());
                    outputStream.flush();
                }
            }

        } catch (IOException e) {
            // throw new RuntimeException("Handler exception: " + e);
            log.log(Level.WARNING, "IO error processing request", e);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unexpected error in request handler", e);
        } finally {
            try {
                clientSocket.close();
                // System.out.println("Socket closed");
                log.log(Level.FINE, "Socket closed for {0}", clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                // System.out.println("Exception trying to close socket");
                log.log(Level.WARNING, "Exception trying to close socket", e);
            }
        }
    }

}
