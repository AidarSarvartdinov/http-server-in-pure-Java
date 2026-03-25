package com.server;

import com.server.http.server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(4221);
        server.start();
    }
}
