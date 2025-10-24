package com.mmdr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for string operations.
 * 
 * @author MMDR Team
 */
public class StringUtils {
    
    /**
     * Check if a string is null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    /**
     * Check if a string is null, empty, or only whitespace
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Truncate a string to a maximum length
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        
        if (str.length() <= maxLength) {
            return str;
        }
        
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Repeat a string n times
     */
    public static String repeat(String str, int count) {
        if (str == null || count <= 0) {
            return "";
        }
        
        return str.repeat(count);
    }
    
    /**
     * Join strings with a delimiter
     */
    public static String join(String delimiter, String... parts) {
        return String.join(delimiter, parts);
    }
    
    /**
     * Join a list of strings with a delimiter
     */
    public static String join(String delimiter, List<String> parts) {
        return String.join(delimiter, parts);
    }
    
    /**
     * Split a string and trim all parts
     */
    public static List<String> splitAndTrim(String str, String delimiter) {
        List<String> result = new ArrayList<>();
        
        for (String part : str.split(Pattern.quote(delimiter))) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        
        return result;
    }
    
    /**
     * Capitalize the first letter of a string
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * Convert camelCase to snake_case
     */
    public static String camelToSnake(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return str.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    /**
     * Convert snake_case to camelCase
     */
    public static String snakeToCamel(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (char c : str.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(c);
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * Pad a string to the left
     */
    public static String padLeft(String str, int length, char padChar) {
        if (str == null) {
            str = "";
        }
        
        if (str.length() >= length) {
            return str;
        }
        
        return repeat(String.valueOf(padChar), length - str.length()) + str;
    }
    
    /**
     * Pad a string to the right
     */
    public static String padRight(String str, int length, char padChar) {
        if (str == null) {
            str = "";
        }
        
        if (str.length() >= length) {
            return str;
        }
        
        return str + repeat(String.valueOf(padChar), length - str.length());
    }
    
    /**
     * Remove Minecraft color codes from a string
     */
    public static String stripColorCodes(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return str.replaceAll("§[0-9a-fk-or]", "");
    }
    
    /**
     * Count occurrences of a substring
     */
    public static int countOccurrences(String str, String substring) {
        if (isEmpty(str) || isEmpty(substring)) {
            return 0;
        }
        
        int count = 0;
        int index = 0;
        
        while ((index = str.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        
        return count;
    }
    
    /**
     * Reverse a string
     */
    public static String reverse(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return new StringBuilder(str).reverse().toString();
    }
    
    /**
     * Check if a string contains only digits
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        
        return str.matches("-?\\d+(\\.\\d+)?");
    }
    
    /**
     * Check if a string is a valid Java identifier
     */
    public static boolean isValidIdentifier(String str) {
        if (isEmpty(str) || !Character.isJavaIdentifierStart(str.charAt(0))) {
            return false;
        }
        
        for (int i = 1; i < str.length(); i++) {
            if (!Character.isJavaIdentifierPart(str.charAt(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Wrap text to a maximum width
     */
    public static List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        
        if (isEmpty(text)) {
            return lines;
        }
        
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxWidth) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
            }
            
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            
            currentLine.append(word);
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines;
    }
}