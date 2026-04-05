package com.server;

import com.server.http.server.Server;
import com.server.http.server.common.ApplicationParameters;

public class Main {
    public static void main(String[] args) {
        ApplicationParameters.getInstance().setFileDirectory(args);
        
        Server server = new Server(4221);
        server.start();
    }
}
