package com.server.http.server.common;

public class ApplicationParameters {
    private static volatile ApplicationParameters INSTANCE;

    private String fileDirectory;
    private int port = 8080;

    private ApplicationParameters() {}

    public static ApplicationParameters getInstance() {
        if (INSTANCE == null) {
            synchronized (ApplicationParameters.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApplicationParameters();
                }
            }
        }
        return INSTANCE;
    }

    public String getFileDirectory() {
        return fileDirectory;
    }

    public void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--directory" -> {
                    if (i + 1 < args.length) {
                        fileDirectory = args[++i];
                    }
                }
                case "--port" -> {
                    if (i + 1 < args.length) {
                        try {
                            port = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException ignored) {
                            // default port
                        }
                    }
                }
            }
        }
    }

    public int getPort() {
        return port;
    }

    public boolean isDirectoryExists() {
        return fileDirectory != null && !fileDirectory.isBlank();
    }
}
