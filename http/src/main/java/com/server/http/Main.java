package com.server.http;

import com.server.http.server.Server;
import com.server.http.server.bind.HandlerHolder;
import com.server.http.server.common.ApplicationParameters;

public class Main {
    public static void main(String[] args) {
        ApplicationParameters params = ApplicationParameters.getInstance();
        params.parseArgs(args);

        // Scanning annotated classes
        HandlerHolder.getInstance();

        Server server = new Server(params.getPort());
        server.start();
    }
}
