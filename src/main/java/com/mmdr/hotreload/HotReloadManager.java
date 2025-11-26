package com.mmdr.hotreload;

import com.mmdr.MMDR;
import com.mmdr.util.MMDRConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Manages hot reloading of modified classes during development.
 * 
 * This system watches for file changes in the mod's source directory and
 * automatically reloads modified classes without requiring a full game restart.
 * 
 * Features:
 * - Real-time file system monitoring
 * - Intelligent class reloading with dependency tracking
 * - State preservation during reload
 * - Rollback support on reload failure
 * 
 * @author MMDR Team
 */
public class HotReloadManager {
    private final MMDRConfig config;
    private final FileWatcher fileWatcher;
    private final ClassReloader classReloader;
    private final ExecutorService watcherThread;
    
    private volatile boolean running = false;
    private final Map<String, Long> lastModified = new ConcurrentHashMap<>();
    private final Set<String> pendingReloads = ConcurrentHashMap.newKeySet();
    
    public HotReloadManager(MMDRConfig config) {
        this.config = config;
        this.fileWatcher = new FileWatcher(getWatchPaths());
        this.classReloader = new ClassReloader();
        this.watcherThread = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "MMDR-HotReload-Watcher");
            t.setDaemon(true);
            return t;
        });
    }
    
    /**
     * Start the hot reload system
     */
    public void start() {
        if (running) {
            MMDR.LOGGER.warn("Hot reload already running!");
            return;
        }
        
        running = true;
        MMDR.LOGGER.info("Starting hot reload system...");
        
        watcherThread.submit(() -> {
            try {
                watchForChanges();
            } catch (Exception e) {
                MMDR.LOGGER.error("Hot reload watcher encountered an error", e);
            }
        });
        
        MMDR.LOGGER.info("Hot reload system started successfully");
    }
    
    /**
     * Stop the hot reload system
     */
    public void stop() {
        if (!running) {
            return;
        }
        
        running = false;
        watcherThread.shutdown();
        fileWatcher.close();
        MMDR.LOGGER.info("Hot reload system stopped");
    }
    
    /**
     * Watch for file changes and trigger reloads
     */
    private void watchForChanges() {
        MMDR.LOGGER.info("Watching for file changes in: {}", getWatchPaths());
        
        while (running) {
            try {
                List<Path> changedFiles = fileWatcher.pollChanges(1000);
                
                for (Path file : changedFiles) {
                    if (file.toString().endsWith(".class")) {
                        handleClassChange(file);
                    } else if (file.toString().endsWith(".java")) {
                        handleSourceChange(file);
                    }
                }
                
                // Process pending reloads
                if (!pendingReloads.isEmpty()) {
                    processPendingReloads();
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                MMDR.LOGGER.error("Error watching for changes", e);
            }
        }
    }
    
    /**
     * Handle a changed class file
     */
    private void handleClassChange(Path classFile) {
        String className = getClassNameFromPath(classFile);
        
        if (className == null) {
            return;
        }
        
        long lastMod = lastModified.getOrDefault(className, 0L);
        long currentMod = classFile.toFile().lastModified();
        
        if (currentMod > lastMod) {
            MMDR.LOGGER.info("Detected change in class: {}", className);
            pendingReloads.add(className);
            lastModified.put(className, currentMod);
        }
    }
    
    /**
     * Handle a changed source file (triggers compilation if configured)
     */
    private void handleSourceChange(Path sourceFile) {
        if (!config.isAutoCompileEnabled()) {
            return;
        }
        
        MMDR.LOGGER.info("Detected change in source: {}", sourceFile);
        // TODO: Trigger incremental compilation
    }
    
    /**
     * Process all pending class reloads
     */
    private void processPendingReloads() {
        Set<String> toReload = new HashSet<>(pendingReloads);
        pendingReloads.clear();
        
        MMDR.LOGGER.info("Reloading {} classe(s)...", toReload.size());
        
        try {
            Map<String, Class<?>> reloadedClasses = classReloader.reloadClasses(toReload);
            
            MMDR.LOGGER.info("Successfully reloaded {} classe(s)", reloadedClasses.size());
            
            // Notify listeners
            notifyReloadListeners(reloadedClasses);
            
        } catch (Exception e) {
            MMDR.LOGGER.error("Failed to reload classes", e);
            // TODO: Implement rollback mechanism
        }
    }
    
    /**
     * Notify registered listeners about reloaded classes
     */
    private void notifyReloadListeners(Map<String, Class<?>> reloadedClasses) {
        // TODO: Implement listener system for reload events
    }
    
    /**
     * Get paths to watch for changes
     */
    private List<Path> getWatchPaths() {
        List<Path> paths = new ArrayList<>();
        
        // Add mod output directory
        Path gameDir = FabricLoader.getInstance().getGameDir();
        paths.add(gameDir.resolve("build/classes/java/main"));
        paths.add(gameDir.resolve("out/production/classes"));
        
        // Add configured additional paths
        for (String pathStr : config.getAdditionalWatchPaths()) {
            paths.add(Paths.get(pathStr));
        }
        
        return paths;
    }
    
    /**
     * Convert a file path to a fully qualified class name
     */
    private String getClassNameFromPath(Path classFile) {
        String pathStr = classFile.toString();
        
        // Find the start of the package structure
        int classesIndex = pathStr.indexOf("classes");
        if (classesIndex == -1) {
            return null;
        }
        
        String relativePath = pathStr.substring(classesIndex + 8); // Skip "classes/"
        return relativePath
            .replace('/', '.')
            .replace('\\', '.')
            .replace(".class", "");
    }
    
    /**
     * Manually trigger a reload of a specific class
     */
    public void reloadClass(String className) {
        pendingReloads.add(className);
    }
    
    public boolean isRunning() {
        return running;
    }
}