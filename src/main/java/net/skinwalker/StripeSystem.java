package net.skinwalker;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stripe System - Handle payments and ranks
 * WARNING: Secret key should be stored securely, not in code!
 */
public class StripeSystem {
    
    // WARNING: These should be loaded from a secure config file, not hardcoded!
    // Load from environment variables or config file in production
    private static final String STRIPE_PUBLIC_KEY = System.getenv("STRIPE_PUBLIC_KEY") != null ? 
        System.getenv("STRIPE_PUBLIC_KEY") : "YOUR_STRIPE_PUBLIC_KEY_HERE";
    private static final String STRIPE_SECRET_KEY = System.getenv("STRIPE_SECRET_KEY") != null ? 
        System.getenv("STRIPE_SECRET_KEY") : "YOUR_STRIPE_SECRET_KEY_HERE";
    
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Map<UUID, String> playerRanks = new HashMap<>();
    
    public enum Rank {
        DEFAULT, VIP, PREMIUM, ULTIMATE, YOUTUBER
    }
    
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("rank")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    showRank(ctx.getSource(), player);
                    return 1;
                })
                .then(CommandManager.literal("set")
                    .requires(s -> s.hasPermissionLevel(2))
                    .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                        .then(CommandManager.argument("rank", com.mojang.brigadier.arguments.StringArgumentType.string())
                        .executes(ctx -> {
                            ServerPlayerEntity target = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                            String rankStr = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "rank");
                            setRank(ctx.getSource(), target, rankStr);
                            return 1;
                        })))));
        });
    }
    
    private static void showRank(net.minecraft.server.command.ServerCommandSource source, ServerPlayerEntity player) {
        String rank = playerRanks.getOrDefault(player.getUuid(), "DEFAULT");
        source.sendFeedback(() -> Text.literal("§6Your rank: §l" + rank), false);
    }
    
    private static void setRank(net.minecraft.server.command.ServerCommandSource source, 
                               ServerPlayerEntity player, String rankStr) {
        UUID uuid = player.getUuid();
        playerRanks.put(uuid, rankStr.toUpperCase());
        source.sendFeedback(() -> Text.literal("§aSet " + player.getName().getString() + "'s rank to " + rankStr), true);
        
        // Apply rank benefits
        applyRankBenefits(player, rankStr);
    }
    
    private static void applyRankBenefits(ServerPlayerEntity player, String rank) {
        switch (rank.toUpperCase()) {
            case "VIP":
                CoinSystem.addCoins(player.getUuid(), 100);
                break;
            case "PREMIUM":
                CoinSystem.addCoins(player.getUuid(), 500);
                break;
            case "ULTIMATE":
                CoinSystem.addCoins(player.getUuid(), 1000);
                break;
            case "YOUTUBER":
                RoleSystem.setRole(player, RoleSystem.Role.YOUTUBER);
                CoinSystem.addCoins(player.getUuid(), 2000);
                break;
        }
    }
    
    /**
     * Create a payment intent (should be called from web interface)
     */
    public static String createPaymentIntent(int amount, String currency, UUID playerUuid) throws Exception {
        String url = "https://api.stripe.com/v1/payment_intents";
        String body = "amount=" + amount + "&currency=" + currency + "&metadata[player_uuid]=" + playerUuid.toString();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + STRIPE_SECRET_KEY)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
    
    public static String getPublicKey() {
        return STRIPE_PUBLIC_KEY;
    }
}

