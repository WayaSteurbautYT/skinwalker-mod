package net.skinwalker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Bot System - AI-powered bots for roleplay events
 */
public class BotSystem {

    // API Key - should be moved to config file
    private static final String API_KEY = "AIzaSyDX_MkrzqjE3q1jiDqSB5d95cV2fU6cMJw"; 
    private static final String MODEL_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
    
    private static final Map<String, BotPersona> activeBots = new HashMap<>();
    private static boolean eventActive = false;
    private static final Random random = new Random();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("skinwalker")
                .requires(s -> s.hasPermissionLevel(2))
                .then(CommandManager.literal("summon")
                    .then(CommandManager.argument("name", StringArgumentType.word())
                    .executes(ctx -> {
                        String name = StringArgumentType.getString(ctx, "name");
                        return summonBot(ctx.getSource(), name);
                    })))
                .then(CommandManager.literal("mode")
                    .then(CommandManager.argument("state", StringArgumentType.word())
                    .executes(ctx -> {
                        String state = StringArgumentType.getString(ctx, "state");
                        return setBotMode(ctx.getSource(), state);
                    })))
                .then(CommandManager.literal("remove")
                    .then(CommandManager.argument("name", StringArgumentType.word())
                    .executes(ctx -> {
                        String name = StringArgumentType.getString(ctx, "name");
                        return removeBot(ctx.getSource(), name);
                    })))
                .then(CommandManager.literal("list")
                    .executes(ctx -> {
                        listBots(ctx.getSource());
                        return 1;
                    })));
        });

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            if (!eventActive || activeBots.isEmpty()) return;
            String content = message.getContent().getString();
            
            // Check if any bot should respond
            for (BotPersona bot : activeBots.values()) {
                if (shouldBotRespond(bot, content, sender)) {
                    generateAIResponse(bot, content, sender.getName().getString(), sender);
                    break; // Only one bot responds at a time
                }
            }
        });
        
        // Proximity-based bot responses (for voice chat compatibility)
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!eventActive || activeBots.isEmpty()) return;
            
            // Check proximity every 5 seconds (100 ticks)
            if (server.getTicks() % 100 == 0) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    checkProximityBots(player, server);
                }
            }
        });
    }

    private static int summonBot(net.minecraft.server.command.ServerCommandSource source, String name) {
        BotPersona bot = new BotPersona(name);
        activeBots.put(name, bot);
        eventActive = true;
        
        source.sendFeedback(() -> Text.literal("§aBot " + name + " active."), true);
        
        // Try to spawn bot player (requires player mod or similar)
        try {
            source.getServer().getCommandManager().executeWithPrefix(source, "player " + name + " spawn at ~ ~ ~");
        } catch (Exception e) {
            // Player spawning might not be available, that's okay
        }
        
        // Set skin based on bot name
        setBotSkin(source, bot);
        
        return 1;
    }

    private static int setBotMode(net.minecraft.server.command.ServerCommandSource source, String state) {
        boolean corrupt = state.equalsIgnoreCase("corrupt");
        
        for (BotPersona bot : activeBots.values()) {
            bot.isCorrupt = corrupt;
            setBotSkin(source, bot);
        }
        
        source.sendFeedback(() -> Text.literal("§eMode set to: " + state), true);
        return 1;
    }

    private static int removeBot(net.minecraft.server.command.ServerCommandSource source, String name) {
        if (activeBots.remove(name) != null) {
            source.sendFeedback(() -> Text.literal("§cBot " + name + " removed."), true);
            if (activeBots.isEmpty()) {
                eventActive = false;
            }
            return 1;
        }
        source.sendFeedback(() -> Text.literal("§cBot " + name + " not found."), false);
        return 0;
    }

    private static void listBots(net.minecraft.server.command.ServerCommandSource source) {
        if (activeBots.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§7No active bots."), false);
            return;
        }
        
        source.sendFeedback(() -> Text.literal("§bActive Bots:"), false);
        for (BotPersona bot : activeBots.values()) {
            String status = bot.isCorrupt ? "§4Corrupted" : "§aNormal";
            source.sendFeedback(() -> Text.literal("  §7- " + bot.name + " (" + status + "§7)"), false);
        }
    }

    private static void setBotSkin(net.minecraft.server.command.ServerCommandSource source, BotPersona bot) {
        String skinUrl = getSkinUrl(bot.name, bot.isCorrupt);
        try {
            if (skinUrl.startsWith("http")) {
                source.getServer().getCommandManager().executeWithPrefix(source, 
                    "skin player " + bot.name + " set " + skinUrl);
            } else {
                source.getServer().getCommandManager().executeWithPrefix(source, 
                    "skin " + bot.name + " set " + skinUrl);
            }
        } catch (Exception e) {
            // Skin command might not be available
        }
    }

    private static String getSkinUrl(String name, boolean corrupt) {
        String lowerName = name.toLowerCase();
        
        if (corrupt) {
            // Return skinwalker skin
            switch (lowerName) {
                case "dream": return "https://files.catbox.moe/9gf4xa.png";
                case "mrbeast": return "https://files.catbox.moe/o456fl.png";
                case "craftee": return "https://files.catbox.moe/v2ibhg.png";
                case "technoblade": return "https://files.catbox.moe/6v0y8i.png";
                case "preston": return "https://files.catbox.moe/fbjewy.png";
                default: return "https://files.catbox.moe/cb8c8a.png";
            }
        } else {
            // Return normal skin
            switch (lowerName) {
                case "dream": return "Dream";
                case "mrbeast": return "JimmyDonaldson";
                case "craftee": return "Parker_Games";
                case "technoblade": return "Technoblade";
                case "preston": return "PrestonPlayz";
                default: return "Steve";
            }
        }
    }

    private static boolean shouldBotRespond(BotPersona bot, String message, ServerPlayerEntity sender) {
        String lowerMsg = message.toLowerCase();
        String lowerName = bot.name.toLowerCase();
        
        // Bot responds if:
        // 1. Mentioned by name
        // 2. Random chance (20% if event is active)
        // 3. Close proximity (for voice chat compatibility)
        boolean mentioned = lowerMsg.contains(lowerName);
        boolean randomChance = eventActive && random.nextInt(10) < 2;
        boolean proximity = isNearBot(sender, bot);
        
        return mentioned || randomChance || proximity;
    }
    
    /**
     * Check if player is near a bot (for voice chat/proximity features)
     */
    private static boolean isNearBot(ServerPlayerEntity player, BotPersona bot) {
        // Find bot player entity
        for (ServerPlayerEntity botPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            if (botPlayer.getName().getString().equals(bot.name)) {
                double distance = player.distanceTo(botPlayer);
                return distance < 16.0; // 16 blocks proximity
            }
        }
        return false;
    }
    
    /**
     * Check for proximity-based bot interactions (for Simple Voice Chat compatibility)
     */
    private static void checkProximityBots(ServerPlayerEntity player, net.minecraft.server.MinecraftServer server) {
        for (BotPersona bot : activeBots.values()) {
            if (isNearBot(player, bot) && random.nextInt(100) < 5) {
                // Bot occasionally speaks when players are nearby
                String context = "A player is nearby. Say something brief and contextual.";
                generateAIResponse(bot, context, player.getName().getString(), player);
            }
        }
    }

    private static void generateAIResponse(BotPersona bot, String userMsg, String userName, ServerPlayerEntity player) {
        String prompt = buildPrompt(bot, userMsg, userName);
        
        // Use MultiAPISystem for AI responses
        MultiAPISystem.generateResponse(prompt, null).thenAccept(reply -> {
            if (reply != null && !reply.trim().isEmpty()) {
                player.getServer().execute(() -> {
                    Formatting color = bot.isCorrupt ? Formatting.DARK_RED : Formatting.YELLOW;
                    player.getServer().getPlayerManager().broadcast(
                        Text.literal("<" + bot.name + "> " + reply.trim()).formatted(color), 
                        false
                    );
                });
            }
        }).exceptionally(e -> {
            System.err.println("Error generating AI response for " + bot.name + ": " + e.getMessage());
            return null;
        });
    }

    private static String buildPrompt(BotPersona bot, String userMsg, String userName) {
        if (bot.isCorrupt) {
            return "You are a glitchy, evil Minecraft Skinwalker mimicking " + bot.name + 
                   ". You speak in corrupted, glitchy text sometimes. Keep responses short and creepy. " +
                   "Player " + userName + " said: " + userMsg + "\nReply:";
        } else {
            return "You are YouTuber " + bot.name + " in Minecraft. " +
                   "You are helpful, energetic, and friendly. Keep responses short and in character. " +
                   "Player " + userName + " said: " + userMsg + "\nReply:";
        }
    }

    private static String buildJsonRequest(String prompt) {
        // Escape JSON special characters
        String escapedPrompt = prompt.replace("\\", "\\\\")
                                     .replace("\"", "\\\"")
                                     .replace("\n", "\\n");
        return "{\"contents\": [{\"parts\":[{\"text\": \"" + escapedPrompt + "\"}]}]}";
    }

    private static String extractReply(JsonObject json) {
        try {
            return json.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isEventActive() {
        return eventActive;
    }

    public static void setEventActive(boolean active) {
        eventActive = active;
    }

    public static Map<String, BotPersona> getActiveBots() {
        return new HashMap<>(activeBots);
    }

    /**
     * Bot persona data
     */
    public static class BotPersona {
        public final String name;
        public boolean isCorrupt = false;
        
        public BotPersona(String name) {
            this.name = name;
        }
    }
}

