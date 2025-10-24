package com.mmdr.testing;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single recorded action.
 * 
 * @author MMDR Team
 */
public class RecordedAction {
    private final ActionType type;
    private final String description;
    private final long timestamp;
    private final Map<String, Object> data;
    
    public RecordedAction(ActionType type, String description) {
        this.type = type;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
        this.data = new HashMap<>();
    }
    
    /**
     * Add additional data to this action
     */
    public void addData(String key, Object value) {
        data.put(key, value);
    }
    
    /**
     * Get data by key
     */
    public Object getData(String key) {
        return data.get(key);
    }
    
    /**
     * Get data with default value
     */
    public Object getData(String key, Object defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }
    
    // Getters
    public ActionType getType() { return type; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public Map<String, Object> getAllData() { return new HashMap<>(data); }
}