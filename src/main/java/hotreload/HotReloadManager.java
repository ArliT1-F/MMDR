package hotreload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * High-level coordinator for MMDR hot reloading.
 *
 * <p>Watches compiled class directories for changes, optionally runs
 * bytecode transformations, and then forwards the result to
 * {@link ClassReloader}.</p>
 */
public final class HotReloadManager implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger("MMDR-HotReload");

    private final FileWatcher fileWatcher;
    private final ClassReloader classReloader;
    private final BytecodeTransformer transformer;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Set<Path> watchedDirs = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private volatile long reloadedClasses = 0L;

    public HotReloadManager(BytecodeTransformer transformer) throws IOException {
        this.classReloader = new ClassReloader();
        this.transformer = transformer != null ? transformer : new BytecodeTransformer();
        this.fileWatcher = new FileWatcher(this::onFileEvent);
    }

    public HotReloadManager() throws IOException {
        this(null);
    }

    /** Add a directory containing compiled .class files to be watched. */
    public void addWatchDirectory(Path dir) throws IOException {
        if (dir == null) return;
        Path normalized = dir.toAbsolutePath().normalize();
        if (watchedDirs.add(normalized)) {
            fileWatcher.watchDirectory(normalized);
        }
    }

    /** Start hot reload monitoring. */
    public void start() {
        if (running.compareAndSet(false, true)) {
            fileWatcher.start();
            LOGGER.info("Hot reload manager started; watching " + watchedDirs.size() + " directories");
        }
    }

    /** Stop hot reload monitoring and release resources. */
    public void stop() {
        running.set(false);
        fileWatcher.stop();
        LOGGER.info("Hot reload manager stopped; total reloaded classes = " + reloadedClasses);
    }

    private void onFileEvent(Path path, java.nio.file.WatchEvent.Kind<?> kind) {
        if (!running.get()) {
            return;
        }
        if (path == null || !path.toString().endsWith(".class")) {
            return; // Ignore non-class files
        }
        if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_DELETE) {
            return; // For now we only care about new/modified classes
        }

        try {
            byte[] bytes = Files.readAllBytes(path);
            String internalName = toInternalClassName(path);
            if (internalName == null) {
                return;
            }
            byte[] transformed = transformer.transform(internalName, bytes);
            classReloader.reloadClass(internalName.replace('/', '.'), transformed);
            reloadedClasses++;
            LOGGER.fine(() -> "Reloaded class: " + internalName + " from " + path);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Failed to read changed class file: " + path, ex);
        } catch (Throwable ex) {
            LOGGER.log(Level.WARNING, "Failed to reload class from: " + path, ex);
        }
    }

    /**
     * Map a class file path under a watched directory to an internal
     * JVM class name (e.g. com/example/MyClass).
     */
    private String toInternalClassName(Path classFile) {
        for (Path root : watchedDirs) {
            if (classFile.startsWith(root)) {
                Path relative = root.relativize(classFile);
                String name = relative.toString().replace('\\', '/');
                if (name.endsWith(".class")) {
                    return name.substring(0, name.length() - ".class".length());
                }
            }
        }
        return null;
    }

    public boolean isRunning() {
        return running.get();
    }

    public long getReloadedClasses() {
        return reloadedClasses;
    }

    public Set<Path> getWatchedDirectories() {
        return Collections.unmodifiableSet(watchedDirs);
    }

    public ClassReloader getClassReloader() {
        return classReloader;
    }

    @Override
    public void close() {
        stop();
    }
}