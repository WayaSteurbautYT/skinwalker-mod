package net.skinwalker;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Anti-AFK System - Countdown and warnings to keep players engaged
 */
public class AntiAFKSystem {
    
    private static final Map<UUID, Long> lastMovement = new HashMap<>();
    private static final Map<UUID, Integer> warningCount = new HashMap<>();
    private static final long AFK_CHECK_INTERVAL = 6000; // 5 minutes in ticks
    private static final long AFK_WARNING_TIME = 300000; // 5 minutes in milliseconds
    private static final long AFK_KICK_TIME = 600000; // 10 minutes in milliseconds
    
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % AFK_CHECK_INTERVAL == 0) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    checkAFK(player);
                }
            }
        });
    }
    
    private static void checkAFK(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        long now = System.currentTimeMillis();
        Long lastMove = lastMovement.get(uuid);
        
        // Update last movement if player moved
        if (player.getVelocity().lengthSquared() > 0.01 || 
            player.prevX != player.getX() || 
            player.prevZ != player.getZ() ||
            player.prevY != player.getY()) {
            lastMovement.put(uuid, now);
            warningCount.put(uuid, 0);
            return;
        }
        
        // Check if AFK
        if (lastMove == null) {
            lastMovement.put(uuid, now);
            return;
        }
        
        long timeSinceMove = now - lastMove;
        
        // Warning at 5 minutes
        if (timeSinceMove >= AFK_WARNING_TIME && timeSinceMove < AFK_WARNING_TIME + 1000) {
            int warnings = warningCount.getOrDefault(uuid, 0) + 1;
            warningCount.put(uuid, warnings);
            
            player.sendMessage(Text.literal("§c§l⚠ WARNING: You are AFK!"), true);
            player.sendMessage(Text.literal("§7Move within 5 minutes or you will be removed from the event."), false);
            
            // Play sound
            try {
                player.getServer().getCommandManager().executeWithPrefix(
                    player.getCommandSource(),
                    "playsound minecraft:block.note_block.bell master @s ~ ~ ~ 1 1");
            } catch (Exception e) {
                // Ignore
            }
        }
        
        // Countdown every 30 seconds after warning
        if (timeSinceMove >= AFK_WARNING_TIME) {
            long remaining = AFK_KICK_TIME - timeSinceMove;
            if (remaining > 0 && remaining % 30000 < 1000) { // Every 30 seconds
                int minutes = (int) (remaining / 60000);
                int seconds = (int) ((remaining % 60000) / 1000);
                player.sendMessage(Text.literal("§c⏰ AFK Countdown: " + minutes + "m " + seconds + "s"), true);
            }
        }
        
        // Kick at 10 minutes
        if (timeSinceMove >= AFK_KICK_TIME) {
            player.networkHandler.disconnect(Text.literal("§cYou were removed for being AFK too long."));
        }
    }
    
    public static void resetAFK(UUID uuid) {
        lastMovement.put(uuid, System.currentTimeMillis());
        warningCount.put(uuid, 0);
    }
}

