package com.mmdr.console;

import com.mmdr.MMDR;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

/**
 * REPL (Read-Eval-Print-Loop) engine using Groovy.
 * 
 * Executes code snippets and provides a scripting environment
 * with access to Minecraft game objects.
 * 
 * @author MMDR Team
 */
public class REPLEngine {
    private final Binding binding;
    private final GroovyShell shell;
    private final List<String> imports;
    
    public REPLEngine() {
        this.binding = new Binding();
        this.shell = new GroovyShell(binding);
        this.imports = new ArrayList<>();
        
        // Provide access to common objects
        binding.setProperty("mc", MinecraftClient.getInstance());
        binding.setProperty("mmdr", MMDR.class);
    }
    
    /**
     * Evaluate a code snippet
     * 
     * @param code The code to evaluate
     * @return Result of evaluation
     */
    public Object evaluate(String code) throws Exception {
        // Prepend imports
        StringBuilder fullCode = new StringBuilder();
        for (String imp : imports) {
            fullCode.append("import ").append(imp).append("\n");
        }
        fullCode.append(code);
        
        return shell.evaluate(fullCode.toString());
    }
    
    /**
     * Add an import to the REPL environment
     */
    public void addImport(String importStatement) {
        imports.add(importStatement);
    }
    
    /**
     * Set a variable in the REPL environment
     */
    public void setVariable(String name, Object value) {
        binding.setProperty(name, value);
    }
    
    /**
     * Get a variable from the REPL environment
     */
    public Object getVariable(String name) {
        return binding.getProperty(name);
    }
    
    /**
     * Clear all variables
     */
    public void clearVariables() {
        binding.getVariables().clear();
    }
}