package com.server.http.server.bind;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {
    private ClassLoader classLoader;

    public ClassScanner(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

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
                } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                    System.err.println("Could not load class from JAR: " + className);
                }
            }
        }
    }

    public List<Class<?>> findClassesAnnotatedWith(Class<? extends Annotation> annotation)
            throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        String classPath = System.getProperty("java.class.path");
        System.out.println("JAVA CLASS PATH: " + classPath);
        String[] paths = classPath.split(File.pathSeparator);
        System.out.println("Scanning classpath roots...");


        for (String path : paths) {
            File file = new File(path);

            if (file.isDirectory()) {
                Path root = file.toPath().normalize();
                scanDirectory(root, root, classes, annotation);
            } else if (file.isFile() && file.toString().endsWith(".jar")) {
                try (JarFile jarFile = new JarFile(file)) {
                    scanJar(jarFile, classes, annotation);
                }
            }
        }
        System.out.println("Total classes found: " + classes.size());
        return classes;
    }

    private void scanDirectory(Path root, Path path, List<Class<?>> classes, Class<? extends Annotation> annotation) {
        File dir = path.toFile();
        File[] files = dir.listFiles();
        if (files == null) {
            System.err.println("Cannot read directory: " + dir.getAbsolutePath());
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(root, file.toPath(), classes, annotation);
            } else if (file.isFile() && file.getName().endsWith(".class")) {
                String className = getClassName(root, file.toPath());
                try {
                    Class<?> clazz = Class.forName(className, false, classLoader);
                    if (clazz.isAnnotationPresent(annotation)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Failed to load class: " + className);
                }
            }
        }
    }

    private String getClassName(Path root, Path classFilePath) {
        Path relative = root.relativize(classFilePath);

        String className = relative.toString()
                .replace(File.separatorChar, '.')
                .replace(".class", "");

        return className;
    }
}
