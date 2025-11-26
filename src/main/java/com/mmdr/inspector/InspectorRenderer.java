package com.mmdr.inspector;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/**
 * Rendering utilities for the inspector overlay.
 * 
 * Provides methods for drawing panels, boxes, lines, and other
 * visual elements used by the inspector.
 * 
 * @author MMDR Team
 */
public class InspectorRenderer {
    
    /**
     * Draw a panel with rounded corners (simplified as rectangle for now)
     */
    public void drawPanel(DrawContext context, int x, int y, int width, int height, int color) {
        // Draw background
        context.fill(x, y, x + width, y + height, color);
        
        // Draw border
        int borderColor = 0xFF444444;
        context.fill(x, y, x + width, y + 1, borderColor); // Top
        context.fill(x, y + height - 1, x + width, y + height, borderColor); // Bottom
        context.fill(x, y, x + 1, y + height, borderColor); // Left
        context.fill(x + width - 1, y, x + width, y + height, borderColor); // Right
    }
    
    /**
     * Draw a highlighted box around a block in the world
     */
    public void drawBlockHighlight(MatrixStack matrices, Box box, float red, float green, float blue, float alpha) {
        // This would be called during world rendering
        // Implementation requires access to rendering context
        // Simplified for now
    }
    
    /**
     * Draw a line in 3D space
     */
    public void drawLine3D(MatrixStack matrices, Vec3d from, Vec3d to, float red, float green, float blue, float alpha) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        // Would use immediate mode rendering or buffer building
        // Simplified for now
    }
    
    /**
     * Draw text with a shadow/outline for better visibility
     */
    public void drawTextWithBackground(DrawContext context, String text, int x, int y, int textColor, int backgroundColor) {
        // Draw background
        int textWidth = context.getMatrices().peek().getPositionMatrix().getClass().toString().length() * 6; // Simplified
        context.fill(x - 2, y - 2, x + textWidth + 2, y + 10, backgroundColor);
        
        // Draw text
        context.drawText(null, text, x, y, textColor, true);
    }
    
    /**
     * Draw a progress bar
     */
    public void drawProgressBar(DrawContext context, int x, int y, int width, int height, float progress, int color) {
        // Background
        context.fill(x, y, x + width, y + height, 0xFF222222);
        
        // Progress fill
        int fillWidth = (int) (width * Math.min(1.0f, Math.max(0.0f, progress)));
        context.fill(x, y, x + fillWidth, y + height, color);
        
        // Border
        context.fill(x, y, x + width, y + 1, 0xFF444444);
        context.fill(x, y + height - 1, x + width, y + height, 0xFF444444);
        context.fill(x, y, x + 1, y + height, 0xFF444444);
        context.fill(x + width - 1, y, x + width, y + height, 0xFF444444);
    }
    
    /**
     * Draw a graph (for performance metrics, packet rates, etc.)
     */
    public void drawGraph(DrawContext context, int x, int y, int width, int height, float[] values, int color) {
        if (values == null || values.length == 0) {
            return;
        }
        
        // Background
        context.fill(x, y, x + width, y + height, 0xFF111111);
        
        // Find max value for scaling
        float maxValue = 0;
        for (float value : values) {
            maxValue = Math.max(maxValue, value);
        }
        
        if (maxValue == 0) {
            maxValue = 1;
        }
        
        // Draw lines between points
        int pointSpacing = width / Math.max(1, values.length - 1);
        
        for (int i = 0; i < values.length - 1; i++) {
            int x1 = x + i * pointSpacing;
            int y1 = y + height - (int) ((values[i] / maxValue) * height);
            int x2 = x + (i + 1) * pointSpacing;
            int y2 = y + height - (int) ((values[i + 1] / maxValue) * height);
            
            // Draw a simple line (using fill as approximation)
            context.fill(x1, y1, x2, y2, color);
        }
        
        // Border
        context.fill(x, y, x + width, y + 1, 0xFF444444);
        context.fill(x, y + height - 1, x + width, y + height, 0xFF444444);
        context.fill(x, y, x + 1, y + height, 0xFF444444);
        context.fill(x + width - 1, y, x + width, y + height, 0xFF444444);
    }
}