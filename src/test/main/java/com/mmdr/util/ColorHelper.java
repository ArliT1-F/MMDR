package com.mmdr.util;

/**
 * Utility class for color manipulation and formatting.
 * 
 * @author MMDR Team
 */
public class ColorHelper {
    
    /**
     * Convert RGB to packed integer color
     */
    public static int rgb(int r, int g, int b) {
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
    
    /**
     * Convert ARGB to packed integer color
     */
    public static int argb(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    /**
     * Extract alpha channel from color
     */
    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }
    
    /**
     * Extract red channel from color
     */
    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }
    
    /**
     * Extract green channel from color
     */
    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }
    
    /**
     * Extract blue channel from color
     */
    public static int getBlue(int color) {
        return color & 0xFF;
    }
    
    /**
     * Interpolate between two colors
     */
    public static int lerp(int color1, int color2, float t) {
        t = Math.max(0, Math.min(1, t));
        
        int a1 = getAlpha(color1);
        int r1 = getRed(color1);
        int g1 = getGreen(color1);
        int b1 = getBlue(color1);
        
        int a2 = getAlpha(color2);
        int r2 = getRed(color2);
        int g2 = getGreen(color2);
        int b2 = getBlue(color2);
        
        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);
        
        return argb(a, r, g, b);
    }
    
    /**
     * Darken a color by a factor
     */
    public static int darken(int color, float factor) {
        factor = Math.max(0, Math.min(1, factor));
        
        int a = getAlpha(color);
        int r = (int) (getRed(color) * (1 - factor));
        int g = (int) (getGreen(color) * (1 - factor));
        int b = (int) (getBlue(color) * (1 - factor));
        
        return argb(a, r, g, b);
    }
    
    /**
     * Lighten a color by a factor
     */
    public static int lighten(int color, float factor) {
        factor = Math.max(0, Math.min(1, factor));
        
        int a = getAlpha(color);
        int r = getRed(color) + (int) ((255 - getRed(color)) * factor);
        int g = getGreen(color) + (int) ((255 - getGreen(color)) * factor);
        int b = getBlue(color) + (int) ((255 - getBlue(color)) * factor);
        
        return argb(a, r, g, b);
    }
    
    /**
     * Set alpha channel of a color
     */
    public static int setAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }
    
    /**
     * Parse hex color string (#RRGGBB or #AARRGGBB)
     */
    public static int parseHex(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        
        if (hex.length() == 6) {
            return 0xFF000000 | Integer.parseInt(hex, 16);
        } else if (hex.length() == 8) {
            return (int) Long.parseLong(hex, 16);
        }
        
        throw new IllegalArgumentException("Invalid hex color: " + hex);
    }
    
    /**
     * Convert color to hex string
     */
    public static String toHex(int color, boolean includeAlpha) {
        if (includeAlpha) {
            return String.format("#%08X", color);
        } else {
            return String.format("#%06X", color & 0xFFFFFF);
        }
    }
    
    /**
     * Get Minecraft formatting code for a color
     */
    public static String getMinecraftColor(MinecraftColor color) {
        return "§" + color.getCode();
    }
    
    /**
     * Minecraft color codes
     */
    public enum MinecraftColor {
        BLACK('0'),
        DARK_BLUE('1'),
        DARK_GREEN('2'),
        DARK_AQUA('3'),
        DARK_RED('4'),
        DARK_PURPLE('5'),
        GOLD('6'),
        GRAY('7'),
        DARK_GRAY('8'),
        BLUE('9'),
        GREEN('a'),
        AQUA('b'),
        RED('c'),
        LIGHT_PURPLE('d'),
        YELLOW('e'),
        WHITE('f');
        
        private final char code;
        
        MinecraftColor(char code) {
            this.code = code;
        }
        
        public char getCode() {
            return code;
        }
        
        public String getFormatCode() {
            return "§" + code;
        }
    }
}