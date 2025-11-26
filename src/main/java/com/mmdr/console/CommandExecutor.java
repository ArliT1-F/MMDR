package com.mmdr.console;

import com.mmdr.MMDR;

import java.util.ArrayList;
import java.util.List;

/**
 * Executes commands and code snippets in the debug console.
 * 
 * Handles both REPL code execution and special console commands.
 * 
 * @author MMDR Team
 */
public class CommandExecutor {
    private final REPLEngine replEngine;
    
    public CommandExecutor(REPLEngine replEngine) {
        this.replEngine = replEngine;
    }
    
    /**
     * Execute input (command or code)
     */
    public Object execute(String input) throws Exception {
        // Check if it's a special command
        if (input.startsWith("/")) {
            return executeCommand(input.substring(1));
        }
        
        // Otherwise, evaluate as code
        return replEngine.evaluate(input);
    }
    
    /**
     * Execute a special console command
     */
    private Object executeCommand(String command) {
        String[] parts = command.split("\\s+");
        String cmd = parts[0].toLowerCase();
        
        switch (cmd) {
            case "clear":
                return "§7Console cleared (not implemented in screen yet)";
            
            case "help":
                return getHelpText();
            
            case "reload":
                if (parts.length > 1) {
                    MMDR.getHotReloadManager().reloadClass(parts[1]);
                    return "§aQueued reload: " + parts[1];
                }
                return "§cUsage: /reload <classname>";
            
            case "vars":
                return listVariables();
            
            case "imports":
                return "§7Available imports (feature coming soon)";
            
            default:
                return "§cUnknown command: " + cmd + " (type /help for commands)";
        }
    }
    
    /**
     * Get help text
     */
    private String getHelpText() {
        return """
            §6MMDR Console Commands:
            §7/clear - Clear console output
            §7/help - Show this help
            §7/reload <class> - Reload a specific class
            §7/vars - List all variables
            §7/imports - Show available imports
            
            §6Available Variables:
            §7mc - MinecraftClient instance
            §7mmdr - MMDR main class
            """;
    }
    
    /**
     * List all variables in the REPL environment
     */
    private String listVariables() {
        StringBuilder sb = new StringBuilder("§6Variables:\n");
        // TODO: Implement variable listing
        sb.append("§7mc = MinecraftClient\n");
        sb.append("§7mmdr = MMDR");
        return sb.toString();
    }
    
    /**
     * Get auto-completion suggestions
     */
    public List<String> getSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        
        // Suggest commands
        if (input.startsWith("/")) {
            String[] commands = {"clear", "help", "reload", "vars", "imports"};
            String partial = input.substring(1).toLowerCase();
            
            for (String cmd : commands) {
                if (cmd.startsWith(partial)) {
                    suggestions.add("/" + cmd);
                }
            }
        }
        
        // TODO: Suggest variable names, method names, etc.
        
        return suggestions;
    }
}