package net.skinwalker;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.StringArgumentType;

/**
 * Game Mode System - Different play modes for the mod
 */
public class GameModeSystem {
    
    public enum GameMode {
        SURVIVAL,    // Classic skinwalker hunt
        EVENT,       // Scripted story progression
        SANDBOX,     // Free play with all features
        TEAM         // Survivors vs Skinwalkers
    }
    
    private static GameMode currentMode = GameMode.SURVIVAL;
    
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("skinwalker")
                .requires(s -> s.hasPermissionLevel(2))
                .then(CommandManager.literal("mode")
                    .then(CommandManager.argument("gamemode", StringArgumentType.word())
                    .executes(ctx -> {
                        String modeStr = StringArgumentType.getString(ctx, "gamemode");
                        return setGameMode(ctx.getSource(), modeStr);
                    }))
                    .executes(ctx -> {
                        showCurrentMode(ctx.getSource());
                        return 1;
                    })));
        });
    }
    
    private static int setGameMode(net.minecraft.server.command.ServerCommandSource source, String modeStr) {
        try {
            GameMode mode = GameMode.valueOf(modeStr.toUpperCase());
            currentMode = mode;
            source.getServer().getPlayerManager().broadcast(
                Text.literal("§aGame mode set to: §l" + mode.name()), false);
            applyModeSettings(source, mode);
            return 1;
        } catch (IllegalArgumentException e) {
            source.sendFeedback(() -> Text.literal("§cInvalid mode. Use: SURVIVAL, EVENT, SANDBOX, TEAM"), false);
            return 0;
        }
    }
    
    private static void showCurrentMode(net.minecraft.server.command.ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("§bCurrent game mode: §l" + currentMode.name()), false);
        source.sendFeedback(() -> Text.literal("§7Available modes: SURVIVAL, EVENT, SANDBOX, TEAM"), false);
    }
    
    private static void applyModeSettings(net.minecraft.server.command.ServerCommandSource source, GameMode mode) {
        switch (mode) {
            case SURVIVAL:
                // Enable win/loss system
                WinLossSystem.init();
                source.getServer().getPlayerManager().broadcast(
                    Text.literal("§7Survival Mode: Hunt or be hunted!"), false);
                break;
            case EVENT:
                // Enable story system
                source.getServer().getPlayerManager().broadcast(
                    Text.literal("§7Event Mode: Follow the story!"), false);
                break;
            case SANDBOX:
                // All features enabled
                source.getServer().getPlayerManager().broadcast(
                    Text.literal("§7Sandbox Mode: All features enabled!"), false);
                break;
            case TEAM:
                // Team-based gameplay
                source.getServer().getPlayerManager().broadcast(
                    Text.literal("§7Team Mode: Survivors vs Skinwalkers!"), false);
                break;
        }
    }
    
    public static GameMode getCurrentMode() {
        return currentMode;
    }
}

