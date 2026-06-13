package com.server.http.server.bind;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.server.http.Main;
import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;

/**
 * Holds and manages all discovered controller handlers (methods annotated with
 * {@link RequestMapping}).
 * <p>
 * This class is a singleton. It scans the classpath for classes annotated with
 * {@link Controller},
 * instantiates them, and collects methods that are valid request handlers.
 * A valid handler method must:
 * <ul>
 * <li>Be annotated with {@link RequestMapping}</li>
 * <li>Accept exactly one parameter of type {@link RequestContext}</li>
 * <li>Return {@link ResponseContext}</li>
 * <li>Have a non-blank path specified in the annotation</li>
 * </ul>
 * </p>
 * <p>
 * Collected handlers are stored as {@link HandlerMethod} objects and can be
 * retrieved
 * for later invocation (e.g., by an HTTP request dispatcher).
 * </p>
 */
public class HandlerHolder {
    private static volatile HandlerHolder INSTANCE;

    private final List<Class<?>> handlerTypeList = new ArrayList<>();

    private final List<HandlerMethod> handlerMethods = new ArrayList<>();

    private final ClassScanner classScanner = new ClassScanner(Main.class.getClassLoader());

    private static final Logger log = Logger.getLogger(HandlerHolder.class.getName());

    /**
     * Private constructor that triggers class scanning and handler collection.
     * <p>
     * Uses {@link ClassScanner} with the class loader of the main application class
     * to find all {@link Controller} classes. Then iterates over their methods
     * to build the list of valid {@link HandlerMethod}s.
     * </p>
     */
    private HandlerHolder() {
        handlerTypeList.addAll(classScanner.findClassesAnnotatedWith(Controller.class));
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
        return Collections.unmodifiableList(handlerMethods);
    }

    /**
     * Instantiates each discovered controller class and collects its methods
     * annotated with {@link RequestMapping} that satisfy the required signature.
     * <p>
     * Methods that do not match the signature or have an invalid path are logged
     * as warnings and skipped. If a controller cannot be instantiated, the whole
     * class is skipped and an error is logged.
     * </p>
     */
    private void collectHandlerMethods() {
        for (Class<?> handlerType : handlerTypeList) {
            Object handlerObject = null;
            try {
                handlerObject = handlerType.getConstructor().newInstance();
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Cannot instantiate controller: " + handlerType.getName(), ex);
                continue;
            }

            for (Method method : handlerType.getDeclaredMethods()) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();

                    if (parameterTypes.length != 1 || parameterTypes[0] != RequestContext.class) {
                        log.warning("Method " + method.getName() + " in " + handlerType.getName() +
                                " has @RequestMapping but does not accept RequestContext – skipped");
                        continue;
                    }

                    if (method.getReturnType() != ResponseContext.class) {
                        log.warning("Method " + method.getName() + " does not return ResponseContext – skipped");
                        continue;
                    }

                    var annotation = method.getAnnotation(RequestMapping.class);
                    if (annotation.path() == null || annotation.path().isBlank()) {
                        log.warning("Skipping method " + method.getName() + " - path is empty");
                        continue;
                    }

                    handlerMethods.add(new HandlerMethod(handlerObject,
                            annotation.method(), annotation.path(), method));
                }
            }
        }
    }
}
