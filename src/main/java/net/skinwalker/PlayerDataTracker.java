package net.skinwalker;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Player Data Tracker - Tracks behavior patterns and statistics
 */
public class PlayerDataTracker {
    
    private static final Map<UUID, PlayerBehavior> playerBehaviors = new HashMap<>();
    
    public static void init() {
        // Track player behavior every tick
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                trackBehavior(player);
            }
        });
    }
    
    private static void trackBehavior(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        PlayerBehavior behavior = playerBehaviors.computeIfAbsent(uuid, k -> new PlayerBehavior());
        
        // Track movement
        if (player.getVelocity().lengthSquared() > 0.1) {
            behavior.movementCount++;
        }
        
        // Track position changes
        if (player.prevX != player.getX() || player.prevZ != player.getZ()) {
            behavior.distanceTraveled += Math.sqrt(
                Math.pow(player.getX() - player.prevX, 2) + 
                Math.pow(player.getZ() - player.prevZ, 2)
            );
        }
        
        // Track time played
        behavior.timePlayed++;
        
        // Detect suspicious patterns
        detectSuspiciousBehavior(player, behavior);
    }
    
    private static void detectSuspiciousBehavior(ServerPlayerEntity player, PlayerBehavior behavior) {
        // Pattern 1: Not moving for a long time (staring)
        if (behavior.movementCount == 0 && behavior.timePlayed > 200) {
            behavior.suspiciousScore += 5;
        }
        
        // Pattern 2: Moving too fast (unnatural)
        if (behavior.distanceTraveled > 50 && behavior.timePlayed < 100) {
            behavior.suspiciousScore += 3;
        }
        
        // Pattern 3: Invisibility
        if (player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.INVISIBILITY)) {
            behavior.suspiciousScore += 10;
        }
        
        // Pattern 4: Not sleeping when others do
        if (player.getWorld().isNight() && !player.isSleeping()) {
            behavior.suspiciousScore += 2;
        }
    }
    
    public static int getSuspiciousScore(UUID uuid) {
        PlayerBehavior behavior = playerBehaviors.get(uuid);
        return behavior != null ? behavior.suspiciousScore : 0;
    }
    
    public static PlayerBehavior getBehavior(UUID uuid) {
        return playerBehaviors.get(uuid);
    }
    
    public static class PlayerBehavior {
        int movementCount = 0;
        double distanceTraveled = 0;
        int timePlayed = 0;
        int suspiciousScore = 0;
        int chatMessages = 0;
        long lastChatTime = 0;
    }
}

