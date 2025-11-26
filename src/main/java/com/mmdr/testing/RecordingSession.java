package com.mmdr.testing;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a recording session consisting of a timeline of {@link RecordedAction}s.
 */
public class RecordingSession {

    private String name;
    private long startTimestamp;
    private long endTimestamp;
    private List<RecordedAction> actions = new ArrayList<>();

    /**
     * Create a new session with the given name. The start timestamp is captured
     * at construction time.
     */
    public RecordingSession(String name) {
        this.name = name;
        this.startTimestamp = System.currentTimeMillis();
    }

    // For JSON deserialization
    private RecordingSession() {
    }

    public String getName() {
        return name;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    /**
     * Duration in milliseconds between start and last recorded action.
     */
    public long getDuration() {
        long end = endTimestamp == 0L ? System.currentTimeMillis() : endTimestamp;
        return Math.max(0L, end - startTimestamp);
    }

    public List<RecordedAction> getActions() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * Append a new action to this session and update end timestamp.
     */
    public void addAction(RecordedAction action) {
        if (action == null) return;
        actions.add(action);
        endTimestamp = Math.max(endTimestamp, action.getTimestamp());
        if (startTimestamp == 0L) {
            startTimestamp = action.getTimestamp();
        }
    }

    /** Serialize this session (including actions) to JSON using Gson. */
    public String toJson() {
        return new Gson().toJson(this);
    }

    /** Deserialize a session from JSON previously produced by {@link #toJson()}. */
    public static RecordingSession fromJson(String json) {
        return new Gson().fromJson(json, RecordingSession.class);
    }
}

/**
 * Represents a single recorded action within a {@link RecordingSession}.
 */
class RecordedAction {
    private ActionType type;
    private String description;
    private long timestamp;
    private Map<String, Object> data = new HashMap<>();

    // For JSON deserialization
    private RecordedAction() {
    }

    public RecordedAction(ActionType type, String description) {
        this.type = type;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    /** Add additional data to this action. */
    public void addData(String key, Object value) {
        data.put(key, value);
    }

    /** Get data by key. */
    public Object getData(String key) {
        return data.get(key);
    }

    /** Get data with default value. */
    public Object getData(String key, Object defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }

    // Getters for serialization and external usage
    public ActionType getType() { return type; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public Map<String, Object> getAllData() { return new HashMap<>(data); }
}
