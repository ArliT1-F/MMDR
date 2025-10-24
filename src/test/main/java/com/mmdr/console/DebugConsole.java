package com.mmdr.console;

import com.mmdr.MMDR;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactive debug console with REPL (Read-Eval-Print-Loop) functionality.
 * 
 * Allows developers to execute code, inspect objects, and interact with
 * the game world in real-time without recompiling.
 * 
 * Features:
 * - Execute arbitrary Java/Groovy code
 * - Access to game objects and state
 * - Command history and auto-completion
 * - Multi-line input support
 * 
 * @author MMDR Team
 */
public class DebugConsole {
    private final REPLEngine replEngine;
    private final CommandExecutor commandExecutor;
    private final List<String> commandHistory;
    
    private boolean visible = false;
    private ConsoleScreen currentScreen = null;
    
    public DebugConsole() {
        this.replEngine = new REPLEngine();
        this.commandExecutor = new CommandExecutor(replEngine);
        this.commandHistory = new ArrayList<>();
        
        // Pre-import common packages
        replEngine.addImport("net.minecraft.client.MinecraftClient");
        replEngine.addImport("net.minecraft.entity.player.PlayerEntity");
        replEngine.addImport("net.minecraft.item.ItemStack");
        replEngine.addImport("net.minecraft.util.math.BlockPos");
        
        MMDR.LOGGER.info("Debug Console initialized");
    }
    
    /**
     * Toggle console visibility
     */
    public void toggle(MinecraftClient client) {
        if (visible) {
            hide(client);
        } else {
            show(client);
        }
    }
    
    /**
     * Show the console
     */
    public void show(MinecraftClient client) {
        if (visible) {
            return;
        }
        
        visible = true;
        currentScreen = new ConsoleScreen(this);
        client.setScreen(currentScreen);
        
        MMDR.LOGGER.debug("Console opened");
    }
    
    /**
     * Hide the console
     */
    public void hide(MinecraftClient client) {
        if (!visible) {
            return;
        }
        
        visible = false;
        client.setScreen(null);
        currentScreen = null;
        
        MMDR.LOGGER.debug("Console closed");
    }
    
    /**
     * Execute a command/code snippet
     * 
     * @param input The code to execute
     * @return Result of execution
     */
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        // Add to history
        commandHistory.add(input);
        
        try {
            Object result = commandExecutor.execute(input);
            return formatResult(result);
        } catch (Exception e) {
            return formatError(e);
        }
    }
    
    /**
     * Format execution result for display
     */
    private String formatResult(Object result) {
        if (result == null) {
            return "null";
        }
        
        return result.toString();
    }
    
    /**
     * Format error for display
     */
    private String formatError(Exception e) {
        return "§c[ERROR] " + e.getClass().getSimpleName() + ": " + e.getMessage();
    }
    
    /**
     * Get command history
     */
    public List<String> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }
    
    /**
     * Clear command history
     */
    public void clearHistory() {
        commandHistory.clear();
    }
    
    /**
     * Get auto-completion suggestions for input
     */
    public List<String> getSuggestions(String input) {
        return commandExecutor.getSuggestions(input);
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public REPLEngine getReplEngine() {
        return replEngine;
    }
}