package com.server.http.server.common;

public class ApplicationParameters {
    private static volatile ApplicationParameters INSTANCE;

    private String fileDirectory;

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

    public void setFileDirectory(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--directory")) {
                fileDirectory = args[i+1];
            }
        }
    }

    public boolean isDirectoryExists() {
        return fileDirectory != null && !fileDirectory.isBlank();
    }
}
