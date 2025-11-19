package net.skinwalker;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Player Check System - Detects suspicious behavior and verifies players
 */
public class PlayerCheckSystem {
    
    private static final Map<UUID, PlayerData> playerData = new HashMap<>();
    
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("skinwalker")
                .requires(s -> s.hasPermissionLevel(2))
                .then(CommandManager.literal("check")
                    .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                    .executes(ctx -> {
                        ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                        return checkPlayer(ctx.getSource(), player);
                    }))));
        });
    }
    
    private static int checkPlayer(net.minecraft.server.command.ServerCommandSource source, ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        PlayerData data = playerData.computeIfAbsent(uuid, k -> new PlayerData());
        
        // Calculate trust score
        int trustScore = calculateTrustScore(player, data);
        String status = getStatus(trustScore);
        
        source.sendFeedback(() -> Text.literal("§b=== Player Check: " + player.getName().getString() + " ==="), false);
        source.sendFeedback(() -> Text.literal("§7Trust Score: " + trustScore + "/100"), false);
        source.sendFeedback(() -> Text.literal("§7Status: " + status), false);
        
        // Check if converted
        if (SkinSystem.isConverted(uuid)) {
            source.sendFeedback(() -> Text.literal("§4⚠ WARNING: Player has been converted to skinwalker!"), false);
        }
        
        // Check role
        RoleSystem.Role role = RoleSystem.getPlayerRole(player);
        if (role == RoleSystem.Role.SKINWALKER) {
            source.sendFeedback(() -> Text.literal("§4⚠ WARNING: Player has SKINWALKER role!"), false);
        }
        
        return 1;
    }
    
    private static int calculateTrustScore(ServerPlayerEntity player, PlayerData data) {
        int score = 100;
        
        // Check if converted
        if (SkinSystem.isConverted(player.getUuid())) {
            score -= 50;
        }
        
        // Check role
        RoleSystem.Role role = RoleSystem.getPlayerRole(player);
        if (role == RoleSystem.Role.SKINWALKER) {
            score -= 30;
        }
        
        // Check for invisibility (suspicious)
        if (player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.INVISIBILITY)) {
            score -= 10;
        }
        
        // Check movement patterns (placeholder - would need more complex tracking)
        if (data.suspiciousMovements > 5) {
            score -= 15;
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    private static String getStatus(int trustScore) {
        if (trustScore >= 80) return "§aTRUSTED";
        if (trustScore >= 50) return "§eSUSPICIOUS";
        if (trustScore >= 20) return "§6WARNING";
        return "§4DANGER - LIKELY SKINWALKER";
    }
    
    /**
     * Player data for tracking behavior
     */
    static class PlayerData {
        int suspiciousMovements = 0;
        int chatMessages = 0;
        long lastSeen = System.currentTimeMillis();
    }
}

