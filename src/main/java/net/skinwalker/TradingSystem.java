package net.skinwalker;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Trading System - Players can trade items and coins
 */
public class TradingSystem {
    
    private static final Map<UUID, TradeRequest> pendingTrades = new HashMap<>();
    
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("trade")
                .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                .executes(ctx -> {
                    ServerPlayerEntity sender = ctx.getSource().getPlayerOrThrow();
                    ServerPlayerEntity target = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                    sendTradeRequest(ctx.getSource(), sender, target);
                    return 1;
                }))
                .then(CommandManager.literal("accept")
                    .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                    .executes(ctx -> {
                        ServerPlayerEntity accepter = ctx.getSource().getPlayerOrThrow();
                        ServerPlayerEntity trader = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                        acceptTrade(ctx.getSource(), accepter, trader);
                        return 1;
                    })))
                .then(CommandManager.literal("coins")
                    .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            ServerPlayerEntity sender = ctx.getSource().getPlayerOrThrow();
                            ServerPlayerEntity target = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                            tradeCoins(ctx.getSource(), sender, target, amount);
                            return 1;
                        })))));
        });
    }
    
    private static void sendTradeRequest(net.minecraft.server.command.ServerCommandSource source, 
                                        ServerPlayerEntity sender, ServerPlayerEntity target) {
        source.sendFeedback(() -> Text.literal("§aTrade request sent to " + target.getName().getString()), false);
        target.sendMessage(Text.literal("§a" + sender.getName().getString() + " wants to trade! Use /trade accept " + sender.getName().getString()), false);
    }
    
    private static void acceptTrade(net.minecraft.server.command.ServerCommandSource source,
                                   ServerPlayerEntity accepter, ServerPlayerEntity trader) {
        // Simple trade implementation
        source.sendFeedback(() -> Text.literal("§aTrade accepted! Use /trade coins to trade coins."), false);
        trader.sendMessage(Text.literal("§a" + accepter.getName().getString() + " accepted your trade!"), false);
    }
    
    private static void tradeCoins(net.minecraft.server.command.ServerCommandSource source,
                                  ServerPlayerEntity sender, ServerPlayerEntity target, int amount) {
        UUID senderUuid = sender.getUuid();
        UUID targetUuid = target.getUuid();
        
        if (CoinSystem.spendCoins(senderUuid, amount)) {
            CoinSystem.addCoins(targetUuid, amount);
            source.sendFeedback(() -> Text.literal("§aSent " + amount + " coins to " + target.getName().getString()), true);
            target.sendMessage(Text.literal("§aReceived " + amount + " coins from " + sender.getName().getString()), false);
        } else {
            source.sendFeedback(() -> Text.literal("§cNot enough coins!"), false);
        }
    }
    
    static class TradeRequest {
        final UUID sender;
        final UUID target;
        final ItemStack[] senderItems;
        final ItemStack[] targetItems;
        final int senderCoins;
        final int targetCoins;
        
        TradeRequest(UUID sender, UUID target, ItemStack[] senderItems, ItemStack[] targetItems, int senderCoins, int targetCoins) {
            this.sender = sender;
            this.target = target;
            this.senderItems = senderItems;
            this.targetItems = targetItems;
            this.senderCoins = senderCoins;
            this.targetCoins = targetCoins;
        }
    }
}

