import os
import zipfile

# --- CONFIGURATION ---
# PASTE YOUR API KEY HERE
API_KEY = "AIzaSyDX_MkrzqjE3q1jiDqSB5d95cV2fU6cMJw" 
# ---------------------

print("Generating Skinwalker Mod v2.0 (Roleplay Update)...")

# 1. Gradle Properties
gradle_props = """
minecraft_version=1.20.1
yarn_mappings=1.20.1+build.10
loader_version=0.15.11
fabric_version=0.92.2+1.20.1
mod_version=2.0.0
maven_group=net.skinwalker
archives_base_name=skinwalker-roleplay
java_version=17
"""

# 2. Build Gradle (Using 8.7/8.10 compatible setup)
build_gradle = """
plugins {
    id 'fabric-loom' version '1.7-SNAPSHOT'
    id 'maven-publish'
}
version = project.mod_version
group = project.maven_group
base { archivesName = project.archives_base_name }
repositories {
    mavenCentral()
    maven { url "https://maven.fabricmc.net/" }
}
dependencies {
    minecraft "com.mojang:minecraft:${project.minecraft_version}"
    mappings "net.fabricmc:yarn:${project.yarn_mappings}:v2"
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"
}
processResources {
    inputs.property "version", project.version
    filesMatching("fabric.mod.json") { expand "version": project.version }
}
tasks.withType(JavaCompile).configureEach { it.options.release = 17 }
java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
"""

# 3. Fabric Mod JSON
fabric_mod_json = """
{
  "schemaVersion": 1,
  "id": "skinwalker",
  "version": "${version}",
  "name": "Skinwalker Roleplay",
  "description": "Full event mod with Roles, Abilities, and Story Mode.",
  "authors": ["You"],
  "license": "MIT",
  "icon": "assets/skinwalker/icon.png",
  "environment": "*",
  "entrypoints": {
    "main": [ "net.skinwalker.SkinwalkerMod" ]
  },
  "depends": {
    "fabricloader": ">=0.15.11",
    "minecraft": "~1.20.1",
    "java": ">=17",
    "fabric-api": "*"
  }
}
"""

# --- JAVA FILES ---

# File 1: Main Mod Class
java_main = """package net.skinwalker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SkinwalkerMod implements ModInitializer {
    
    public static final String MOD_ID = "skinwalker";
    
    // Register the Director Wand
    public static final Item DIRECTOR_WAND = new DirectorWand(new FabricItemSettings().maxCount(1));

    @Override
    public void onInitialize() {
        System.out.println("Skinwalker Roleplay v2 Loading...");
        
        // Register Items
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "director_wand"), DIRECTOR_WAND);
        
        // Initialize Systems
        RoleSystem.init();
        StorySystem.init();
        BotSystem.init();
        
        // Give wand on join if OP
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.player.hasPermissionLevel(2)) {
                handler.player.sendMessage(Text.literal("§c[System] §7Run /give @s skinwalker:director_wand to control the event."), false);
            }
        });
    }
}
"""

# File 2: Role System (Abilities)
java_roles = """package net.skinwalker;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoleSystem {
    
    public enum Role { DREAM, TECHNO, BEAST, CRAFTEE, SKINWALKER, SURVIVOR }
    private static final Map<UUID, Role> playerRoles = new HashMap<>();

    public static void init() {
        // Command: /role set <player> <role>
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("role")
                .requires(s -> s.hasPermissionLevel(2))
                .then(CommandManager.literal("set")
                    .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                    .then(CommandManager.argument("role", StringArgumentType.word())
                    .executes(ctx -> {
                        ServerPlayerEntity p = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                        String rStr = StringArgumentType.getString(ctx, "role").toUpperCase();
                        try {
                            Role r = Role.valueOf(rStr);
                            setRole(p, r);
                            ctx.getSource().sendFeedback(() -> Text.literal("Set " + p.getName().getString() + " to " + r.name()), true);
                        } catch (Exception e) {
                            ctx.getSource().sendFeedback(() -> Text.literal("Invalid Role. Use: DREAM, TECHNO, BEAST, CRAFTEE, SKINWALKER"), false);
                        }
                        return 1;
                    })))));
        });

        // Tick Event for Abilities
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                Role role = playerRoles.getOrDefault(player.getUuid(), Role.SURVIVOR);
                applyAbilities(player, role);
            }
        });
    }

    public static void setRole(ServerPlayerEntity player, Role role) {
        playerRoles.put(player.getUuid(), role);
        player.sendMessage(Text.literal("§aYou are now: §l" + role.name()), true);
        
        // Initial Setup (Skins)
        String cmd = "skin " + player.getName().getString() + " set ";
        switch (role) {
            case DREAM: player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), cmd + "Dream"); break;
            case TECHNO: player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), cmd + "Technoblade"); break;
            case BEAST: player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), cmd + "JimmyDonaldson"); break;
            case CRAFTEE: player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), cmd + "Parker_Games"); break;
            case SKINWALKER: player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), cmd + "https://files.catbox.moe/cb8c8a.png"); break;
        }
    }

    private static void applyAbilities(ServerPlayerEntity player, Role role) {
        switch (role) {
            case DREAM:
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, 1, false, false));
                break;
            case TECHNO:
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20, 0, false, false));
                break;
            case BEAST:
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20, 0, false, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 20, 0, false, false));
                break;
            case SKINWALKER:
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 20, 0, false, false));
                // Sneak to go invisible
                if (player.isSneaking()) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 10, 0, false, false));
                }
                break;
        }
    }
}
"""

# File 3: Story System (The Script)
java_story = """package net.skinwalker;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.StringArgumentType;

public class StorySystem {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("story")
                .requires(s -> s.hasPermissionLevel(2))
                .then(CommandManager.argument("chapter", StringArgumentType.string())
                .executes(ctx -> playChapter(ctx.getSource(), StringArgumentType.getString(ctx, "chapter")))));
        });
    }

    public static int playChapter(ServerCommandSource source, String chapter) {
        switch (chapter) {
            case "intro":
                broadcast(source, "§eDream joined the game");
                broadcast(source, "§eMrBeast joined the game");
                broadcast(source, "§eTechnoblade joined the game");
                break;
            case "phase1":
                broadcast(source, "§b[Dream] Yo what's up — heard someone’s hacking this world.");
                broadcast(source, "§6[MrBeast] First one to find 3 diamonds gets OP!");
                broadcast(source, "§a[Craftee] Something's not crafting right here...");
                break;
            case "corrupt":
                broadcast(source, "§4§o[Dream] H̷͍̐̈́͆ě̷͈̲͉͔̬l̷̢̖͠p̴̭̘̮̐̒̕");
                broadcast(source, "§4[MrBeast] That wasn't part of the plan...");
                broadcast(source, "§c[Technoblade] What the hell is this seed.");
                playSound(source, "minecraft:entity.ender_dragon.growl");
                break;
            case "news":
                broadcast(source, "§c[NEWS] Craftee is missing. Technoblade corrupted. Dream overwritten.");
                broadcast(source, "§4§l[BREAKING NEWS] SkinWalker Entities are breached on the Server. Trust no one.");
                break;
            case "endgame":
                source.getServer().getCommandManager().executeWithPrefix(source, "gamemode adventure @a");
                source.getServer().getCommandManager().executeWithPrefix(source, "execute in minecraft:the_end run tp @a 0 100 0");
                broadcast(source, "§4§lThe End Begins");
                playSound(source, "minecraft:entity.wither.spawn");
                broadcast(source, "§7[Skinwalker] I used all their faces. Now I wear yours.");
                break;
        }
        return 1;
    }

    private static void broadcast(ServerCommandSource source, String msg) {
        source.getServer().getPlayerManager().broadcast(Text.literal(msg), false);
    }
    
    private static void playSound(ServerCommandSource source, String sound) {
        source.getServer().getCommandManager().executeWithPrefix(source, "playsound " + sound + " master @a ~ ~ ~ 1 1");
    }
}
"""

# File 4: Director Wand (The Menu)
java_wand = """package net.skinwalker;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DirectorWand extends Item {
    public DirectorWand(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            user.sendMessage(Text.literal("§c§l--- [ DIRECTOR MENU ] ---"), false);
            
            // Button 1: Start
            user.sendMessage(Text.literal("§a[▶ START EVENT]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/story intro"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Fake Joins")))), false);
                
            // Button 2: Phase 1
            user.sendMessage(Text.literal("§e[1️⃣ PHASE 1: HAPPY]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/story phase1"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Normal Chat")))), false);

            // Button 3: Corrupt
            user.sendMessage(Text.literal("§4[💀 PHASE 2: CORRUPT]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/story corrupt"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Scary Sounds & Skins")))), false);
                
            // Button 4: Summon Bots
            user.sendMessage(Text.literal("§b[🤖 SUMMON BOTS]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skinwalker summon Dream"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Spawns AI Dream")))), false);
            
            // Button 5: End Game
            user.sendMessage(Text.literal("§0[⬛ END GAME]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/story endgame"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Teleport to End & Kill")))), false);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
"""

# File 5: Bot System (AI Integration)
java_bot = """package net.skinwalker;

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

public class BotSystem {

    // PASTE KEY FROM MAIN SCRIPT
    private static final String API_KEY = "AIzaSyDX_MkrzqjE3q1jiDqSB5d95cV2fU6cMJw"; 
    private static final String MODEL_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
    
    private static final Map<String, Boolean> activeBots = new HashMap<>();

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            dispatcher.register(CommandManager.literal("skinwalker")
                .requires(s -> s.hasPermissionLevel(2))
                .then(CommandManager.literal("summon")
                    .then(CommandManager.argument("name", StringArgumentType.word())
                    .executes(ctx -> {
                        String name = StringArgumentType.getString(ctx, "name");
                        activeBots.put(name, false); // False = Normal Mode
                        ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), "player " + name + " spawn at ~ ~ ~");
                        // Set Skin
                        String skin = name.equals("Dream") ? "Dream" : "Steve";
                        ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), "skin " + name + " set " + skin);
                        return 1;
                    }))));
        });

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            if (activeBots.isEmpty()) return;
            String content = message.getContent().getString();
            for (String botName : activeBots.keySet()) {
                if (content.toLowerCase().contains(botName.toLowerCase())) {
                    generateResponse(botName, content, sender);
                }
            }
        });
    }

    private static void generateResponse(String botName, String msg, ServerPlayerEntity player) {
        CompletableFuture.runAsync(() -> {
            try {
                String prompt = "You are " + botName + " in Minecraft. Reply to: " + msg;
                String jsonInput = "{\\"contents\\": [{\\"parts\\":[{\\"text\\": \\"" + prompt + "\\"}]}]}";
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(MODEL_URL)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonInput)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                if (json.has("candidates")) {
                    String reply = json.getAsJsonArray("candidates").get(0).getAsJsonObject().getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject().get("text").getAsString();
                    player.getServer().execute(() -> player.getServer().getPlayerManager().broadcast(Text.literal("<" + botName + "> " + reply.trim()).formatted(Formatting.YELLOW), false));
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}
"""

# --- GENERATION LOGIC ---
print("Writing source files...")

# Create directories
os.makedirs("src/main/resources/assets/skinwalker", exist_ok=True)
os.makedirs("src/main/java/net/skinwalker", exist_ok=True)
os.makedirs("gradle/wrapper", exist_ok=True)

# Write Config Files
with open("gradle.properties", "w", encoding='utf-8') as f: f.write(gradle_props)
with open("build.gradle", "w", encoding='utf-8') as f: f.write(build_gradle)
with open("src/main/resources/fabric.mod.json", "w", encoding='utf-8') as f: f.write(fabric_mod_json)
with open("settings.gradle", "w", encoding='utf-8') as f: f.write("rootProject.name = 'skinwalker-roleplay'")

# Write Java Files
with open("src/main/java/net/skinwalker/SkinwalkerMod.java", "w", encoding='utf-8') as f: f.write(java_main)
with open("src/main/java/net/skinwalker/RoleSystem.java", "w", encoding='utf-8') as f: f.write(java_roles)
with open("src/main/java/net/skinwalker/StorySystem.java", "w", encoding='utf-8') as f: f.write(java_story)
with open("src/main/java/net/skinwalker/DirectorWand.java", "w", encoding='utf-8') as f: f.write(java_wand)
with open("src/main/java/net/skinwalker/BotSystem.java", "w", encoding='utf-8') as f: f.write(java_bot)

# Write Gradle Wrapper Properties (8.10.2)
wrapper_props = """distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.10.2-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
networkTimeout=10000
"""
with open("gradle/wrapper/gradle-wrapper.properties", "w", encoding='utf-8') as f: f.write(wrapper_props)

print("------------------------------------------------")
print("SUCCESS! Mod v2.0 Source Generated.")
print("1. Open Command Prompt here.")
print("2. Run: gradlew build")
print("3. Look in build/libs for 'skinwalker-roleplay-2.0.0.jar'")
print("------------------------------------------------")