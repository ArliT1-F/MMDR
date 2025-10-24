package com.mmdr;

import com.mmdr.console.DebugConsole;
import com.mmdr.hotreload.HotReloadManager;
import com.mmdr.inspector.InspectorOverlay;
import com.mmdr.testing.TestHarness;
import com.mmdr.util.MMDRConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for Minecraft Mod Development Runtime (MMDR)
 * 
 * MMDR provides a comprehensive suite of development tools including:
 * - Hot reload system for instant code changes
 * - Interactive debug console with REPL
 * - Visual inspector for blocks, entities, and packets
 * - Automatic test harness and recording
 * 
 * @author MMDR Team
 * @version 1.0.0
 */
public class MMDR implements ModInitializer {
    public static final String MOD_ID = "mmdr";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    // Core Systems
    private static HotReloadManager hotReloadManager;
    private static DebugConsole debugConsole;
    private static InspectorOverlay inspectorOverlay;
    private static TestHarness testHarness;
    private static MMDRConfig config;
    
    // Keybindings
    private static KeyBinding openConsoleKey;
    private static KeyBinding toggleInspectorKey;
    private static KeyBinding startRecordingKey;
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing MMDR - Minecraft Mod Development Runtime");
        
        // Load configuration
        config = MMDRConfig.load();
        
        // Initialize core systems
        initializeHotReload();
        initializeConsole();
        initializeInspector();
        initializeTestHarness();
        
        // Register keybindings
        registerKeybindings();
        
        // Register event handlers
        registerEventHandlers();
        
        LOGGER.info("MMDR initialization complete!");
    }
    
    /**
     * Initialize the hot reload system
     */
    private void initializeHotReload() {
        LOGGER.info("Initializing Hot Reload Manager...");
        hotReloadManager = new HotReloadManager(config);
        
        if (config.isHotReloadEnabled()) {
            hotReloadManager.start();
            LOGGER.info("Hot Reload is ACTIVE - watching for file changes");
        } else {
            LOGGER.info("Hot Reload is DISABLED - enable in config");
        }
    }
    
    /**
     * Initialize the debug console
     */
    private void initializeConsole() {
        LOGGER.info("Initializing Debug Console...");
        debugConsole = new DebugConsole();
    }
    
    /**
     * Initialize the visual inspector
     */
    private void initializeInspector() {
        LOGGER.info("Initializing Visual Inspector...");
        inspectorOverlay = new InspectorOverlay();
    }
    
    /**
     * Initialize the test harness
     */
    private void initializeTestHarness() {
        LOGGER.info("Initializing Test Harness...");
        testHarness = new TestHarness();
    }
    
    /**
     * Register keybindings for MMDR features
     */
    private void registerKeybindings() {
        openConsoleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.mmdr.open_console",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            "category.mmdr"
        ));
        
        toggleInspectorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.mmdr.toggle_inspector",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F3,
            "category.mmdr"
        ));
        
        startRecordingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.mmdr.start_recording",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F9,
            "category.mmdr"
        ));
    }
    
    /**
     * Register event handlers for client-side events
     */
    private void registerEventHandlers() {
        // Handle key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConsoleKey.wasPressed()) {
                debugConsole.toggle(client);
            }
            
            while (toggleInspectorKey.wasPressed()) {
                inspectorOverlay.toggle();
            }
            
            while (startRecordingKey.wasPressed()) {
                testHarness.toggleRecording();
            }
        });
        
        // Render inspector overlay
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            if (inspectorOverlay.isEnabled()) {
                inspectorOverlay.render(context, tickDelta);
            }
        });
    }
    
    // Getters for accessing MMDR systems
    public static HotReloadManager getHotReloadManager() {
        return hotReloadManager;
    }
    
    public static DebugConsole getDebugConsole() {
        return debugConsole;
    }
    
    public static InspectorOverlay getInspectorOverlay() {
        return inspectorOverlay;
    }
    
    public static TestHarness getTestHarness() {
        return testHarness;
    }
    
    public static MMDRConfig getConfig() {
        return config;
    }
}