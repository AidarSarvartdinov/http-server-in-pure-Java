package com.server.http.server.controller;

import com.server.http.json.JsonObject;
import com.server.http.json.JsonParser;
import com.server.http.json.JsonString;
import com.server.http.server.bind.Controller;
import com.server.http.server.bind.RequestMapping;
import com.server.http.server.common.HttpHeaders;
import com.server.http.server.common.HttpMethod;
import com.server.http.server.common.HttpStatus;
import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;

import java.util.Map;

@Controller
public class JsonController {

    @RequestMapping(path = "/json/echo", method = HttpMethod.POST)
    public ResponseContext echoJson(RequestContext requestContext) {
        String body = requestContext.getBody();
        if (body == null || body.isBlank()) {
            return ResponseContext.build(HttpStatus.BAD_REQUEST);
        }
        JsonParser parser = new JsonParser(body);
        JsonObject jsonObject = parser.parseObject();
        return ResponseContext.build(
                HttpStatus.OK,
                HttpHeaders.fromHeaderMap(Map.of("Content-Type", "application/json")),
                jsonObject.toString()
        );
    }

    @RequestMapping(path = "/json/status", method = HttpMethod.GET)
    public ResponseContext statusJson(RequestContext requestContext) {
        JsonObject status = new JsonObject();
        status.put("status", new JsonString("running"));
        status.put("version", new JsonString("1.0"));
        return ResponseContext.build(
                HttpStatus.OK,
                HttpHeaders.fromHeaderMap(Map.of("Content-Type", "application/json")),
                status.toString()
        );
    }
}