package com.mmdr.testing;

import com.mmdr.MMDR;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Test harness that records player actions and generates unit tests.
 * 
 * This system allows developers to:
 * - Record gameplay sessions
 * - Convert actions into automated tests
 * - Generate JUnit test code
 * - Create mock environments for testing
 * - Run tests in isolated environments
 * 
 * Features:
 * - Action recording with timestamps
 * - Smart test generation with assertions
 * - Mock world/player creation
 * - CI/CD integration support
 * 
 * @author MMDR Team
 */
public class TestHarness {
    private final MinecraftClient client;
    private final ActionRecorder recorder;
    private final TestGenerator generator;
    private final MockEnvironment mockEnvironment;
    
    private RecordingSession currentSession = null;
    private final List<RecordingSession> savedSessions;
    
    // Recording state
    private volatile boolean recording = false;
    private Path recordingsDir;
    
    public TestHarness() {
        this.client = MinecraftClient.getInstance();
        this.recorder = new ActionRecorder();
        this.generator = new TestGenerator();
        this.mockEnvironment = new MockEnvironment();
        this.savedSessions = new ArrayList<>();
        
        // Create recordings directory
        try {
            this.recordingsDir = Paths.get("mmdr_recordings");
            Files.createDirectories(recordingsDir);
        } catch (IOException e) {
            MMDR.LOGGER.error("Failed to create recordings directory", e);
            this.recordingsDir = Paths.get(".");
        }
        
        MMDR.LOGGER.info("Test Harness initialized");
    }
    
    /**
     * Toggle recording on/off
     */
    public void toggleRecording() {
        if (recording) {
            stopRecording();
        } else {
            startRecording();
        }
    }
    
    /**
     * Start recording a new session
     */
    public void startRecording() {
        if (recording) {
            MMDR.LOGGER.warn("Already recording!");
            return;
        }
        
        recording = true;
        currentSession = new RecordingSession("session_" + System.currentTimeMillis());
        recorder.startRecording(currentSession);
        
        MMDR.LOGGER.info("Recording started: {}", currentSession.getName());
        
        // Show notification to player
        if (client.player != null) {
            client.player.sendMessage(
                net.minecraft.text.Text.literal("§a[MMDR] Recording started"), 
                true
            );
        }
    }
    
    /**
     * Stop the current recording session
     */
    public void stopRecording() {
        if (!recording) {
            MMDR.LOGGER.warn("Not currently recording!");
            return;
        }
        
        recording = false;
        recorder.stopRecording();
        
        // Save the session
        if (currentSession != null) {
            savedSessions.add(currentSession);
            saveSession(currentSession);
            
            MMDR.LOGGER.info("Recording stopped: {} ({} actions)", 
                currentSession.getName(), 
                currentSession.getActions().size());
            
            // Show notification
            if (client.player != null) {
                client.player.sendMessage(
                    net.minecraft.text.Text.literal(
                        "§a[MMDR] Recording stopped - " + currentSession.getActions().size() + " actions recorded"
                    ), 
                    true
                );
            }
        }
        
        currentSession = null;
    }
    
    /**
     * Save a recording session to disk
     */
    private void saveSession(RecordingSession session) {
        try {
            Path sessionFile = recordingsDir.resolve(session.getName() + ".json");
            String json = session.toJson();
            Files.writeString(sessionFile, json);
            
            MMDR.LOGGER.info("Session saved to: {}", sessionFile);
        } catch (IOException e) {
            MMDR.LOGGER.error("Failed to save recording session", e);
        }
    }
    
    /**
     * Load a recording session from disk
     */
    public RecordingSession loadSession(String name) {
        try {
            Path sessionFile = recordingsDir.resolve(name + ".json");
            
            if (!Files.exists(sessionFile)) {
                MMDR.LOGGER.error("Session file not found: {}", sessionFile);
                return null;
            }
            
            String json = Files.readString(sessionFile);
            RecordingSession session = RecordingSession.fromJson(json);
            
            MMDR.LOGGER.info("Session loaded: {}", name);
            return session;
        } catch (IOException e) {
            MMDR.LOGGER.error("Failed to load recording session", e);
            return null;
        }
    }
    
    /**
     * Generate test code from a recording session
     */
    public String generateTestCode(RecordingSession session, String testClassName) {
        if (session == null) {
            MMDR.LOGGER.error("Cannot generate test from null session");
            return null;
        }
        
        MMDR.LOGGER.info("Generating test code for session: {}", session.getName());
        
        String testCode = generator.generateJUnitTest(session, testClassName);
        
        // Save test file
        try {
            Path testFile = recordingsDir.resolve(testClassName + ".java");
            Files.writeString(testFile, testCode);
            MMDR.LOGGER.info("Test code saved to: {}", testFile);
        } catch (IOException e) {
            MMDR.LOGGER.error("Failed to save test code", e);
        }
        
        return testCode;
    }
    
    /**
     * Generate test code from the current session
     */
    public String generateTestCodeFromCurrent(String testClassName) {
        if (currentSession == null && savedSessions.isEmpty()) {
            MMDR.LOGGER.error("No recording session available");
            return null;
        }
        
        RecordingSession session = currentSession != null ? currentSession : savedSessions.get(savedSessions.size() - 1);
        return generateTestCode(session, testClassName);
    }
    
    /**
     * Replay a recorded session
     */
    public void replaySession(RecordingSession session) {
        if (session == null) {
            MMDR.LOGGER.error("Cannot replay null session");
            return;
        }
        
        MMDR.LOGGER.info("Replaying session: {}", session.getName());
        
        // TODO: Implement replay logic
        // This would involve executing each recorded action in sequence
        // with proper timing
        
        MMDR.LOGGER.warn("Replay functionality not yet implemented");
    }
    
    /**
     * Create a mock environment for testing
     */
    public MockEnvironment.MockWorld createMockWorld() {
        return mockEnvironment.createMockWorld();
    }
    
    /**
     * Export session as Cucumber/Gherkin feature file
     */
    public String exportAsCucumber(RecordingSession session) {
        if (session == null) {
            return null;
        }
        
        return generator.generateCucumberFeature(session);
    }
    
    /**
     * Export session as plain text report
     */
    public String exportAsReport(RecordingSession session) {
        if (session == null) {
            return null;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("Recording Session Report\n");
        report.append("========================\n\n");
        report.append("Name: ").append(session.getName()).append("\n");
        report.append("Duration: ").append(session.getDuration()).append(" ms\n");
        report.append("Actions: ").append(session.getActions().size()).append("\n\n");
        
        report.append("Action Timeline:\n");
        report.append("----------------\n");
        
        for (RecordedAction action : session.getActions()) {
            report.append(String.format("[%6d ms] %s\n", 
                action.getTimestamp() - session.getStartTimestamp(),
                action.getDescription()));
        }
        
        return report.toString();
    }
    
    /**
     * Get all saved sessions
     */
    public List<RecordingSession> getSavedSessions() {
        return new ArrayList<>(savedSessions);
    }
    
    /**
     * Get the current recording session
     */
    public RecordingSession getCurrentSession() {
        return currentSession;
    }
    
    /**
     * Check if currently recording
     */
    public boolean isRecording() {
        return recording;
    }
    
    /**
     * Get the action recorder
     */
    public ActionRecorder getRecorder() {
        return recorder;
    }
    
    /**
     * Get the test generator
     */
    public TestGenerator getGenerator() {
        return generator;
    }
    
    /**
     * Get the mock environment
     */
    public MockEnvironment getMockEnvironment() {
        return mockEnvironment;
    }
    
    /**
     * List all recording files
     */
    public List<String> listRecordings() {
        List<String> recordings = new ArrayList<>();
        
        try {
            Files.list(recordingsDir)
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(path -> recordings.add(path.getFileName().toString().replace(".json", "")));
        } catch (IOException e) {
            MMDR.LOGGER.error("Failed to list recordings", e);
        }
        
        return recordings;
    }
    
    /**
     * Delete a recording
     */
    public boolean deleteRecording(String name) {
        try {
            Path sessionFile = recordingsDir.resolve(name + ".json");
            
            if (Files.exists(sessionFile)) {
                Files.delete(sessionFile);
                MMDR.LOGGER.info("Deleted recording: {}", name);
                return true;
            }
        } catch (IOException e) {
            MMDR.LOGGER.error("Failed to delete recording", e);
        }
        
        return false;
    }
}