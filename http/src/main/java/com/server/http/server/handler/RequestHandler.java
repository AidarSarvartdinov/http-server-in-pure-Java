package com.server.http.server.handler;

import com.server.http.server.bind.HandlerMethod;
import com.server.http.server.common.HttpStatus;
import com.server.http.server.interceptor.InterceptorHolder;
import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;
import com.server.http.server.service.HandlerMethodResolver;

import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private final Socket clientSocket;
    private final HandlerMethodResolver handlerMethodResolver;

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
                System.out.println("Context is null");
                return;
            }

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
            throw new RuntimeException("Handler exception: " + e);
        } finally {
            try {
                clientSocket.close();
                System.out.println("Socket closed");
            } catch (IOException e) {
                System.out.println("Exception trying to close socket");
            }
        }
    }

}
