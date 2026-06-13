package com.server.http.server.bind;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Scans the classpath (directories and JAR files) to find all classes annotated with a given annotation.
 * <p>
 * Used during HTTP server initialization to discover components like {@code @Controller}. 
 * Supports both compiled {@code .class} directories and JAR libraries.
 * </p>
 */
public class ClassScanner {
    private final ClassLoader classLoader;

    private static final Logger log = Logger.getLogger(ClassScanner.class.getName());

    /**
     * Constructs a ClassScanner with the specified class loader.
     *
     * @param classLoader the class loader used to load discovered classes.
     *                    If {@code null}, the system class loader will be used.
     */
    public ClassScanner(ClassLoader classLoader) {
        if (classLoader != null) {
            this.classLoader = classLoader;
        } else {
            this.classLoader = ClassLoader.getSystemClassLoader();
        }
        
    }

    /**
     * Finds all classes in the classpath that are marked with the given annotation.
     * <p>
     * Scans every entry listed in the {@code java.class.path} system property:
     * <ul>
     *     <li>Directories – recursively traversed for {@code .class} files.</li>
     *     <li>JAR files – each entry with the {@code .class} extension is inspected.</li>
     * </ul>
     * </p>
     * <p>
     * Classes are loaded without initialization ({@code initialize = false}) to avoid
     * executing static blocks during scanning.
     * </p>
     *
     * @param annotation the annotation class to filter by
     * @return a list of classes annotated with the specified annotation
     */
    public List<Class<?>> findClassesAnnotatedWith(Class<? extends Annotation> annotation) {
        List<Class<?>> classes = new ArrayList<>();
        String classPath = System.getProperty("java.class.path");
        String[] paths = classPath.split(File.pathSeparator);

        for (String path : paths) {
            File file = new File(path);

            if (file.isDirectory()) {
                Path root = file.toPath().normalize();
                try {
                    scanDirectory(root, root, classes, annotation);
                } catch (IOException ex) {
                    log.log(Level.WARNING, "Cannot read directory: " + root.toString());
                }
            } else if (file.isFile() && file.toString().endsWith(".jar")) {
                try (JarFile jarFile = new JarFile(file)) {
                    scanJar(jarFile, classes, annotation);
                } catch (IOException ex) {
                    log.log(Level.WARNING, "Cannot read JAR: " + file.getAbsolutePath(), ex);
                }
            }
        }
        return classes;
    }

    /**
     * Scans a single JAR file and adds annotated classes to the list.
     *
     * @param jarFile    the opened JAR file
     * @param classes    the list to accumulate results
     * @param annotation the target annotation
     */
    private void scanJar(JarFile jarFile, List<Class<?>> classes, Class<? extends Annotation> annotation) {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.endsWith(".class")) {
                String className = entryName.replace('/', '.').replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className, false, classLoader);
                    if (clazz.isAnnotationPresent(annotation)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException ex) {
                    log.log(Level.WARNING, "Could not load class from JAR: " + className, ex);
                }
            }
        }
    }

    /**
     * Recursively walks a directory using {@link Files#walkFileTree} and processes all {@code .class} files.
     *
     * @param root       the root directory of the classpath (used to build full class names)
     * @param path       the current directory being traversed
     * @param classes    the list to accumulate results
     * @param annotation the target annotation
     * @throws IOException if a fatal error occurs during walking
     */
    private void scanDirectory(Path root, Path path, List<Class<?>> classes, Class<? extends Annotation> annotation) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.toString().endsWith(".class")) {
                    String className = getClassName(root, file);
                    loadAndAddClass(className, classes, annotation);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException ex) {
                log.log(Level.WARNING, "Cannot access file: " + file, ex);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Loads a class by name (without initialization) and adds it to the list if the annotation is present.
     *
     * @param className  the fully qualified class name (e.g., "com.example.MyController")
     * @param classes    the list to accumulate results
     * @param annotation the target annotation
     */
    private void loadAndAddClass(String className, List<Class<?>> classes, Class<? extends Annotation> annotation) {
        try {
            Class<?> clazz = Class.forName(className, false, classLoader);
            if (clazz.isAnnotationPresent(annotation)) {
                classes.add(clazz);
            }
        } catch (ClassNotFoundException ex) {
            log.log(Level.WARNING, "Failed to load class: " + className, ex);
        }
    }

    /**
     * Converts a path to a {@code .class} file into a fully qualified class name.
     * <p>
     * Example: if root = /project/classes and classFilePath = /project/classes/com/example/App.class,
     * the result will be "com.example.App".
     * </p>
     *
     * @param root          the normalized root directory of the classpath
     * @param classFilePath the path to the specific {@code .class} file
     * @return the fully qualified class name with dots as package separators
     */
    private String getClassName(Path root, Path classFilePath) {
        Path relative = root.relativize(classFilePath);

        String className = relative.toString()
                .replace(File.separatorChar, '.')
                .replace(".class", "");

        return className;
    }
}
