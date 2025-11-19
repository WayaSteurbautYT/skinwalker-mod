package net.skinwalker;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Shop System - Buy items with coins
 */
public class ShopSystem {
    
    private static final Map<String, ShopItem> shopItems = new HashMap<>();
    
    static {
        // Initialize shop items
        shopItems.put("diamond", new ShopItem(Items.DIAMOND, 50, "Diamond"));
        shopItems.put("netherite", new ShopItem(Items.NETHERITE_INGOT, 200, "Netherite Ingot"));
        shopItems.put("detector", new ShopItem(net.skinwalker.SkinwalkerMod.SKINWALKER_DETECTOR, 100, "Skinwalker Detector"));
        shopItems.put("potion", new ShopItem(net.skinwalker.SkinwalkerMod.TRANSFORMATION_POTION, 150, "Transformation Potion"));
        shopItems.put("cure", new ShopItem(net.skinwalker.SkinwalkerMod.CURE_ITEM, 200, "Cure Item"));
    }
    
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("shop")
                .executes(ctx -> {
                    showShop(ctx.getSource());
                    return 1;
                })
                .then(CommandManager.literal("buy")
                    .then(CommandManager.argument("item", com.mojang.brigadier.arguments.StringArgumentType.string())
                    .executes(ctx -> {
                        String itemName = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "item");
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        return buyItem(ctx.getSource(), player, itemName);
                    }))));
        });
    }
    
    private static void showShop(net.minecraft.server.command.ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("§b§l=== SHOP ==="), false);
        for (Map.Entry<String, ShopItem> entry : shopItems.entrySet()) {
            ShopItem item = entry.getValue();
            source.sendFeedback(() -> Text.literal("§7- §e" + entry.getKey() + " §7(" + item.name + ") §6" + item.price + " coins"), false);
        }
    }
    
    private static int buyItem(net.minecraft.server.command.ServerCommandSource source, ServerPlayerEntity player, String itemName) {
        ShopItem item = shopItems.get(itemName.toLowerCase());
        if (item == null) {
            source.sendFeedback(() -> Text.literal("§cItem not found!"), false);
            return 0;
        }
        
        UUID uuid = player.getUuid();
        if (CoinSystem.spendCoins(uuid, item.price)) {
            ItemStack stack = new ItemStack(item.item);
            player.getInventory().insertStack(stack);
            source.sendFeedback(() -> Text.literal("§aPurchased " + item.name + " for " + item.price + " coins!"), true);
            return 1;
        } else {
            source.sendFeedback(() -> Text.literal("§cNot enough coins! You need " + item.price + " coins."), false);
            return 0;
        }
    }
    
    static class ShopItem {
        final net.minecraft.item.Item item;
        final int price;
        final String name;
        
        ShopItem(net.minecraft.item.Item item, int price, String name) {
            this.item = item;
            this.price = price;
            this.name = name;
        }
    }
}

