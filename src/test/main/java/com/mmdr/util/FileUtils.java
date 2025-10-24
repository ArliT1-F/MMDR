package com.mmdr.util;

import com.mmdr.MMDR;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file operations.
 * 
 * @author MMDR Team
 */
public class FileUtils {
    
    /**
     * Ensure a directory exists, creating it if necessary
     */
    public static void ensureDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            MMDR.LOGGER.debug("Created directory: {}", directory);
        }
    }
    
    /**
     * Delete a directory and all its contents
     */
    public static void deleteDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        
        MMDR.LOGGER.debug("Deleted directory: {}", directory);
    }
    
    /**
     * Copy a directory and all its contents
     */
    public static void copyDirectory(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = target.resolve(source.relativize(file));
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
        
        MMDR.LOGGER.debug("Copied directory from {} to {}", source, target);
    }
    
    /**
     * Find all files matching a pattern
     */
    public static List<Path> findFiles(Path directory, String pattern) throws IOException {
        List<Path> result = new ArrayList<>();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (matcher.matches(file.getFileName())) {
                    result.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        
        return result;
    }
    
    /**
     * Get file extension
     */
    public static String getExtension(Path file) {
        String name = file.getFileName().toString();
        int lastDot = name.lastIndexOf('.');
        
        if (lastDot > 0) {
            return name.substring(lastDot + 1);
        }
        
        return "";
    }
    
    /**
     * Get file name without extension
     */
    public static String getNameWithoutExtension(Path file) {
        String name = file.getFileName().toString();
        int lastDot = name.lastIndexOf('.');
        
        if (lastDot > 0) {
            return name.substring(0, lastDot);
        }
        
        return name;
    }
    
    /**
     * Get file size in a human-readable format
     */
    public static String getHumanReadableSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), unit);
    }
    
    /**
     * Read all lines from a file safely
     */
    public static List<String> readAllLinesSafe(Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            MMDR.LOGGER.error("Failed to read file: {}", file, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Write lines to a file safely
     */
    public static boolean writeLinesSafe(Path file, List<String> lines) {
        try {
            Files.write(file, lines);
            return true;
        } catch (IOException e) {
            MMDR.LOGGER.error("Failed to write file: {}", file, e);
            return false;
        }
    }
    
    /**
     * Check if a file is empty
     */
    public static boolean isEmpty(Path file) throws IOException {
        return Files.size(file) == 0;
    }
    
    /**
     * Get the last modified time as a formatted string
     */
    public static String getLastModifiedTime(Path file) throws IOException {
        return Files.getLastModifiedTime(file).toString();
    }
}