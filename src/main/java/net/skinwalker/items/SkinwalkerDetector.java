package net.skinwalker.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.skinwalker.PlayerCheckSystem;
import net.skinwalker.RoleSystem;

/**
 * Skinwalker Detector - Item to check if nearby players are skinwalkers
 */
public class SkinwalkerDetector extends Item {
    public SkinwalkerDetector(Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            // Check nearby players
            int detected = 0;
            for (PlayerEntity player : world.getPlayers()) {
                if (player != user && player.distanceTo(user) < 10) {
                    RoleSystem.Role role = RoleSystem.getPlayerRole((net.minecraft.server.network.ServerPlayerEntity) player);
                    if (role == RoleSystem.Role.SKINWALKER) {
                        detected++;
                        user.sendMessage(Text.literal("§4⚠ DETECTED: " + player.getName().getString() + " is a SKINWALKER!"), false);
                    }
                }
            }
            
            if (detected == 0) {
                user.sendMessage(Text.literal("§aNo skinwalkers detected nearby."), false);
            } else {
                user.sendMessage(Text.literal("§c" + detected + " skinwalker(s) detected!"), false);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}

