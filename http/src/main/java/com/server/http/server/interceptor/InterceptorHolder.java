package com.server.http.server.interceptor;

import java.util.ArrayList;
import java.util.List;

import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;

/**
 * Holds and manages all registered response interceptors.
 * <p>
 * Interceptors are called before sending the HTTP response, allowing modification
 * of the response (e.g., compression, header manipulation).
 * </p>
 * <p>
 * This class is a thread‑safe singleton. By default, it registers an
 * {@link EncodeInterceptor} for gzip compression.
 * </p>
 */
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

    /**
     * Executes all registered interceptors' {@code beforeSendResponse} methods.
     *
     * @param requestContext  the original request context
     * @param responseContext the response context to be modified (may be null-safe)
     */
    public void beforeSendResponse(RequestContext requestContext, ResponseContext responseContext) {
        interceptors.forEach(it -> it.beforeSendResponse(requestContext, responseContext));
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }
}
