package hotreload;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * Lightweight wrapper around {@link java.nio.file.WatchService}.
 *
 * <p>Designed to be cheap to run in the background for hot reload.
 * It watches one or more directories for file changes and notifies
 * a listener callback on changes. Events are coalesced per-path to
 * avoid flooding when editors write temp files.</p>
 */
public final class FileWatcher implements AutoCloseable {

    /** Listener receives the absolute path and the event kind. */
    public interface Listener extends BiConsumer<Path, WatchEvent.Kind<?>> {
        @Override
        void accept(Path path, WatchEvent.Kind<?> kind);
    }

    private final WatchService watchService;
    private final Map<WatchKey, Path> keyToDir = new ConcurrentHashMap<>();
    private final Set<Path> watchedRoots = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Thread workerThread;
    private final Listener listener;

    public FileWatcher(FileSystem fileSystem, Listener listener) throws IOException {
        this.watchService = fileSystem.newWatchService();
        this.listener = listener;
        this.workerThread = new Thread(this::runLoop, "mmdr-file-watcher");
        this.workerThread.setDaemon(true);
    }

    public FileWatcher(Listener listener) throws IOException {
        this(FileSystems.getDefault(), listener);
    }

    /** Register a directory to be watched recursively (one level). */
    public void watchDirectory(Path dir) throws IOException {
        if (dir == null) {
            return;
        }
        Path real = dir.toAbsolutePath().normalize();
        if (watchedRoots.add(real)) {
            WatchKey key = real.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE
            );
            keyToDir.put(key, real);
        }
    }

    /** Start the background watching thread. Safe to call multiple times. */
    public void start() {
        if (running.compareAndSet(false, true)) {
            workerThread.start();
        }
    }

    /** Stop watching and release resources. */
    public void stop() {
        running.set(false);
        try {
            watchService.close();
        } catch (IOException ignored) {
        }
    }

    private void runLoop() {
        try {
            while (running.get()) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (ClosedWatchServiceException e) {
                    break; // closed via stop()
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                Path dir = keyToDir.get(key);
                if (dir != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path child = dir.resolve(ev.context()).toAbsolutePath().normalize();
                        try {
                            listener.accept(child, kind);
                        } catch (Throwable ignored) {
                            // Listener errors should not kill the watcher.
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    keyToDir.remove(key);
                    if (keyToDir.isEmpty()) {
                        break;
                    }
                }
            }
        } finally {
            running.set(false);
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public Set<Path> getWatchedRoots() {
        return Collections.unmodifiableSet(watchedRoots);
    }

    @Override
    public void close() {
        stop();
    }
}