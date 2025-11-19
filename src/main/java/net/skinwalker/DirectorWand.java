package net.skinwalker;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Director Wand - Control item for event directors
 */
public class DirectorWand extends Item {
    public DirectorWand(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user.hasPermissionLevel(2)) {
            user.sendMessage(Text.literal("§c§l=== [ DIRECTOR MENU ] ==="), false);
            
            // Story Controls
            user.sendMessage(Text.literal("§b§lStory Controls:"), false);
            user.sendMessage(Text.literal("§a[▶ START EVENT]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/story intro"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Fake Joins")))), false);
            user.sendMessage(Text.literal("§e[1️⃣ PHASE 1: HAPPY]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/story phase1"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Normal Chat")))), false);
            user.sendMessage(Text.literal("§4[💀 PHASE 2: CORRUPT]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/story corrupt"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Scary Sounds & Skins")))), false);
            user.sendMessage(Text.literal("§5[⚠ WARNINGS]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/story warnings"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Warning Messages")))), false);
            user.sendMessage(Text.literal("§0[⬛ END GAME]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/story endgame"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Teleport to End & Kill")))), false);
            
            // Bot Controls
            user.sendMessage(Text.literal("§b§lBot Controls:"), false);
            user.sendMessage(Text.literal("§b[🤖 SUMMON DREAM]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skinwalker summon Dream"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Spawns AI Dream")))), false);
            user.sendMessage(Text.literal("§b[🤖 SUMMON MRBEAST]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skinwalker summon MrBeast"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Spawns AI MrBeast")))), false);
            user.sendMessage(Text.literal("§b[🤖 SUMMON CRAFTEE]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skinwalker summon Craftee"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Spawns AI Craftee")))), false);
            user.sendMessage(Text.literal("§c[💀 CORRUPT MODE]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skinwalker mode corrupt"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Set all bots to corrupted")))), false);
            user.sendMessage(Text.literal("§a[✅ NORMAL MODE]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skinwalker mode normal"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Set all bots to normal")))), false);
            
            // Utility Commands
            user.sendMessage(Text.literal("§7§lUtility:"), false);
            user.sendMessage(Text.literal("§7[List Bots]")
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skinwalker list"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("List all active bots")))), false);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}

