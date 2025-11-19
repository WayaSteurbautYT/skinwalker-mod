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
 * Handles skin checking, conversion, and skinwalker skin management
 */
public class SkinSystem {
    
    // Skinwalker skin URLs from commands file
    private static final Map<String, String> SKINWALKER_SKINS = new HashMap<>();
    private static final Map<String, String> NORMAL_SKINS = new HashMap<>();
    
    // Track original skins before conversion
    private static final Map<UUID, String> originalSkins = new HashMap<>();
    
    static {
        // Initialize skinwalker skins
        SKINWALKER_SKINS.put("normal", "https://files.catbox.moe/cb8c8a.png");
        SKINWALKER_SKINS.put("waya", "https://files.catbox.moe/qxdjf2.png");
        SKINWALKER_SKINS.put("lucas", "https://files.catbox.moe/7fs0c4.png");
        SKINWALKER_SKINS.put("babi", "https://files.catbox.moe/5mbyi0.png");
        SKINWALKER_SKINS.put("dream", "https://files.catbox.moe/9gf4xa.png");
        SKINWALKER_SKINS.put("technoblade", "https://files.catbox.moe/6v0y8i.png");
        SKINWALKER_SKINS.put("craftee", "https://files.catbox.moe/v2ibhg.png");
        SKINWALKER_SKINS.put("mrbeast", "https://files.catbox.moe/o456fl.png");
        SKINWALKER_SKINS.put("preston", "https://files.catbox.moe/fbjewy.png");
        SKINWALKER_SKINS.put("steve", "https://files.catbox.moe/zq4kyt.png");
        
        // Normal skin mappings
        NORMAL_SKINS.put("waya", "WayaSteurbautYTR");
        NORMAL_SKINS.put("lucas", "Udnof007");
        NORMAL_SKINS.put("dream", "Dream");
        NORMAL_SKINS.put("craftee", "Parker_Games");
        NORMAL_SKINS.put("mrbeast", "JimmyDonaldson");
        NORMAL_SKINS.put("technoblade", "Technoblade");
        NORMAL_SKINS.put("preston", "PrestonPlayz");
        NORMAL_SKINS.put("babi", "https://files.catbox.moe/ur57tg.png");
    }
    
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            // /skinwalker convert <player> [type]
            dispatcher.register(CommandManager.literal("skinwalker")
                .requires(s -> s.hasPermissionLevel(2))
                .then(CommandManager.literal("convert")
                    .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                        .then(CommandManager.argument("type", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                            String type = StringArgumentType.getString(ctx, "type").toLowerCase();
                            convertToSkinwalker(player, type);
                            ctx.getSource().sendFeedback(() -> Text.literal("Converted " + player.getName().getString() + " to skinwalker: " + type), true);
                            return 1;
                        }))
                        .executes(ctx -> {
                            ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                            convertToSkinwalker(player, "normal");
                            ctx.getSource().sendFeedback(() -> Text.literal("Converted " + player.getName().getString() + " to skinwalker"), true);
                            return 1;
                        })))
                .then(CommandManager.literal("restore")
                    .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                    .executes(ctx -> {
                        ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                        restoreSkin(player);
                        ctx.getSource().sendFeedback(() -> Text.literal("Restored " + player.getName().getString() + "'s original skin"), true);
                        return 1;
                    })))
                .then(CommandManager.literal("set")
                    .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                        .then(CommandManager.argument("type", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayerEntity player = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                            String type = StringArgumentType.getString(ctx, "type").toLowerCase();
                            setSkin(player, type, true);
                            ctx.getSource().sendFeedback(() -> Text.literal("Set " + player.getName().getString() + " skin to: " + type), true);
                            return 1;
                        })))));
        });
    }
    
    /**
     * Convert a player to a skinwalker skin
     */
    public static void convertToSkinwalker(ServerPlayerEntity player, String type) {
        UUID uuid = player.getUuid();
        
        // Store original skin if not already stored
        if (!originalSkins.containsKey(uuid)) {
            // Try to get current skin (this is a placeholder - actual skin retrieval would need Mojang API)
            originalSkins.put(uuid, player.getName().getString());
        }
        
        String skinUrl = SKINWALKER_SKINS.get(type.toLowerCase());
        if (skinUrl != null) {
            setSkinFromUrl(player, skinUrl);
        } else {
            // Default to normal skinwalker
            setSkinFromUrl(player, SKINWALKER_SKINS.get("normal"));
        }
    }
    
    /**
     * Restore player's original skin
     */
    public static void restoreSkin(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        String original = originalSkins.remove(uuid);
        
        if (original != null) {
            // Try to restore from player name or URL
            String normalSkin = NORMAL_SKINS.get(original.toLowerCase());
            if (normalSkin != null) {
                if (normalSkin.startsWith("http")) {
                    setSkinFromUrl(player, normalSkin);
                } else {
                    setSkinFromName(player, normalSkin);
                }
            } else {
                // Default restore
                setSkinFromName(player, original);
            }
        }
    }
    
    /**
     * Set skin by type (normal or skinwalker)
     */
    public static void setSkin(ServerPlayerEntity player, String type, boolean isSkinwalker) {
        if (isSkinwalker) {
            String skinUrl = SKINWALKER_SKINS.get(type.toLowerCase());
            if (skinUrl != null) {
                setSkinFromUrl(player, skinUrl);
            }
        } else {
            String skinName = NORMAL_SKINS.get(type.toLowerCase());
            if (skinName != null) {
                if (skinName.startsWith("http")) {
                    setSkinFromUrl(player, skinName);
                } else {
                    setSkinFromName(player, skinName);
                }
            }
        }
    }
    
    /**
     * Set skin from URL
     */
    private static void setSkinFromUrl(ServerPlayerEntity player, String url) {
        String cmd = "skin player " + player.getName().getString() + " set " + url;
        try {
            player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), cmd);
        } catch (Exception e) {
            player.sendMessage(Text.literal("§cError setting skin from URL"), false);
        }
    }
    
    /**
     * Set skin from player name
     */
    private static void setSkinFromName(ServerPlayerEntity player, String name) {
        String cmd = "skin set " + name;
        try {
            player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), cmd);
        } catch (Exception e) {
            player.sendMessage(Text.literal("§cError setting skin from name"), false);
        }
    }
    
    /**
     * Get skinwalker skin URL by type
     */
    public static String getSkinwalkerSkin(String type) {
        return SKINWALKER_SKINS.get(type.toLowerCase());
    }
    
    /**
     * Check if player has been converted
     */
    public static boolean isConverted(UUID uuid) {
        return originalSkins.containsKey(uuid);
    }
}

