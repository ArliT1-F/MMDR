package com.mmdr.inspector;

import com.mmdr.MMDR;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Monitors network packets sent between client and server.
 * 
 * Tracks packet types, frequencies, sizes, and provides
 * real-time visualization of network traffic.
 * 
 * Features:
 * - Real-time packet logging
 * - Packet filtering by type
 * - Traffic statistics
 * - Packet inspection
 * 
 * @author MMDR Team
 */
public class PacketMonitor {
    private static final int MAX_STORED_PACKETS = 1000;
    private static final int DISPLAY_PACKETS = 50;
    
    private final ConcurrentLinkedQueue<PacketInfo> packetQueue;
    private final AtomicLong totalPackets;
    private final AtomicLong totalBytes;
    
    private volatile boolean running = false;
    private long startTime;
    
    // Packet rate calculation
    private long lastPacketCount = 0;
    private long lastRateCheck = 0;
    private int packetsPerSecond = 0;
    
    public PacketMonitor() {
        this.packetQueue = new ConcurrentLinkedQueue<>();
        this.totalPackets = new AtomicLong(0);
        this.totalBytes = new AtomicLong(0);
    }
    
    /**
     * Start monitoring packets
     */
    public void start() {
        if (running) {
            return;
        }
        
        running = true;
        startTime = System.currentTimeMillis();
        
        MMDR.LOGGER.info("Packet monitor started");
    }
    
    /**
     * Stop monitoring packets
     */
    public void stop() {
        if (!running) {
            return;
        }
        
        running = false;
        MMDR.LOGGER.info("Packet monitor stopped");
    }
    
    /**
     * Record a packet (called from mixin)
     */
    public void recordPacket(Packet<?> packet, boolean clientbound) {
        if (!running) {
            return;
        }
        
        PacketInfo info = new PacketInfo(packet, clientbound);
        
        // Add to queue
        packetQueue.offer(info);
        
        // Limit queue size
        while (packetQueue.size() > MAX_STORED_PACKETS) {
            packetQueue.poll();
        }
        
        // Update statistics
        totalPackets.incrementAndGet();
        // Note: Getting actual packet size would require serialization
        // Using estimated size for now
        totalBytes.addAndGet(estimatePacketSize(packet));
        
        // Update packets per second
        updatePacketRate();
    }
    
    /**
     * Update packet rate calculation
     */
    private void updatePacketRate() {
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastRateCheck >= 1000) {
            long currentCount = totalPackets.get();
            packetsPerSecond = (int) (currentCount - lastPacketCount);
            lastPacketCount = currentCount;
            lastRateCheck = currentTime;
        }
    }
    
    /**
     * Estimate packet size (rough approximation)
     */
    private int estimatePacketSize(Packet<?> packet) {
        // This is a rough estimate
        // Actual implementation would serialize the packet
        return 64; // Average estimate
    }
    
    /**
     * Get recent packets for display
     */
    public List<PacketInfo> getRecentPackets(int count) {
        List<PacketInfo> recent = new ArrayList<>();
        
        int skip = Math.max(0, packetQueue.size() - count);
        int i = 0;
        
        for (PacketInfo info : packetQueue) {
            if (i >= skip) {
                recent.add(info);
            }
            i++;
        }
        
        return recent;
    }
    
    /**
     * Get total number of packets
     */
    public long getTotalPackets() {
        return totalPackets.get();
    }
    
    /**
     * Get total bytes transferred
     */
    public long getTotalBytes() {
        return totalBytes.get();
    }
    
    /**
     * Get packets per second
     */
    public int getPacketsPerSecond() {
        return packetsPerSecond;
    }
    
    /**
     * Get uptime in seconds
     */
    public long getUptimeSeconds() {
        if (!running) {
            return 0;
        }
        return (System.currentTimeMillis() - startTime) / 1000;
    }
    
    /**
     * Clear all recorded packets
     */
    public void clear() {
        packetQueue.clear();
        totalPackets.set(0);
        totalBytes.set(0);
        lastPacketCount = 0;
        packetsPerSecond = 0;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Information about a captured packet
     */
    public static class PacketInfo {
        private final String packetName;
        private final boolean clientbound;
        private final long timestamp;
        
        public PacketInfo(Packet<?> packet, boolean clientbound) {
            this.packetName = packet.getClass().getSimpleName();
            this.clientbound = clientbound;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getPacketName() {
            return packetName;
        }
        
        public boolean isClientbound() {
            return clientbound;
        }
        
        public boolean isServerbound() {
            return !clientbound;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public String getFormattedTime() {
            long now = System.currentTimeMillis();
            long diff = now - timestamp;
            
            if (diff < 1000) {
                return diff + "ms ago";
            } else if (diff < 60000) {
                return (diff / 1000) + "s ago";
            } else {
                return (diff / 60000) + "m ago";
            }
        }
    }
}