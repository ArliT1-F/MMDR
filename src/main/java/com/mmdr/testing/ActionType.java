package com.mmdr.testing;

/**
 * Types of actions that can be recorded.
 * 
 * @author MMDR Team
 */
public enum ActionType {
    BLOCK_BREAK("Break Block"),
    BLOCK_PLACE("Place Block"),
    BLOCK_USE("Use Block"),
    ITEM_USE("Use Item"),
    ITEM_DROP("Drop Item"),
    ENTITY_ATTACK("Attack Entity"),
    ENTITY_INTERACT("Interact with Entity"),
    MOVE("Move"),
    ROTATE("Rotate"),
    JUMP("Jump"),
    SNEAK("Sneak"),
    SPRINT("Sprint"),
    HOTBAR_CHANGE("Change Hotbar Slot"),
    INVENTORY_CHANGE("Inventory Change"),
    CHAT_MESSAGE("Chat Message"),
    COMMAND("Execute Command"),
    ASSERTION("Test Assertion"),
    CUSTOM("Custom Action");
    
    private final String displayName;
    
    ActionType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}