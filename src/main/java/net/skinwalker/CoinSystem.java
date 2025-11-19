package net.skinwalker;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Coin System - Earn coins by playing, spend in shop
 */
public class CoinSystem {
    
    private static final Map<UUID, Integer> playerCoins = new HashMap<>();
    private static final Map<UUID, Long> lastCoinEarn = new HashMap<>();
    private static final long COIN_EARN_INTERVAL = 6000; // 5 minutes in ticks
    
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("coins")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    showCoins(ctx.getSource(), player);
                    return 1;
                })
                .then(CommandManager.literal("give")
                    .requires(s -> s.hasPermissionLevel(2))
                    .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            ServerPlayerEntity target = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                            addCoins(target.getUuid(), amount);
                            ctx.getSource().sendFeedback(() -> Text.literal("Gave " + amount + " coins to " + target.getName().getString()), true);
                            return 1;
                        })))));
        });
        
        // Earn coins over time
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % COIN_EARN_INTERVAL == 0) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    earnCoins(player);
                }
            }
        });
    }
    
    private static void earnCoins(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        long now = System.currentTimeMillis();
        Long lastEarn = lastCoinEarn.get(uuid);
        
        // Earn coins every 5 minutes of playtime
        if (lastEarn == null || now - lastEarn >= 300000) {
            int coins = 10; // Base coins
            RoleSystem.Role role = RoleSystem.getPlayerRole(player);
            
            // Role bonuses
            switch (role) {
                case YOUTUBER:
                    coins += 5;
                    break;
                case DREAM:
                case TECHNO:
                case BEAST:
                case CRAFTEE:
                    coins += 3;
                    break;
            }
            
            addCoins(uuid, coins);
            player.sendMessage(Text.literal("§a+§l" + coins + " §acoins earned!"), false);
            lastCoinEarn.put(uuid, now);
        }
    }
    
    public static void addCoins(UUID uuid, int amount) {
        playerCoins.put(uuid, playerCoins.getOrDefault(uuid, 0) + amount);
    }
    
    public static boolean spendCoins(UUID uuid, int amount) {
        int current = playerCoins.getOrDefault(uuid, 0);
        if (current >= amount) {
            playerCoins.put(uuid, current - amount);
            return true;
        }
        return false;
    }
    
    public static int getCoins(UUID uuid) {
        return playerCoins.getOrDefault(uuid, 0);
    }
    
    private static void showCoins(net.minecraft.server.command.ServerCommandSource source, ServerPlayerEntity player) {
        int coins = getCoins(player.getUuid());
        source.sendFeedback(() -> Text.literal("§6You have §l" + coins + " §6coins"), false);
    }
}

