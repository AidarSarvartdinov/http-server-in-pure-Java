package com.server.http.server.controller;

import java.util.Map;

import com.server.http.server.bind.RequestMapping;
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
}
