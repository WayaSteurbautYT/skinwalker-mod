package net.skinwalker;

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
    
    public enum Role { DREAM, TECHNO, BEAST, CRAFTEE, SKINWALKER, SURVIVOR, YOUTUBER }
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
        player.sendMessage(Text.literal("�aYou are now: �l" + role.name()), true);
        
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
                // Spawn aura particles
                if (player.getServer().getTicks() % 20 == 0) {
                    ParticleSystem.spawnSkinwalkerAura(player);
                }
                // Track corruption
                AdvancedSkinSystem.tickCorruption(player);
                break;
            case YOUTUBER:
                // YouTubers have special abilities
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20, 1, false, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 20, 0, false, false));
                // Can transform to skinwalker when infected
                if (InfectionSystem.getInfectionLevel(player.getUuid()) >= 1.0f) {
                    RoleSystem.setRole(player, Role.SKINWALKER);
                }
                break;
        }
    }
    
    public static Role getPlayerRole(ServerPlayerEntity player) {
        return playerRoles.getOrDefault(player.getUuid(), Role.SURVIVOR);
    }
}
