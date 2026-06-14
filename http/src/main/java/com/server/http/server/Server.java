package com.server.http.server;

import com.server.http.server.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple HTTP server that listens on a specified port and handles incoming
 * requests using virtual threads.
 * <p>
 * Each client connection is handed off to a {@link RequestHandler} and executed
 * in a separate virtual thread. The server runs until the JVM terminates;
 * no graceful shutdown is currently implemented.
 * </p>
 */
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
                executorService.execute(new RequestHandler(clientSocket));
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "IOException", e);
        }
    }
}
