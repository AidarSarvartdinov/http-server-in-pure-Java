package com.server.http.server.handler;

import com.server.http.server.common.HttpStatus;
import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;

import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private final Socket clientSocket;

    public RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
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

            if (context.pathsIsEqualsTo("/")) {
                outputStream.write(ResponseContext.build(HttpStatus.OK).getResponseAsBytes());
                outputStream.flush();
            } else {
                outputStream.write(ResponseContext.build(HttpStatus.NOT_FOUND).getResponseAsBytes());
                outputStream.flush();
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
