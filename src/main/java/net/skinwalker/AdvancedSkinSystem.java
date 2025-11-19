package net.skinwalker;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Advanced Skin System - Fetches player skins and merges with skinwalker corruption layers
 */
public class AdvancedSkinSystem {
    
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Map<UUID, String> originalSkinUrls = new HashMap<>();
    private static final Map<UUID, Integer> corruptionLevel = new HashMap<>(); // 0-100
    
    // Skinwalker overlay URLs (corruption layers)
    private static final String[] CORRUPTION_LAYERS = {
        "https://files.catbox.moe/cb8c8a.png", // Base corruption
        "https://files.catbox.moe/qxdjf2.png", // Medium corruption
        "https://files.catbox.moe/7fs0c4.png"  // Heavy corruption
    };
    
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("skinwalker")
                .requires(s -> s.hasPermissionLevel(2))
                .then(CommandManager.literal("fetchskin")
                    .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                    .executes(ctx -> {
                        ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                        fetchPlayerSkin(ctx.getSource(), player);
                        return 1;
                    })))
                .then(CommandManager.literal("corrupt")
                    .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                        .then(CommandManager.argument("level", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0, 100))
                        .executes(ctx -> {
                            ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                            int level = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(ctx, "level");
                            applyCorruption(ctx.getSource(), player, level);
                            return 1;
                        }))
                        .executes(ctx -> {
                            ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                            applyCorruption(ctx.getSource(), player, 50);
                            return 1;
                        }))));
        });
    }
    
    /**
     * Fetch player's original skin from Mojang API
     */
    private static void fetchPlayerSkin(net.minecraft.server.command.ServerCommandSource source, ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        String playerName = player.getName().getString();
        
        CompletableFuture.runAsync(() -> {
            try {
                // Mojang API endpoint
                String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "");
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                // Parse JSON to get skin URL
                // Note: This is simplified - full implementation would parse the JSON properly
                if (response.statusCode() == 200) {
                    String body = response.body();
                    // Extract skin URL from JSON (simplified - would use Gson in production)
                    String skinUrl = extractSkinUrl(body);
                    
                    if (skinUrl != null) {
                        originalSkinUrls.put(uuid, skinUrl);
                        source.getServer().execute(() -> {
                            source.sendFeedback(() -> Text.literal("§aFetched skin for " + playerName), true);
                        });
                    }
                }
            } catch (Exception e) {
                source.getServer().execute(() -> {
                    source.sendFeedback(() -> Text.literal("§cFailed to fetch skin: " + e.getMessage()), false);
                });
            }
        });
    }
    
    /**
     * Apply corruption level to player (0-100)
     */
    private static void applyCorruption(net.minecraft.server.command.ServerCommandSource source, 
                                       ServerPlayerEntity player, int level) {
        UUID uuid = player.getUuid();
        corruptionLevel.put(uuid, level);
        
        // Determine which corruption layer to use
        String corruptionSkin;
        if (level < 33) {
            corruptionSkin = CORRUPTION_LAYERS[0];
        } else if (level < 66) {
            corruptionSkin = CORRUPTION_LAYERS[1];
        } else {
            corruptionSkin = CORRUPTION_LAYERS[2];
        }
        
        // For now, apply the corruption skin directly
        // In a full implementation, you'd merge the original skin with corruption layers
        try {
            source.getServer().getCommandManager().executeWithPrefix(source, 
                "skin player " + player.getName().getString() + " set " + corruptionSkin);
            
            // Spawn particles for visual effect
            ParticleSystem.spawnCorruptionParticles(player);
            
            source.sendFeedback(() -> Text.literal("§4Applied corruption level " + level + " to " + player.getName().getString()), true);
        } catch (Exception e) {
            source.sendFeedback(() -> Text.literal("§cError applying corruption"), false);
        }
    }
    
    /**
     * Get corruption level for player
     */
    public static int getCorruptionLevel(UUID uuid) {
        return corruptionLevel.getOrDefault(uuid, 0);
    }
    
    /**
     * Increase corruption over time (called from tick event)
     */
    public static void tickCorruption(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        RoleSystem.Role role = RoleSystem.getPlayerRole(player);
        
        if (role == RoleSystem.Role.SKINWALKER) {
            int current = corruptionLevel.getOrDefault(uuid, 0);
            if (current < 100) {
                corruptionLevel.put(uuid, Math.min(100, current + 1));
            }
        }
    }
    
    private static String extractSkinUrl(String json) {
        // Simplified extraction - in production, use Gson
        try {
            int urlIndex = json.indexOf("\"url\"");
            if (urlIndex != -1) {
                int start = json.indexOf("\"", urlIndex + 5) + 1;
                int end = json.indexOf("\"", start);
                return json.substring(start, end);
            }
        } catch (Exception e) {
            // Fallback
        }
        return null;
    }
}

