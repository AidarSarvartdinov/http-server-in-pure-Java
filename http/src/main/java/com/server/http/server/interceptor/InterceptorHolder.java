package com.server.http.server.interceptor;

import java.util.ArrayList;
import java.util.List;

import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;

public class InterceptorHolder {
    private static volatile InterceptorHolder INSTANCE;
    private final List<Interceptor> interceptors;

    private InterceptorHolder() {
        this.interceptors = new ArrayList<>();
        interceptors.add(new EncodeInterceptor());
    }

    public static InterceptorHolder getInstance() {
        if (INSTANCE == null) {
            synchronized (InterceptorHolder.class) {
                if (INSTANCE == null) {
                    INSTANCE = new InterceptorHolder();
                }
            }
        }
        return INSTANCE;
    }

    public void beforeSendResponse(RequestContext requestContext, ResponseContext responseContext) {
        interceptors.forEach(it -> it.beforeSendResponse(requestContext, responseContext));
    }
}
