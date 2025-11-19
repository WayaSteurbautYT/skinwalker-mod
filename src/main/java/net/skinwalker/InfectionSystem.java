package net.skinwalker;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Infection System - Tracks infection progress and displays infection bar
 */
public class InfectionSystem {
    
    private static final Map<UUID, Float> infectionLevel = new HashMap<>(); // 0.0 to 1.0
    private static final Map<UUID, Boolean> isInfected = new HashMap<>();
    
    public static void init() {
        // Tick event to update infection
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                updateInfection(player);
            }
        });
    }
    
    private static void updateInfection(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        RoleSystem.Role role = RoleSystem.getPlayerRole(player);
        
        if (role == RoleSystem.Role.SKINWALKER || role == RoleSystem.Role.YOUTUBER) {
            // Skinwalkers and YouTubers spread infection
            float current = infectionLevel.getOrDefault(uuid, 0.0f);
            if (current < 1.0f) {
                infectionLevel.put(uuid, Math.min(1.0f, current + 0.001f));
            }
            
            // Check nearby players for infection spread
            for (ServerPlayerEntity nearby : player.getServerWorld().getPlayers()) {
                if (nearby != player && nearby.distanceTo(player) < 5.0) {
                    spreadInfection(nearby, 0.0005f);
                }
            }
        }
        
        // Display infection bar
        displayInfectionBar(player);
    }
    
    private static void spreadInfection(ServerPlayerEntity player, float amount) {
        UUID uuid = player.getUuid();
        RoleSystem.Role role = RoleSystem.getPlayerRole(player);
        
        // Survivors can get infected
        if (role == RoleSystem.Role.SURVIVOR) {
            float current = infectionLevel.getOrDefault(uuid, 0.0f);
            float newLevel = Math.min(1.0f, current + amount);
            infectionLevel.put(uuid, newLevel);
            
            // Convert to skinwalker at 100%
            if (newLevel >= 1.0f && !isInfected.getOrDefault(uuid, false)) {
                convertToInfected(player);
            }
        }
    }
    
    private static void convertToInfected(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        isInfected.put(uuid, true);
        
        // Convert to skinwalker
        RoleSystem.setRole(player, RoleSystem.Role.SKINWALKER);
        SkinSystem.convertToSkinwalker(player, "normal");
        ParticleSystem.spawnTransformationParticles(player);
        
        // Broadcast message
        player.getServer().getPlayerManager().broadcast(
            Text.literal("§4§l" + player.getName().getString() + " has been INFECTED!"), false);
        
        // Play sound
        try {
            player.getServer().getCommandManager().executeWithPrefix(
                player.getCommandSource(),
                "playsound minecraft:entity.wither.spawn master @a ~ ~ ~ 1 1");
        } catch (Exception e) {
            // Ignore
        }
    }
    
    private static void displayInfectionBar(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        float level = infectionLevel.getOrDefault(uuid, 0.0f);
        
        if (level > 0.0f) {
            // Send action bar with infection level
            int percentage = (int) (level * 100);
            String color = level < 0.3f ? "§a" : level < 0.7f ? "§e" : "§c";
            
            player.sendMessage(Text.literal(color + "Infection: " + percentage + "%"), true);
        }
    }
    
    public static float getInfectionLevel(UUID uuid) {
        return infectionLevel.getOrDefault(uuid, 0.0f);
    }
    
    public static boolean isInfected(UUID uuid) {
        return isInfected.getOrDefault(uuid, false);
    }
    
    public static void setInfectionLevel(UUID uuid, float level) {
        infectionLevel.put(uuid, Math.max(0.0f, Math.min(1.0f, level)));
    }
}

