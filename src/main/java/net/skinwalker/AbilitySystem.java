package net.skinwalker;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Ability System - Manages player abilities including vanish/reveal
 */
public class AbilitySystem {
    
    private static final Map<UUID, Long> vanishCooldowns = new HashMap<>();
    private static final Map<UUID, Boolean> vanishedPlayers = new HashMap<>();
    private static final long VANISH_COOLDOWN = 5000; // 5 seconds
    
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("vanish")
                .requires(s -> s.hasPermissionLevel(2))
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    return toggleVanish(ctx.getSource(), player, true);
                }));
            
            dispatcher.register(CommandManager.literal("reveal")
                .requires(s -> s.hasPermissionLevel(2))
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    return toggleVanish(ctx.getSource(), player, false);
                }));
        });
        
        // Tick event to maintain invisibility
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID uuid = player.getUuid();
                if (vanishedPlayers.getOrDefault(uuid, false)) {
                    player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.INVISIBILITY, 40, 0, false, false, false));
                }
            }
        });
    }
    
    private static int toggleVanish(net.minecraft.server.command.ServerCommandSource source, 
                                    ServerPlayerEntity player, boolean vanish) {
        UUID uuid = player.getUuid();
        
        if (vanish) {
            // Check cooldown
            long lastUse = vanishCooldowns.getOrDefault(uuid, 0L);
            long now = System.currentTimeMillis();
            if (now - lastUse < VANISH_COOLDOWN) {
                long remaining = (VANISH_COOLDOWN - (now - lastUse)) / 1000;
                source.sendFeedback(() -> Text.literal("§cCooldown: " + remaining + " seconds"), false);
                return 0;
            }
            
            vanishedPlayers.put(uuid, true);
            player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.INVISIBILITY, 99999, 1, false, false, false));
            
            // Fake leave message
            String playerName = player.getName().getString();
            String leaveMsg = "{\"text\":\"" + playerName + " left the game\",\"color\":\"yellow\"}";
            try {
                source.getServer().getCommandManager().executeWithPrefix(source, 
                    "tellraw @a " + leaveMsg);
            } catch (Exception e) {
                // Fallback
                source.getServer().getPlayerManager().broadcast(
                    Text.literal("§e" + playerName + " left the game"), false);
            }
            
            vanishCooldowns.put(uuid, now);
            source.sendFeedback(() -> Text.literal("§aYou are now vanished."), true);
            
        } else {
            vanishedPlayers.remove(uuid);
            player.removeStatusEffect(StatusEffects.INVISIBILITY);
            
            // Fake join message
            String playerName = player.getName().getString();
            String joinMsg = "{\"text\":\"" + playerName + " joined the game\",\"color\":\"yellow\"}";
            try {
                source.getServer().getCommandManager().executeWithPrefix(source, 
                    "tellraw @a " + joinMsg);
            } catch (Exception e) {
                // Fallback
                source.getServer().getPlayerManager().broadcast(
                    Text.literal("§e" + playerName + " joined the game"), false);
            }
            
            source.sendFeedback(() -> Text.literal("§aYou are now visible."), true);
        }
        
        return 1;
    }
    
    public static boolean isVanished(UUID uuid) {
        return vanishedPlayers.getOrDefault(uuid, false);
    }
}

