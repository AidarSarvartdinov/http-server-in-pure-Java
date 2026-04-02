package com.server.http.server.bind;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.server.http.server.common.HttpMethod;
import com.server.http.server.exception.HandlerException;

public class HandlerMethod {
    private final Object handlerObject;

    private final HttpMethod method;

    private final String path;

    private final Method handler;

    public HandlerMethod(Object handlerObject, HttpMethod method, String path, Method handler) {
        this.handlerObject = handlerObject;
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    @SuppressWarnings("unchecked")
    public <T> T invoke(Object... args) {
        try {
            return (T) handler.invoke(handlerObject, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new HandlerException(e);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }
    
    public String getPath() {
        return path;
    }
}
