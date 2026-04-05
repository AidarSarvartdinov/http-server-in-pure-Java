package com.server.http.server.interceptor;

import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;

public interface Interceptor {
    void beforeSendResponse(RequestContext requestContext, ResponseContext responseContext);
}
