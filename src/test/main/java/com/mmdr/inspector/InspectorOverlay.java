package com.mmdr.inspector;

import com.mmdr.MMDR;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Visual overlay that displays detailed information about blocks, entities, and game state.
 * 
 * Features:
 * - Real-time inspection of blocks and entities under crosshair
 * - NBT data viewer with hierarchical display
 * - Network packet monitor
 * - Event listener visualization
 * - Performance metrics
 * 
 * @author MMDR Team
 */
public class InspectorOverlay {
    private final MinecraftClient client;
    private final InspectorRenderer renderer;
    private final NBTViewer nbtViewer;
    private final PacketMonitor packetMonitor;
    
    private boolean enabled = false;
    private InspectionMode mode = InspectionMode.BASIC;
    private Object currentTarget = null;
    
    // Display options
    private boolean showNBT = true;
    private boolean showPackets = false;
    private boolean showEvents = false;
    private boolean showPerformance = false;
    
    public InspectorOverlay() {
        this.client = MinecraftClient.getInstance();
        this.renderer = new InspectorRenderer();
        this.nbtViewer = new NBTViewer();
        this.packetMonitor = new PacketMonitor();
        
        MMDR.LOGGER.info("Inspector Overlay initialized");
    }
    
    /**
     * Toggle inspector on/off
     */
    public void toggle() {
        enabled = !enabled;
        
        if (enabled) {
            MMDR.LOGGER.info("Inspector enabled - Mode: {}", mode);
            packetMonitor.start();
        } else {
            MMDR.LOGGER.info("Inspector disabled");
            packetMonitor.stop();
        }
    }
    
    /**
     * Render the inspector overlay
     */
    public void render(DrawContext context, float tickDelta) {
        if (!enabled || client.player == null || client.world == null) {
            return;
        }
        
        // Update current target
        updateTarget();
        
        // Render based on mode
        int y = 10;
        
        // Render header
        y = renderHeader(context, y);
        
        // Render target information
        if (currentTarget != null) {
            y = renderTargetInfo(context, y);
            
            if (showNBT) {
                y = renderNBTData(context, y);
            }
        }
        
        // Render packet monitor
        if (showPackets) {
            renderPacketMonitor(context);
        }
        
        // Render event listeners
        if (showEvents) {
            renderEventListeners(context);
        }
        
        // Render performance metrics
        if (showPerformance) {
            renderPerformanceMetrics(context);
        }
    }
    
    /**
     * Update the current inspection target based on what player is looking at
     */
    private void updateTarget() {
        HitResult hitResult = client.crosshairTarget;
        
        if (hitResult == null) {
            currentTarget = null;
            return;
        }
        
        switch (hitResult.getType()) {
            case BLOCK:
                BlockHitResult blockHit = (BlockHitResult) hitResult;
                currentTarget = new BlockTarget(
                    blockHit.getBlockPos(),
                    client.world.getBlockState(blockHit.getBlockPos()),
                    client.world.getBlockEntity(blockHit.getBlockPos())
                );
                break;
                
            case ENTITY:
                EntityHitResult entityHit = (EntityHitResult) hitResult;
                currentTarget = new EntityTarget(entityHit.getEntity());
                break;
                
            default:
                currentTarget = null;
                break;
        }
    }
    
    /**
     * Render the overlay header
     */
    private int renderHeader(DrawContext context, int y) {
        String title = "§6§l[MMDR Inspector] §r§7Mode: " + mode.name();
        context.drawText(client.textRenderer, title, 10, y, 0xFFFFFF, true);
        y += 15;
        
        // Render toggle hints
        String hints = "§7[F3] Toggle Mode | [F4] NBT | [F5] Packets | [F6] Events | [F7] Performance";
        context.drawText(client.textRenderer, hints, 10, y, 0xAAAAAA, false);
        y += 12;
        
        return y + 5;
    }
    
    /**
     * Render information about the current target
     */
    private int renderTargetInfo(DrawContext context, int y) {
        if (currentTarget instanceof BlockTarget) {
            return renderBlockInfo(context, y, (BlockTarget) currentTarget);
        } else if (currentTarget instanceof EntityTarget) {
            return renderEntityInfo(context, y, (EntityTarget) currentTarget);
        }
        
        return y;
    }
    
    /**
     * Render block information
     */
    private int renderBlockInfo(DrawContext context, int y, BlockTarget target) {
        // Draw background panel
        int panelHeight = 100;
        renderer.drawPanel(context, 10, y, 400, panelHeight, 0xDD000000);
        
        int x = 15;
        y += 5;
        
        // Block position
        context.drawText(client.textRenderer, 
            "§ePosition: §f" + formatBlockPos(target.pos), 
            x, y, 0xFFFFFF, false);
        y += 12;
        
        // Block ID
        context.drawText(client.textRenderer,
            "§eBlock: §f" + target.state.getBlock().getTranslationKey(),
            x, y, 0xFFFFFF, false);
        y += 12;
        
        // Block state properties
        context.drawText(client.textRenderer,
            "§eProperties:",
            x, y, 0xFFFFFF, false);
        y += 12;
        
        target.state.getEntries().forEach((property, value) -> {
            String propText = "  §7" + property.getName() + ": §f" + value.toString();
            context.drawText(client.textRenderer, propText, x, y, 0xFFFFFF, false);
        });
        y += target.state.getEntries().size() * 12;
        
        // Block entity info
        if (target.blockEntity != null) {
            context.drawText(client.textRenderer,
                "§eBlock Entity: §f" + target.blockEntity.getClass().getSimpleName(),
                x, y, 0xFFFFFF, false);
            y += 12;
        }
        
        return y + 10;
    }
    
    /**
     * Render entity information
     */
    private int renderEntityInfo(DrawContext context, int y, EntityTarget target) {
        // Draw background panel
        int panelHeight = 120;
        renderer.drawPanel(context, 10, y, 400, panelHeight, 0xDD000000);
        
        int x = 15;
        y += 5;
        
        Entity entity = target.entity;
        
        // Entity type
        context.drawText(client.textRenderer,
            "§eEntity: §f" + entity.getType().getTranslationKey(),
            x, y, 0xFFFFFF, false);
        y += 12;
        
        // Entity ID
        context.drawText(client.textRenderer,
            "§eID: §f" + entity.getId(),
            x, y, 0xFFFFFF, false);
        y += 12;
        
        // Entity UUID
        context.drawText(client.textRenderer,
            "§eUUID: §f" + entity.getUuidAsString(),
            x, y, 0xFFFFFF, false);
        y += 12;
        
        // Position
        context.drawText(client.textRenderer,
            "§ePosition: §f" + String.format("%.2f, %.2f, %.2f", 
                entity.getX(), entity.getY(), entity.getZ()),
            x, y, 0xFFFFFF, false);
        y += 12;
        
        // Velocity
        context.drawText(client.textRenderer,
            "§eVelocity: §f" + String.format("%.2f, %.2f, %.2f",
                entity.getVelocity().x, entity.getVelocity().y, entity.getVelocity().z),
            x, y, 0xFFFFFF, false);
        y += 12;
        
        // Custom name
        if (entity.hasCustomName()) {
            context.drawText(client.textRenderer,
                "§eCustom Name: §f" + entity.getCustomName().getString(),
                x, y, 0xFFFFFF, false);
            y += 12;
        }
        
        // Health (for living entities)
        if (entity instanceof net.minecraft.entity.LivingEntity) {
            net.minecraft.entity.LivingEntity living = (net.minecraft.entity.LivingEntity) entity;
            context.drawText(client.textRenderer,
                "§eHealth: §f" + String.format("%.1f / %.1f",
                    living.getHealth(), living.getMaxHealth()),
                x, y, 0xFFFFFF, false);
            y += 12;
        }
        
        return y + 10;
    }
    
    /**
     * Render NBT data for the current target
     */
    private int renderNBTData(DrawContext context, int y) {
        if (currentTarget == null) {
            return y;
        }
        
        // Draw NBT panel
        renderer.drawPanel(context, 10, y, 500, 200, 0xDD000000);
        
        int x = 15;
        y += 5;
        
        context.drawText(client.textRenderer,
            "§6§lNBT Data:",
            x, y, 0xFFFFFF, false);
        y += 15;
        
        // Get NBT data
        String nbtText = getNBTForTarget();
        
        // Render NBT tree
        List<String> nbtLines = nbtViewer.formatNBT(nbtText);
        
        int maxLines = 15;
        for (int i = 0; i < Math.min(nbtLines.size(), maxLines); i++) {
            context.drawText(client.textRenderer, nbtLines.get(i), x, y, 0xFFFFFF, false);
            y += 10;
        }
        
        if (nbtLines.size() > maxLines) {
            context.drawText(client.textRenderer,
                "§7... (" + (nbtLines.size() - maxLines) + " more lines)",
                x, y, 0xAAAAAA, false);
            y += 10;
        }
        
        return y + 10;
    }
    
    /**
     * Get NBT data for the current target
     */
    private String getNBTForTarget() {
        if (currentTarget instanceof BlockTarget) {
            BlockTarget target = (BlockTarget) currentTarget;
            if (target.blockEntity != null) {
                return nbtViewer.getNBT(target.blockEntity);
            }
        } else if (currentTarget instanceof EntityTarget) {
            EntityTarget target = (EntityTarget) currentTarget;
            return nbtViewer.getNBT(target.entity);
        }
        
        return "{}";
    }
    
    /**
     * Render packet monitor
     */
    private void renderPacketMonitor(DrawContext context) {
        int x = client.getWindow().getScaledWidth() - 310;
        int y = 10;
        
        renderer.drawPanel(context, x, y, 300, 200, 0xDD000000);
        
        x += 5;
        y += 5;
        
        context.drawText(client.textRenderer,
            "§6§lPacket Monitor:",
            x, y, 0xFFFFFF, false);
        y += 15;
        
        // Get recent packets
        List<PacketMonitor.PacketInfo> recentPackets = packetMonitor.getRecentPackets(15);
        
        for (PacketMonitor.PacketInfo packet : recentPackets) {
            String direction = packet.isClientbound() ? "§a←" : "§c→";
            String text = direction + " §7" + packet.getPacketName();
            context.drawText(client.textRenderer, text, x, y, 0xFFFFFF, false);
            y += 10;
        }
        
        // Packet statistics
        y += 5;
        context.drawText(client.textRenderer,
            "§7Total: " + packetMonitor.getTotalPackets() + " | " +
            "Rate: " + packetMonitor.getPacketsPerSecond() + "/s",
            x, y, 0xAAAAAA, false);
    }
    
    /**
     * Render event listeners
     */
    private void renderEventListeners(DrawContext context) {
        int x = 10;
        int y = client.getWindow().getScaledHeight() - 210;
        
        renderer.drawPanel(context, x, y, 400, 200, 0xDD000000);
        
        x += 5;
        y += 5;
        
        context.drawText(client.textRenderer,
            "§6§lActive Event Listeners:",
            x, y, 0xFFFFFF, false);
        y += 15;
        
        // TODO: Implement event listener tracking
        context.drawText(client.textRenderer,
            "§7[Feature coming soon]",
            x, y, 0xAAAAAA, false);
    }
    
    /**
     * Render performance metrics
     */
    private void renderPerformanceMetrics(DrawContext context) {
        int x = client.getWindow().getScaledWidth() - 210;
        int y = client.getWindow().getScaledHeight() - 110;
        
        renderer.drawPanel(context, x, y, 200, 100, 0xDD000000);
        
        x += 5;
        y += 5;
        
        context.drawText(client.textRenderer,
            "§6§lPerformance:",
            x, y, 0xFFFFFF, false);
        y += 15;
        
        // FPS
        int fps = client.getCurrentFps();
        String fpsColor = fps >= 60 ? "§a" : fps >= 30 ? "§e" : "§c";
        context.drawText(client.textRenderer,
            "§7FPS: " + fpsColor + fps,
            x, y, 0xFFFFFF, false);
        y += 12;
        
        // Memory
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        context.drawText(client.textRenderer,
            "§7Memory: §f" + usedMemory + "MB / " + maxMemory + "MB",
            x, y, 0xFFFFFF, false);
        y += 12;
        
        // Loaded chunks
        if (client.world != null) {
            context.drawText(client.textRenderer,
                "§7Chunks: §f" + client.world.getChunkManager().getLoadedChunkCount(),
                x, y, 0xFFFFFF, false);
            y += 12;
        }
        
        // Entity count
        if (client.world != null) {
            int entityCount = client.world.getEntities().size();
            context.drawText(client.textRenderer,
                "§7Entities: §f" + entityCount,
                x, y, 0xFFFFFF, false);
        }
    }
    
    /**
     * Format a block position
     */
    private String formatBlockPos(BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }
    
    /**
     * Cycle through inspection modes
     */
    public void cycleMode() {
        InspectionMode[] modes = InspectionMode.values();
        int currentIndex = mode.ordinal();
        mode = modes[(currentIndex + 1) % modes.length];
        MMDR.LOGGER.info("Inspector mode changed to: {}", mode);
    }
    
    /**
     * Toggle NBT display
     */
    public void toggleNBT() {
        showNBT = !showNBT;
    }
    
    /**
     * Toggle packet monitor
     */
    public void togglePackets() {
        showPackets = !showPackets;
        
        if (showPackets && !packetMonitor.isRunning()) {
            packetMonitor.start();
        } else if (!showPackets && packetMonitor.isRunning()) {
            packetMonitor.stop();
        }
    }
    
    /**
     * Toggle event listener display
     */
    public void toggleEvents() {
        showEvents = !showEvents;
    }
    
    /**
     * Toggle performance metrics
     */
    public void togglePerformance() {
        showPerformance = !showPerformance;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Inspection modes
     */
    public enum InspectionMode {
        BASIC,      // Basic block/entity info
        DETAILED,   // Detailed info with NBT
        ADVANCED    // Everything including packets and events
    }
    
    /**
     * Container for block inspection target
     */
    private static class BlockTarget {
        final BlockPos pos;
        final BlockState state;
        final BlockEntity blockEntity;
        
        BlockTarget(BlockPos pos, BlockState state, BlockEntity blockEntity) {
            this.pos = pos;
            this.state = state;
            this.blockEntity = blockEntity;
        }
    }
    
    /**
     * Container for entity inspection target
     */
    private static class EntityTarget {
        final Entity entity;
        
        EntityTarget(Entity entity) {
            this.entity = entity;
        }
    }
}