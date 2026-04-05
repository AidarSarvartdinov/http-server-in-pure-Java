package com.server.http.server.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.server.http.server.bind.RequestMapping;
import com.server.http.server.common.ApplicationParameters;
import com.server.http.server.common.HttpHeaders;
import com.server.http.server.common.HttpMethod;
import com.server.http.server.common.HttpStatus;
import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;

public class ApplicationController {

    @RequestMapping(path = "/", method = HttpMethod.GET)
    public ResponseContext simpleOk(RequestContext requestContext) {
        return ResponseContext.build(HttpStatus.OK);
    }

    @RequestMapping(path = "/echo/{command}", method = HttpMethod.GET)
    public ResponseContext echo(RequestContext requestContext) {
        var responseBody = requestContext.getLastPart();

        return ResponseContext.build(
                HttpStatus.OK,
                HttpHeaders.fromHeaderMap(Map.of("Content-Type", "text/plain",
                        "Content-Length", String.valueOf(responseBody.getBytes().length))),
                responseBody);
    }

    @RequestMapping(path = "/user-agent", method = HttpMethod.GET)
    public ResponseContext userAgent(RequestContext requestContext) {
        String responseBody = requestContext.getHeaders().getFirst("User-Agent");

        return ResponseContext.build(
                HttpStatus.OK,
                HttpHeaders.fromHeaderMap(Map.of("Content-Type", "text/plain",
                        "Content-Length", String.valueOf(responseBody.getBytes().length))),
                responseBody);
    }

    @RequestMapping(path = "/files/{file}", method = HttpMethod.GET)
    public ResponseContext readFileByName(RequestContext context) {
        if (!ApplicationParameters.getInstance().isDirectoryExists()) {
            return ResponseContext.build(HttpStatus.NOT_FOUND);
        }

        String directory = ApplicationParameters.getInstance().getFileDirectory();
        directory = directory.endsWith("/") ? directory : directory + "/";
        String fileName = context.getLastPart();
        File file = new File(String.format("%s%s", directory, fileName));
        
        if (!file.exists() || !file.isFile()) {
            return ResponseContext.build(HttpStatus.NOT_FOUND);
        }

        var fileContent = readFile(file);

        return ResponseContext.build(
            HttpStatus.OK,
            HttpHeaders.fromHeaderMap(Map.of("Content-Type", "application/octet-stream",
                "Content-Length", String.valueOf(fileContent.getBytes().length)
            )),
            fileContent
        );
    }

    @RequestMapping(path = "/files/{file}", method = HttpMethod.POST)
    public ResponseContext saveFile(RequestContext context) {
        if (!ApplicationParameters.getInstance().isDirectoryExists()) {
            return ResponseContext.build(HttpStatus.NOT_FOUND);
        }

        String directory = ApplicationParameters.getInstance().getFileDirectory();
        directory = directory.endsWith("/") ? directory : directory + "/";
        String fileName = context.getLastPart();
        saveFile(String.format("%s%s", directory, fileName), context.getBody());

        return ResponseContext.build(HttpStatus.CREATED);
    }

    private String readFile(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fileInputStream.read(data);
            return new String(data);
        } catch(IOException ex) {
            throw new RuntimeException("Exception trying to read file", ex);
        }
    }

    private void saveFile(String fullPath, String content) {
        try {
            Path path = Paths.get(fullPath);

            if (Files.exists(path)) {
                Files.delete(path);
            }

            Files.write(path, content.getBytes());
        } catch (IOException ex) {
            throw new RuntimeException("Exception trying to save file", ex);
        }
    }
}
