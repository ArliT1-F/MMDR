package com.mmdr.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mmdr.MMDR;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration manager for MMDR.
 * 
 * Handles loading, saving, and accessing configuration values.
 * Configuration is stored in JSON format in the game directory.
 * 
 * @author MMDR Team
 */
public class MMDRConfig {
    private static final String CONFIG_FILE = "mmdr-config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    // Hot Reload Settings
    private boolean hotReloadEnabled = true;
    private boolean autoCompileEnabled = false;
    private int fileWatchInterval = 1000; // milliseconds
    private List<String> additionalWatchPaths = new ArrayList<>();
    private List<String> excludedPackages = new ArrayList<>();
    
    // Console Settings
    private boolean consoleEnabled = true;
    private int consoleMaxHistory = 100;
    private boolean consoleAutoComplete = true;
    private List<String> consoleAutoImports = new ArrayList<>();
    
    // Inspector Settings
    private boolean inspectorEnabled = true;
    private boolean inspectorShowNBT = true;
    private boolean inspectorShowPackets = false;
    private boolean inspectorShowEvents = false;
    private boolean inspectorShowPerformance = true;
    private int inspectorMaxPackets = 1000;
    
    // Test Harness Settings
    private boolean testHarnessEnabled = true;
    private String testOutputDirectory = "mmdr_tests";
    private String recordingsDirectory = "mmdr_recordings";
    private boolean autoGenerateTests = false;
    private String defaultTestFramework = "junit5"; // junit5, testng, cucumber
    
    // Performance Settings
    private int maxReloadThreads = 2;
    private boolean debugLogging = false;
    private int maxMemoryMB = 512;
    
    // UI Settings
    private int overlayScale = 100; // percentage
    private boolean enableNotifications = true;
    private String theme = "dark"; // dark, light, custom
    
    /**
     * Load configuration from file or create default
     */
    public static MMDRConfig load() {
        Path configPath = getConfigPath();
        
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                MMDRConfig config = GSON.fromJson(json, MMDRConfig.class);
                MMDR.LOGGER.info("Configuration loaded from {}", configPath);
                return config;
            } catch (IOException e) {
                MMDR.LOGGER.error("Failed to load configuration, using defaults", e);
                return createDefault();
            }
        } else {
            MMDR.LOGGER.info("No configuration file found, creating default");
            MMDRConfig config = createDefault();
            config.save();
            return config;
        }
    }
    
    /**
     * Create default configuration
     */
    private static MMDRConfig createDefault() {
        MMDRConfig config = new MMDRConfig();
        
        // Add default auto-imports for console
        config.consoleAutoImports.add("net.minecraft.client.MinecraftClient");
        config.consoleAutoImports.add("net.minecraft.block.Blocks");
        config.consoleAutoImports.add("net.minecraft.item.Items");
        config.consoleAutoImports.add("net.minecraft.util.math.BlockPos");
        config.consoleAutoImports.add("net.minecraft.text.Text");
        
        // Add default excluded packages for hot reload
        config.excludedPackages.add("java.*");
        config.excludedPackages.add("javax.*");
        config.excludedPackages.add("sun.*");
        config.excludedPackages.add("net.minecraft.*");
        
        return config;
    }
    
    /**
     * Save configuration to file
     */
    public void save() {
        Path configPath = getConfigPath();
        
        try {
            String json = GSON.toJson(this);
            Files.writeString(configPath, json);
            MMDR.LOGGER.info("Configuration saved to {}", configPath);
        } catch (IOException e) {
            MMDR.LOGGER.error("Failed to save configuration", e);
        }
    }
    
    /**
     * Get the configuration file path
     */
    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
    }
    
    /**
     * Reload configuration from file
     */
    public void reload() {
        MMDRConfig newConfig = load();
        copyFrom(newConfig);
        MMDR.LOGGER.info("Configuration reloaded");
    }
    
    /**
     * Copy values from another config
     */
    private void copyFrom(MMDRConfig other) {
        this.hotReloadEnabled = other.hotReloadEnabled;
        this.autoCompileEnabled = other.autoCompileEnabled;
        this.fileWatchInterval = other.fileWatchInterval;
        this.additionalWatchPaths = new ArrayList<>(other.additionalWatchPaths);
        this.excludedPackages = new ArrayList<>(other.excludedPackages);
        
        this.consoleEnabled = other.consoleEnabled;
        this.consoleMaxHistory = other.consoleMaxHistory;
        this.consoleAutoComplete = other.consoleAutoComplete;
        this.consoleAutoImports = new ArrayList<>(other.consoleAutoImports);
        
        this.inspectorEnabled = other.inspectorEnabled;
        this.inspectorShowNBT = other.inspectorShowNBT;
        this.inspectorShowPackets = other.inspectorShowPackets;
        this.inspectorShowEvents = other.inspectorShowEvents;
        this.inspectorShowPerformance = other.inspectorShowPerformance;
        this.inspectorMaxPackets = other.inspectorMaxPackets;
        
        this.testHarnessEnabled = other.testHarnessEnabled;
        this.testOutputDirectory = other.testOutputDirectory;
        this.recordingsDirectory = other.recordingsDirectory;
        this.autoGenerateTests = other.autoGenerateTests;
        this.defaultTestFramework = other.defaultTestFramework;
        
        this.maxReloadThreads = other.maxReloadThreads;
        this.debugLogging = other.debugLogging;
        this.maxMemoryMB = other.maxMemoryMB;
        
        this.overlayScale = other.overlayScale;
        this.enableNotifications = other.enableNotifications;
        this.theme = other.theme;
    }
    
    /**
     * Export configuration as JSON string
     */
    public String toJson() {
        return GSON.toJson(this);
    }
    
    /**
     * Validate configuration values
     */
    public boolean validate() {
        boolean valid = true;
        
        if (fileWatchInterval < 100) {
            MMDR.LOGGER.warn("fileWatchInterval too low, setting to 100ms");
            fileWatchInterval = 100;
            valid = false;
        }
        
        if (consoleMaxHistory < 10) {
            MMDR.LOGGER.warn("consoleMaxHistory too low, setting to 10");
            consoleMaxHistory = 10;
            valid = false;
        }
        
        if (maxReloadThreads < 1) {
            MMDR.LOGGER.warn("maxReloadThreads too low, setting to 1");
            maxReloadThreads = 1;
            valid = false;
        }
        
        if (overlayScale < 50 || overlayScale > 200) {
            MMDR.LOGGER.warn("overlayScale out of range, setting to 100");
            overlayScale = 100;
            valid = false;
        }
        
        return valid;
    }
    
    // ===== Hot Reload Getters/Setters =====
    
    public boolean isHotReloadEnabled() {
        return hotReloadEnabled;
    }
    
    public void setHotReloadEnabled(boolean hotReloadEnabled) {
        this.hotReloadEnabled = hotReloadEnabled;
    }
    
    public boolean isAutoCompileEnabled() {
        return autoCompileEnabled;
    }
    
    public void setAutoCompileEnabled(boolean autoCompileEnabled) {
        this.autoCompileEnabled = autoCompileEnabled;
    }
    
    public int getFileWatchInterval() {
        return fileWatchInterval;
    }
    
    public void setFileWatchInterval(int fileWatchInterval) {
        this.fileWatchInterval = fileWatchInterval;
    }
    
    public List<String> getAdditionalWatchPaths() {
        return new ArrayList<>(additionalWatchPaths);
    }
    
    public void addWatchPath(String path) {
        if (!additionalWatchPaths.contains(path)) {
            additionalWatchPaths.add(path);
        }
    }
    
    public void removeWatchPath(String path) {
        additionalWatchPaths.remove(path);
    }
    
    public List<String> getExcludedPackages() {
        return new ArrayList<>(excludedPackages);
    }
    
    public void addExcludedPackage(String packageName) {
        if (!excludedPackages.contains(packageName)) {
            excludedPackages.add(packageName);
        }
    }
    
    public boolean isPackageExcluded(String className) {
        for (String excluded : excludedPackages) {
            String pattern = excluded.replace("*", ".*");
            if (className.matches(pattern)) {
                return true;
            }
        }
        return false;
    }
    
    // ===== Console Getters/Setters =====
    
    public boolean isConsoleEnabled() {
        return consoleEnabled;
    }
    
    public void setConsoleEnabled(boolean consoleEnabled) {
        this.consoleEnabled = consoleEnabled;
    }
    
    public int getConsoleMaxHistory() {
        return consoleMaxHistory;
    }
    
    public void setConsoleMaxHistory(int consoleMaxHistory) {
        this.consoleMaxHistory = consoleMaxHistory;
    }
    
    public boolean isConsoleAutoComplete() {
        return consoleAutoComplete;
    }
    
    public void setConsoleAutoComplete(boolean consoleAutoComplete) {
        this.consoleAutoComplete = consoleAutoComplete;
    }
    
    public List<String> getConsoleAutoImports() {
        return new ArrayList<>(consoleAutoImports);
    }
    
    public void addConsoleAutoImport(String importStatement) {
        if (!consoleAutoImports.contains(importStatement)) {
            consoleAutoImports.add(importStatement);
        }
    }
    
    // ===== Inspector Getters/Setters =====
    
    public boolean isInspectorEnabled() {
        return inspectorEnabled;
    }
    
    public void setInspectorEnabled(boolean inspectorEnabled) {
        this.inspectorEnabled = inspectorEnabled;
    }
    
    public boolean isInspectorShowNBT() {
        return inspectorShowNBT;
    }
    
    public void setInspectorShowNBT(boolean inspectorShowNBT) {
        this.inspectorShowNBT = inspectorShowNBT;
    }
    
    public boolean isInspectorShowPackets() {
        return inspectorShowPackets;
    }
    
    public void setInspectorShowPackets(boolean inspectorShowPackets) {
        this.inspectorShowPackets = inspectorShowPackets;
    }
    
    public boolean isInspectorShowEvents() {
        return inspectorShowEvents;
    }
    
    public void setInspectorShowEvents(boolean inspectorShowEvents) {
        this.inspectorShowEvents = inspectorShowEvents;
    }
    
    public boolean isInspectorShowPerformance() {
        return inspectorShowPerformance;
    }
    
    public void setInspectorShowPerformance(boolean inspectorShowPerformance) {
        this.inspectorShowPerformance = inspectorShowPerformance;
    }
    
    public int getInspectorMaxPackets() {
        return inspectorMaxPackets;
    }
    
    public void setInspectorMaxPackets(int inspectorMaxPackets) {
        this.inspectorMaxPackets = inspectorMaxPackets;
    }
    
    // ===== Test Harness Getters/Setters =====
    
    public boolean isTestHarnessEnabled() {
        return testHarnessEnabled;
    }
    
    public void setTestHarnessEnabled(boolean testHarnessEnabled) {
        this.testHarnessEnabled = testHarnessEnabled;
    }
    
    public String getTestOutputDirectory() {
        return testOutputDirectory;
    }
    
    public void setTestOutputDirectory(String testOutputDirectory) {
        this.testOutputDirectory = testOutputDirectory;
    }
    
    public String getRecordingsDirectory() {
        return recordingsDirectory;
    }
    
    public void setRecordingsDirectory(String recordingsDirectory) {
        this.recordingsDirectory = recordingsDirectory;
    }
    
    public boolean isAutoGenerateTests() {
        return autoGenerateTests;
    }
    
    public void setAutoGenerateTests(boolean autoGenerateTests) {
        this.autoGenerateTests = autoGenerateTests;
    }
    
    public String getDefaultTestFramework() {
        return defaultTestFramework;
    }
    
    public void setDefaultTestFramework(String defaultTestFramework) {
        this.defaultTestFramework = defaultTestFramework;
    }
    
    // ===== Performance Getters/Setters =====
    
    public int getMaxReloadThreads() {
        return maxReloadThreads;
    }
    
    public void setMaxReloadThreads(int maxReloadThreads) {
        this.maxReloadThreads = maxReloadThreads;
    }
    
    public boolean isDebugLogging() {
        return debugLogging;
    }
    
    public void setDebugLogging(boolean debugLogging) {
        this.debugLogging = debugLogging;
    }
    
    public int getMaxMemoryMB() {
        return maxMemoryMB;
    }
    
    public void setMaxMemoryMB(int maxMemoryMB) {
        this.maxMemoryMB = maxMemoryMB;
    }
    
    // ===== UI Getters/Setters =====
    
    public int getOverlayScale() {
        return overlayScale;
    }
    
    public void setOverlayScale(int overlayScale) {
        this.overlayScale = overlayScale;
    }
    
    public boolean isEnableNotifications() {
        return enableNotifications;
    }
    
    public void setEnableNotifications(boolean enableNotifications) {
        this.enableNotifications = enableNotifications;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
}