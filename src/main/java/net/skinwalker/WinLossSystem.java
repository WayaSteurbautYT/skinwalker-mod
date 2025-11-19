package net.skinwalker;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Win/Loss System - Tracks objectives and determines winners
 */
public class WinLossSystem {
    
    public enum GameState {
        WAITING, IN_PROGRESS, SURVIVORS_WIN, SKINWALKERS_WIN, DRAW
    }
    
    private static GameState currentState = GameState.WAITING;
    private static final Map<UUID, PlayerStats> playerStats = new HashMap<>();
    private static int survivorsAlive = 0;
    private static int skinwalkersAlive = 0;
    
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("skinwalker")
                .requires(s -> s.hasPermissionLevel(2))
                .then(CommandManager.literal("startgame")
                    .executes(ctx -> {
                        startGame(ctx.getSource());
                        return 1;
                    }))
                .then(CommandManager.literal("endgame")
                    .executes(ctx -> {
                        endGame(ctx.getSource());
                        return 1;
                    }))
                .then(CommandManager.literal("stats")
                    .executes(ctx -> {
                        showStats(ctx.getSource(), ctx.getSource().getPlayerOrThrow());
                        return 1;
                    })));
        });
        
        // Tick event to check win conditions
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (currentState == GameState.IN_PROGRESS) {
                checkWinConditions(server);
            }
        });
    }
    
    private static void startGame(net.minecraft.server.command.ServerCommandSource source) {
        currentState = GameState.IN_PROGRESS;
        countPlayers(source.getServer());
        source.getServer().getPlayerManager().broadcast(
            Text.literal("§a§l=== GAME STARTED ==="), false);
        source.getServer().getPlayerManager().broadcast(
            Text.literal("§7Survivors: " + survivorsAlive + " | Skinwalkers: " + skinwalkersAlive), false);
    }
    
    private static void endGame(net.minecraft.server.command.ServerCommandSource source) {
        currentState = GameState.WAITING;
        source.getServer().getPlayerManager().broadcast(
            Text.literal("§c§l=== GAME ENDED ==="), false);
    }
    
    private static void countPlayers(net.minecraft.server.MinecraftServer server) {
        survivorsAlive = 0;
        skinwalkersAlive = 0;
        
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            RoleSystem.Role role = RoleSystem.getPlayerRole(player);
            if (role == RoleSystem.Role.SKINWALKER) {
                skinwalkersAlive++;
            } else {
                survivorsAlive++;
            }
        }
    }
    
    private static void checkWinConditions(net.minecraft.server.MinecraftServer server) {
        countPlayers(server);
        
        // Win condition 1: All survivors converted
        if (survivorsAlive == 0 && skinwalkersAlive > 0) {
            currentState = GameState.SKINWALKERS_WIN;
            declareWinner(server, "§4§lSKINWALKERS WIN!", "All survivors have been converted!");
            ParticleSystem.spawnVictoryParticles(null); // Would need to target all skinwalkers
        }
        
        // Win condition 2: All skinwalkers eliminated (if there's a way to eliminate them)
        if (skinwalkersAlive == 0 && survivorsAlive > 0) {
            currentState = GameState.SURVIVORS_WIN;
            declareWinner(server, "§a§lSURVIVORS WIN!", "All skinwalkers have been eliminated!");
        }
        
        // Win condition 3: Time limit (could add timer)
        // Win condition 4: Objective completion (could add objectives)
    }
    
    private static void declareWinner(net.minecraft.server.MinecraftServer server, String title, String message) {
        server.getPlayerManager().broadcast(Text.literal(title), false);
        server.getPlayerManager().broadcast(Text.literal(message), false);
        
        // Play sound
        try {
            server.getCommandManager().executeWithPrefix(
                server.getCommandSource(), 
                "playsound minecraft:entity.player.levelup master @a ~ ~ ~ 1 1");
        } catch (Exception e) {
            // Ignore
        }
        
        currentState = GameState.WAITING;
    }
    
    private static void showStats(net.minecraft.server.command.ServerCommandSource source, ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        PlayerStats stats = playerStats.computeIfAbsent(uuid, k -> new PlayerStats());
        
        source.sendFeedback(() -> Text.literal("§b=== Your Stats ==="), false);
        source.sendFeedback(() -> Text.literal("§7Transformations: " + stats.transformations), false);
        source.sendFeedback(() -> Text.literal("§7Survivals: " + stats.survivals), false);
        source.sendFeedback(() -> Text.literal("§7Wins: " + stats.wins), false);
    }
    
    public static void recordTransformation(UUID uuid) {
        playerStats.computeIfAbsent(uuid, k -> new PlayerStats()).transformations++;
    }
    
    public static void recordSurvival(UUID uuid) {
        playerStats.computeIfAbsent(uuid, k -> new PlayerStats()).survivals++;
    }
    
    public static void recordWin(UUID uuid) {
        playerStats.computeIfAbsent(uuid, k -> new PlayerStats()).wins++;
    }
    
    public static GameState getGameState() {
        return currentState;
    }
    
    static class PlayerStats {
        int transformations = 0;
        int survivals = 0;
        int wins = 0;
    }
}

