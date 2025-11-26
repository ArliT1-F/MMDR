package com.mmdr.testing;

import com.mmdr.MMDR;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates mock Minecraft environments for testing.
 * 
 * Provides lightweight mocks of:
 * - World
 * - Player
 * - Entities
 * - Block states
 * 
 * This allows tests to run without a full Minecraft instance.
 * 
 * @author MMDR Team
 */
public class MockEnvironment {
    
    /**
     * Create a mock world for testing
     */
    public MockWorld createMockWorld() {
        return new MockWorld();
    }
    
    /**
     * Create a mock player for testing
     */
    public MockPlayer createMockPlayer(MockWorld world) {
        return new MockPlayer(world);
    }
    
    /**
     * Mock World implementation
     */
    public static class MockWorld {
        private final Map<BlockPos, BlockState> blocks;
        private final Map<BlockPos, Object> blockEntities;
        
        public MockWorld() {
            this.blocks = new HashMap<>();
            this.blockEntities = new HashMap<>();
            
            // Initialize with air blocks
            initializeWorld();
        }
        
        /**
         * Initialize the world with default blocks
         */
        private void initializeWorld() {
            // Create a simple flat world for testing
            for (int x = -16; x <= 16; x++) {
                for (int z = -16; z <= 16; z++) {
                    // Bedrock layer
                    setBlockState(new BlockPos(x, 0, z), Blocks.BEDROCK.getDefaultState());
                    
                    // Stone layers
                    for (int y = 1; y <= 3; y++) {
                        setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                    }
                    
                    // Grass layer
                    setBlockState(new BlockPos(x, 4, z), Blocks.GRASS_BLOCK.getDefaultState());
                }
            }
        }
        
        /**
         * Get block state at position
         */
        public BlockState getBlockState(BlockPos pos) {
            return blocks.getOrDefault(pos, Blocks.AIR.getDefaultState());
        }
        
        /**
         * Set block state at position
         */
        public void setBlockState(BlockPos pos, BlockState state) {
            blocks.put(pos, state);
        }
        
        /**
         * Break block at position
         */
        public boolean breakBlock(BlockPos pos) {
            if (blocks.containsKey(pos)) {
                blocks.put(pos, Blocks.AIR.getDefaultState());
                blockEntities.remove(pos);
                return true;
            }
            return false;
        }
        
        /**
         * Get block entity at position
         */
        public Object getBlockEntity(BlockPos pos) {
            return blockEntities.get(pos);
        }
        
        /**
         * Cleanup resources
         */
        public void cleanup() {
            blocks.clear();
            blockEntities.clear();
        }
        
        /**
         * Get world statistics
         */
        public int getBlockCount() {
            return blocks.size();
        }
    }
    
    /**
     * Mock Player implementation
     */
    public static class MockPlayer {
        private final MockWorld world;
        private double x, y, z;
        private float yaw, pitch;
        private int selectedSlot;
        
        public MockPlayer(MockWorld world) {
            this.world = world;
            this.x = 0;
            this.y = 5;
            this.z = 0;
            this.yaw = 0;
            this.pitch = 0;
            this.selectedSlot = 0;
        }
        
        /**
         * Set player position
         */
        public void setPosition(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        /**
         * Set player rotation
         */
        public void setRotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }
        
        /**
         * Break a block
         */
        public boolean breakBlock(BlockPos pos) {
            return world.breakBlock(pos);
        }
        
        /**
         * Place a block
         */
        public boolean placeBlock(BlockPos pos, net.minecraft.block.Block block) {
            world.setBlockState(pos, block.getDefaultState());
            return true;
        }
        
        /**
         * Select hotbar slot
         */
        public void selectSlot(int slot) {
            this.selectedSlot = Math.max(0, Math.min(8, slot));
        }
        
        // Getters
        public double getX() { return x; }
        public double getY() { return y; }
        public double getZ() { return z; }
        public float getYaw() { return yaw; }
        public float getPitch() { return pitch; }
        public int getSelectedSlot() { return selectedSlot; }
        public MockWorld getWorld() { return world; }
    }
}