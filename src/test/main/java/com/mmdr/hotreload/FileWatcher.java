package com.mmdr.hotreload;

import com.mmdr.MMDR;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Watches file system for changes using Java NIO WatchService.
 * 
 * @author MMDR Team
 */
public class FileWatcher {
    private final WatchService watchService;
    private final Map<WatchKey, Path> watchKeys = new HashMap<>();
    
    public FileWatcher(List<Path> pathsToWatch) {
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            
            for (Path path : pathsToWatch) {
                if (Files.exists(path)) {
                    registerPath(path);
                } else {
                    MMDR.LOGGER.warn("Watch path does not exist: {}", path);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize file watcher", e);
        }
    }
    
    /**
     * Register a path for watching
     */
    private void registerPath(Path path) throws IOException {
        WatchKey key = path.register(
            watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE
        );
        
        watchKeys.put(key, path);
        MMDR.LOGGER.debug("Watching: {}", path);
    }
    
    /**
     * Poll for file changes
     * 
     * @param timeoutMs Timeout in milliseconds
     * @return List of changed file paths
     */
    public List<Path> pollChanges(long timeoutMs) throws InterruptedException {
        List<Path> changedPaths = new ArrayList<>();
        
        WatchKey key = watchService.poll(timeoutMs, TimeUnit.MILLISECONDS);
        
        if (key == null) {
            return changedPaths;
        }
        
        Path dir = watchKeys.get(key);
        
        if (dir == null) {
            key.reset();
            return changedPaths;
        }
        
        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();
            
            if (kind == StandardWatchEventKinds.OVERFLOW) {
                continue;
            }
            
            @SuppressWarnings("unchecked")
            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path filename = ev.context();
            Path fullPath = dir.resolve(filename);
            
            changedPaths.add(fullPath);
        }
        
        key.reset();
        
        return changedPaths;
    }
    
    /**
     * Close the file watcher
     */
    public void close() {
        try {
            watchService.close();
        } catch (IOException e) {
            MMDR.LOGGER.error("Error closing file watcher", e);
        }
    }
}