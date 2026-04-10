package com.server.http.server;

import com.server.http.server.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private final int port;

    private final ExecutorService executorService;

    private static final Logger log = Logger.getLogger(Server.class.getName());

    public Server(int port) {
        this.port = port;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void start() {
        log.info("Starting server...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            log.log(Level.INFO, "Server started on port {0}", String.valueOf(port));
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // System.out.println("Accepted new connection");
                executorService.execute(new RequestHandler(clientSocket));
            }
        } catch (IOException e) {
            // System.out.println("IOException: " + e.getMessage());
            log.log(Level.SEVERE, "IOException", e);
        }
    }
}
