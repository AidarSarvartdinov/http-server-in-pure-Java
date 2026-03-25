package com.server.http.server;

import com.server.http.server.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;

    private final ExecutorService executorService;

    public Server(int port) {
        this.port = port;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted new connection");
                executorService.execute(new RequestHandler(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
