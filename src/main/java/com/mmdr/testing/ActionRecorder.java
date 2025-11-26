package com.mmdr.testing;

import com.mmdr.MMDR;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Records player actions during gameplay.
 * 
 * Captures various types of actions:
 * - Block interactions (break, place, use)
 * - Item usage
 * - Entity interactions
 * - Player movement
 * - Inventory changes
 * - Chat commands
 * 
 * @author MMDR Team
 */
public class ActionRecorder {
    private RecordingSession currentSession;
    private boolean recording = false;
    
    private final MinecraftClient client;
    
    // Last known player state for detecting changes
    private double lastX, lastY, lastZ;
    private float lastYaw, lastPitch;
    private int lastSlot;
    
    public ActionRecorder() {
        this.client = MinecraftClient.getInstance();
        registerEventListeners();
    }
    
    /**
     * Register event listeners for action recording
     */
    private void registerEventListeners() {
        // Block break
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (recording && player == client.player) {
                recordBlockBreak(pos);
            }
            return ActionResult.PASS;
        });
        
        // Block use/place
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (recording && player == client.player) {
                recordBlockUse(hitResult.getBlockPos(), hand);
            }
            return ActionResult.PASS;
        });
        
        // Item use
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (recording && player == client.player) {
                recordItemUse(hand);
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });
        
        // Track continuous actions (movement, etc.)
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }
    
    /**
     * Called every client tick to record continuous actions
     */
    private void onClientTick(MinecraftClient client) {
        if (!recording || client.player == null) {
            return;
        }
        
        PlayerEntity player = client.player;
        
        // Check for movement
        double dx = player.getX() - lastX;
        double dy = player.getY() - lastY;
        double dz = player.getZ() - lastZ;
        
        if (Math.abs(dx) > 0.01 || Math.abs(dy) > 0.01 || Math.abs(dz) > 0.01) {
            recordMovement(player.getX(), player.getY(), player.getZ());
            lastX = player.getX();
            lastY = player.getY();
            lastZ = player.getZ();
        }
        
        // Check for rotation
        if (Math.abs(player.getYaw() - lastYaw) > 0.1 || Math.abs(player.getPitch() - lastPitch) > 0.1) {
            recordRotation(player.getYaw(), player.getPitch());
            lastYaw = player.getYaw();
            lastPitch = player.getPitch();
        }
        
        // Check for hotbar change
        if (player.getInventory().selectedSlot != lastSlot) {
            recordHotbarChange(player.getInventory().selectedSlot);
            lastSlot = player.getInventory().selectedSlot;
        }
    }
    
    /**
     * Start recording actions
     */
    public void startRecording(RecordingSession session) {
        this.currentSession = session;
        this.recording = true;
        
        // Initialize player state
        if (client.player != null) {
            lastX = client.player.getX();
            lastY = client.player.getY();
            lastZ = client.player.getZ();
            lastYaw = client.player.getYaw();
            lastPitch = client.player.getPitch();
            lastSlot = client.player.getInventory().selectedSlot;
        }
        
        MMDR.LOGGER.debug("Action recording started");
    }
    
    /**
     * Stop recording actions
     */
    public void stopRecording() {
        this.recording = false;
        MMDR.LOGGER.debug("Action recording stopped");
    }
    
    /**
     * Record a block break action
     */
    private void recordBlockBreak(BlockPos pos) {
        if (currentSession == null) return;
        
        RecordedAction action = new RecordedAction(
            ActionType.BLOCK_BREAK,
            "Break block at " + formatPos(pos)
        );
        
        action.addData("x", pos.getX());
        action.addData("y", pos.getY());
        action.addData("z", pos.getZ());
        
        if (client.world != null) {
            action.addData("block", client.world.getBlockState(pos).getBlock().getTranslationKey());
        }
        
        currentSession.addAction(action);
    }
    
    /**
     * Record a block use action
     */
    private void recordBlockUse(BlockPos pos, Hand hand) {
        if (currentSession == null) return;
        
        RecordedAction action = new RecordedAction(
            ActionType.BLOCK_USE,
            "Use block at " + formatPos(pos) + " with " + hand
        );
        
        action.addData("x", pos.getX());
        action.addData("y", pos.getY());
        action.addData("z", pos.getZ());
        action.addData("hand", hand.toString());
        
        if (client.world != null) {
            action.addData("block", client.world.getBlockState(pos).getBlock().getTranslationKey());
        }
        
        currentSession.addAction(action);
    }
    
    /**
     * Record an item use action
     */
    private void recordItemUse(Hand hand) {
        if (currentSession == null || client.player == null) return;
        
        RecordedAction action = new RecordedAction(
            ActionType.ITEM_USE,
            "Use item in " + hand
        );
        
        action.addData("hand", hand.toString());
        
        var itemStack = client.player.getStackInHand(hand);
        if (!itemStack.isEmpty()) {
            action.addData("item", itemStack.getItem().getTranslationKey());
            action.addData("count", itemStack.getCount());
        }
        
        currentSession.addAction(action);
    }
    
    /**
     * Record player movement
     */
    private void recordMovement(double x, double y, double z) {
        if (currentSession == null) return;
        
        // Only record significant movements to avoid spam
        RecordedAction action = new RecordedAction(
            ActionType.MOVE,
            String.format("Move to %.2f, %.2f, %.2f", x, y, z)
        );
        
        action.addData("x", x);
        action.addData("y", y);
        action.addData("z", z);
        
        currentSession.addAction(action);
    }
    
    /**
     * Record player rotation
     */
    private void recordRotation(float yaw, float pitch) {
        if (currentSession == null) return;
        
        RecordedAction action = new RecordedAction(
            ActionType.ROTATE,
            String.format("Rotate to yaw=%.1f, pitch=%.1f", yaw, pitch)
        );
        
        action.addData("yaw", yaw);
        action.addData("pitch", pitch);
        
        currentSession.addAction(action);
    }
    
    /**
     * Record hotbar slot change
     */
    private void recordHotbarChange(int slot) {
        if (currentSession == null) return;
        
        RecordedAction action = new RecordedAction(
            ActionType.HOTBAR_CHANGE,
            "Switch to hotbar slot " + slot
        );
        
        action.addData("slot", slot);
        
        currentSession.addAction(action);
    }
    
    /**
     * Record a custom action (for manual recording)
     */
    public void recordCustomAction(String type, String description) {
        if (currentSession == null) return;
        
        RecordedAction action = new RecordedAction(
            ActionType.CUSTOM,
            description
        );
        
        action.addData("customType", type);
        
        currentSession.addAction(action);
    }
    
    /**
     * Record an assertion point (for test generation)
     */
    public void recordAssertion(String assertionType, Object expectedValue) {
        if (currentSession == null) return;
        
        RecordedAction action = new RecordedAction(
            ActionType.ASSERTION,
            "Assert: " + assertionType
        );
        
        action.addData("assertionType", assertionType);
        action.addData("expectedValue", expectedValue.toString());
        
        currentSession.addAction(action);
    }
    
    /**
     * Format a block position
     */
    private String formatPos(BlockPos pos) {
        return String.format("(%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ());
    }
    
    public boolean isRecording() {
        return recording;
    }
}