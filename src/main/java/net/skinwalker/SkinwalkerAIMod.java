package net.skinwalker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SkinwalkerAIMod implements ModInitializer {

    // YOUR API KEY
    private static final String API_KEY = "AIzaSyDX_MkrzqjE3q1jiDqSB5d95cV2fU6cMJw"; 
    private static final String MODEL_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
    
    private final Map<String, BotPersona> activeBots = new HashMap<>();
    private boolean eventActive = false;
    private final Random random = new Random();

    @Override
    public void onInitialize() {
        System.out.println("Skinwalker Mod Loaded.");
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("skinwalker")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("summon")
                    .then(CommandManager.argument("name", StringArgumentType.word())
                    .executes(ctx -> summonBot(ctx, StringArgumentType.getString(ctx, "name")))))
                .then(CommandManager.literal("mode")
                    .then(CommandManager.argument("state", StringArgumentType.word())
                    .executes(ctx -> setBotMode(ctx, StringArgumentType.getString(ctx, "state")))))
            );
        });

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            if (!eventActive || activeBots.isEmpty()) return;
            String content = message.getContent().getString();
            for (BotPersona bot : activeBots.values()) {
                if (content.toLowerCase().contains(bot.name.toLowerCase()) || random.nextInt(10) < 2) {
                    generateAIResponse(bot, content, sender.getName().getString(), sender);
                    break;
                }
            }
        });
    }

    private int summonBot(CommandContext<ServerCommandSource> ctx, String name) {
        BotPersona bot = new BotPersona(name);
        activeBots.put(name, bot);
        eventActive = true;
        ctx.getSource().sendFeedback(() -> Text.literal("Bot " + name + " active."), true);
        try {
             ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), "skin " + name + " set " + getSkinUrl(name, false));
        } catch (Exception e) {}
        return 1;
    }

    private int setBotMode(CommandContext<ServerCommandSource> ctx, String state) {
        boolean corrupt = state.equalsIgnoreCase("corrupt");
        activeBots.values().forEach(bot -> {
            bot.isCorrupt = corrupt;
            try {
                ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), "skin " + bot.name + " set " + getSkinUrl(bot.name, corrupt));
            } catch (Exception e) {}
        });
        ctx.getSource().sendFeedback(() -> Text.literal("Mode set to: " + state), true);
        return 1;
    }

    private void generateAIResponse(BotPersona bot, String userMsg, String userName, ServerPlayerEntity player) {
        CompletableFuture.runAsync(() -> {
            try {
                String prompt = bot.isCorrupt ? "You are a glitchy, evil Minecraft Skinwalker mimicking " + bot.name + ". Keep it short/creepy." : "You are YouTuber " + bot.name + ". Helpful and energetic. Keep it short.";
                String jsonInput = "{\"contents\": [{\"parts\":[{\"text\": \"" + prompt + "\\nPlayer " + userName + ": " + userMsg + "\\nReply:\"}]}]}";
                
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(MODEL_URL)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonInput)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                if (json.has("candidates")) {
                    String reply = json.getAsJsonArray("candidates").get(0).getAsJsonObject().getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject().get("text").getAsString();
                    player.getServer().execute(() -> player.getServer().getPlayerManager().broadcast(Text.literal("<" + bot.name + "> " + reply.trim()).formatted(bot.isCorrupt ? Formatting.DARK_RED : Formatting.YELLOW), false));
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private String getSkinUrl(String name, boolean corrupt) {
        if (name.equalsIgnoreCase("Dream")) return corrupt ? "https://files.catbox.moe/9gf4xa.png" : "Dream";
        if (name.equalsIgnoreCase("MrBeast")) return corrupt ? "https://files.catbox.moe/o456fl.png" : "JimmyDonaldson";
        if (name.equalsIgnoreCase("Craftee")) return corrupt ? "https://files.catbox.moe/v2ibhg.png" : "Parker_Games";
        if (name.equalsIgnoreCase("Technoblade")) return corrupt ? "https://files.catbox.moe/6v0y8i.png" : "Technoblade";
        return "https://files.catbox.moe/cb8c8a.png";
    }

    private static class BotPersona {
        String name; boolean isCorrupt = false;
        public BotPersona(String n) { this.name = n; }
    }
}
