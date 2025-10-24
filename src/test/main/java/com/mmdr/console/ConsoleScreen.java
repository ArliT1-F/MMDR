package com.mmdr.console;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI screen for the debug console.
 * 
 * @author MMDR Team
 */
public class ConsoleScreen extends Screen {
    private final DebugConsole console;
    private final List<String> outputLines;
    
    private TextFieldWidget inputField;
    private int historyIndex = -1;
    
    public ConsoleScreen(DebugConsole console) {
        super(Text.literal("MMDR Debug Console"));
        this.console = console;
        this.outputLines = new ArrayList<>();
        
        // Welcome message
        outputLines.add("§6MMDR Debug Console v1.0.0");
        outputLines.add("§7Type Java/Groovy code to execute. Press ESC to close.");
        outputLines.add("");
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Create input field
        inputField = new TextFieldWidget(
            this.textRenderer,
            10,
            this.height - 25,
            this.width - 20,
            15,
            Text.literal("Input")
        );
        
        inputField.setMaxLength(1000);
        inputField.setFocused(true);
        
        this.addSelectableChild(inputField);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw background
        context.fill(0, 0, this.width, this.height, 0xCC000000);
        
        // Draw output lines
        int y = 10;
        int maxLines = (this.height - 40) / 12;
        int startLine = Math.max(0, outputLines.size() - maxLines);
        
        for (int i = startLine; i < outputLines.size(); i++) {
            context.drawText(this.textRenderer, outputLines.get(i), 10, y, 0xFFFFFF, false);
            y += 12;
        }
        
        // Draw input prompt
        context.drawText(this.textRenderer, "§a> ", 10, this.height - 25, 0xFFFFFF, false);
        
        // Draw input field
        inputField.render(context, mouseX, mouseY, delta);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle Enter key
        if (keyCode == 257) { // ENTER
            executeInput();
            return true;
        }
        
        // Handle Up/Down arrows for history
        if (keyCode == 265) { // UP
            navigateHistory(1);
            return true;
        }
        
        if (keyCode == 264) { // DOWN
            navigateHistory(-1);
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    /**
     * Execute the current input
     */
    private void executeInput() {
        String input = inputField.getText();
        
        if (input.isEmpty()) {
            return;
        }
        
        // Display input
        outputLines.add("§a> §f" + input);
        
        // Execute
        String result = console.execute(input);
        
        // Display result
        if (!result.isEmpty()) {
            for (String line : result.split("\n")) {
                outputLines.add("  " + line);
            }
        }
        
        // Clear input
        inputField.setText("");
        historyIndex = -1;
    }
    
    /**
     * Navigate command history
     */
    private void navigateHistory(int direction) {
        List<String> history = console.getCommandHistory();
        
        if (history.isEmpty()) {
            return;
        }
        
        historyIndex += direction;
        historyIndex = Math.max(-1, Math.min(history.size() - 1, historyIndex));
        
        if (historyIndex >= 0) {
            inputField.setText(history.get(history.size() - 1 - historyIndex));
        } else {
            inputField.setText("");
        }
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}