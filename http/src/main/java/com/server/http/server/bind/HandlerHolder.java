package com.server.http.server.bind;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.server.http.server.controller.ApplicationController;
import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;

public class HandlerHolder {
    private static volatile HandlerHolder INSTANCE;

    private final List<Class<?>> handlerTypeList = new ArrayList<>();

    private final List<HandlerMethod> handlerMethods = new ArrayList<>();

    private HandlerHolder() {
        handlerTypeList.add(ApplicationController.class);
        collectHandlerMethods();
    }

    public static HandlerHolder getInstance() {
        if (INSTANCE == null) {
            synchronized (HandlerHolder.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HandlerHolder();
                }
            }
        }
        return INSTANCE;
    }

    public List<HandlerMethod> getHandlerMethods() {
        return handlerMethods;
    }

    private void collectHandlerMethods() {
        handlerTypeList.forEach(handlerType -> {
            Method[] methods = handlerType.getDeclaredMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 1 && parameterTypes[0] == RequestContext.class) {
                        if (method.getReturnType() == ResponseContext.class) {
                            var annotation = method.getAnnotation(RequestMapping.class);
                            try {
                                handlerMethods.add(new HandlerMethod(handlerType.getConstructor().newInstance(), annotation.method(), annotation.path(), method));
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                System.out.println("Exception trying to create object for class: " + handlerType.getName());
                            }
                        }
                    }
                }
            }
        });
    }
}
