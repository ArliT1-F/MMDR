package com.mmdr.inspector;

import com.mmdr.MMDR;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;

/**
 * Viewer for NBT (Named Binary Tag) data.
 * 
 * Formats NBT data into a human-readable hierarchical structure
 * with syntax highlighting and collapsible sections.
 * 
 * @author MMDR Team
 */
public class NBTViewer {
    private static final int MAX_STRING_LENGTH = 100;
    private static final int MAX_DEPTH = 10;
    
    /**
     * Get NBT data from a block entity
     */
    public String getNBT(BlockEntity blockEntity) {
        if (blockEntity == null) {
            return "{}";
        }
        
        try {
            NbtCompound nbt = blockEntity.createNbt();
            return nbt.toString();
        } catch (Exception e) {
            MMDR.LOGGER.error("Error getting NBT from block entity", e);
            return "{error: \"" + e.getMessage() + "\"}";
        }
    }
    
    /**
     * Get NBT data from an entity
     */
    public String getNBT(Entity entity) {
        if (entity == null) {
            return "{}";
        }
        
        try {
            NbtCompound nbt = new NbtCompound();
            entity.writeNbt(nbt);
            return nbt.toString();
        } catch (Exception e) {
            MMDR.LOGGER.error("Error getting NBT from entity", e);
            return "{error: \"" + e.getMessage() + "\"}";
        }
    }
    
    /**
     * Format NBT string into readable lines with indentation and color
     */
    public List<String> formatNBT(String nbtString) {
        List<String> lines = new ArrayList<>();
        
        if (nbtString == null || nbtString.isEmpty()) {
            lines.add("§7<empty>");
            return lines;
        }
        
        try {
            formatNBTRecursive(nbtString, 0, lines);
        } catch (Exception e) {
            lines.add("§c[Error parsing NBT]");
            lines.add("§7" + e.getMessage());
        }
        
        return lines;
    }
    
    /**
     * Recursively format NBT with proper indentation
     */
    private void formatNBTRecursive(String nbt, int depth, List<String> lines) {
        if (depth > MAX_DEPTH) {
            lines.add(getIndent(depth) + "§7...");
            return;
        }
        
        String indent = getIndent(depth);
        
        // Simple formatting (production version would use proper NBT parsing)
        String[] parts = nbt.split(",");
        
        for (String part : parts) {
            part = part.trim();
            
            if (part.isEmpty()) {
                continue;
            }
            
            // Color-code different NBT types
            String formatted = colorizeNBTPart(part);
            lines.add(indent + formatted);
            
            // Limit lines
            if (lines.size() > 100) {
                lines.add(indent + "§7... (truncated)");
                return;
            }
        }
    }
    
    /**
     * Add color coding to NBT parts
     */
    private String colorizeNBTPart(String part) {
        // Keys
        if (part.contains(":")) {
            String[] keyValue = part.split(":", 2);
            String key = keyValue[0].trim();
            String value = keyValue.length > 1 ? keyValue[1].trim() : "";
            
            return "§e" + key + "§7: " + colorizeValue(value);
        }
        
        return "§f" + part;
    }
    
    /**
     * Colorize NBT values based on type
     */
    private String colorizeValue(String value) {
        if (value.startsWith("\"")) {
            // String value
            return "§a" + truncateString(value, MAX_STRING_LENGTH);
        } else if (value.matches("-?\\d+(\\.\\d+)?[bBsSlLfFdD]?")) {
            // Numeric value
            return "§b" + value;
        } else if (value.equals("true") || value.equals("false")) {
            // Boolean
            return "§d" + value;
        } else if (value.startsWith("{") || value.startsWith("[")) {
            // Compound or list
            return "§7" + value;
        }
        
        return "§f" + value;
    }
    
    /**
     * Get indentation string for a given depth
     */
    private String getIndent(int depth) {
        return "  ".repeat(depth);
    }
    
    /**
     * Truncate long strings
     */
    private String truncateString(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Format NBT compound with proper structure
     */
    public List<String> formatNBTCompound(NbtCompound compound) {
        List<String> lines = new ArrayList<>();
        formatCompoundRecursive(compound, 0, lines);
        return lines;
    }
    
    /**
     * Recursively format NBT compound
     */
    private void formatCompoundRecursive(NbtCompound compound, int depth, List<String> lines) {
        if (depth > MAX_DEPTH) {
            lines.add(getIndent(depth) + "§7...");
            return;
        }
        
        String indent = getIndent(depth);
        
        for (String key : compound.getKeys()) {
            NbtElement element = compound.get(key);
            
            if (element instanceof NbtCompound) {
                lines.add(indent + "§e" + key + "§7: {");
                formatCompoundRecursive((NbtCompound) element, depth + 1, lines);
                lines.add(indent + "§7}");
            } else if (element instanceof NbtList) {
                lines.add(indent + "§e" + key + "§7: [");
                NbtList list = (NbtList) element;
                for (int i = 0; i < list.size(); i++) {
                    lines.add(getIndent(depth + 1) + "§7[" + i + "] " + list.get(i).toString());
                }
                lines.add(indent + "§7]");
            } else {
                String value = element != null ? element.toString() : "null";
                lines.add(indent + "§e" + key + "§7: " + colorizeValue(value));
            }
            
            if (lines.size() > 100) {
                lines.add(indent + "§7... (truncated)");
                return;
            }
        }
    }
}