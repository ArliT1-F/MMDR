package hotreload;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple class reloader based on a throwaway {@link ClassLoader}.
 *
 * <p>This is not a JVM-wide redefinition mechanism; it is intended for
 * usage from the MMDR console and hot reload manager where the latest
 * version of a class can be looked up and instantiated via reflection.</p>
 */
public final class ClassReloader {

    private volatile ReloadClassLoader loader;
    private final Map<String, Class<?>> loaded = new ConcurrentHashMap<>();

    public ClassReloader() {
        // Start with the current context class loader as parent
        this.loader = new ReloadClassLoader(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Define or redefine a class from bytecode.
     *
     * @param className  fully qualified class name
     * @param bytecode   class bytes
     * @return the defined Class instance
     */
    public synchronized Class<?> reloadClass(String className, byte[] bytecode) {
        Objects.requireNonNull(className, "className");
        Objects.requireNonNull(bytecode, "bytecode");

        // Create a fresh child loader to avoid stale definitions.
        ReloadClassLoader newLoader = new ReloadClassLoader(loader.getParent());
        Class<?> cls = newLoader.define(className, bytecode);
        this.loader = newLoader;
        loaded.put(className, cls);
        return cls;
    }

    /** Custom ClassLoader that exposes a define helper using protected defineClass. */
    private static final class ReloadClassLoader extends ClassLoader {
        ReloadClassLoader(ClassLoader parent) {
            super(parent);
        }

        Class<?> define(String name, byte[] bytes) {
            return super.defineClass(name, bytes, 0, bytes.length);
        }
    }

    /** Return the most recently loaded version of the class, or null. */
    public Class<?> getLatestClass(String className) {
        return loaded.get(className);
    }

    /** Convenience for creating a new instance via the no-arg constructor. */
    public Object newInstance(String className) throws ReflectiveOperationException {
        Class<?> cls = getLatestClass(className);
        if (cls == null) {
            throw new ClassNotFoundException("Class not reloaded yet: " + className);
        }
        return cls.getDeclaredConstructor().newInstance();
    }
}