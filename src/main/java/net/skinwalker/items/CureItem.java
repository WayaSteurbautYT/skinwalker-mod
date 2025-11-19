package net.skinwalker.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.skinwalker.RoleSystem;
import net.skinwalker.SkinSystem;
import net.skinwalker.ParticleSystem;

/**
 * Cure Item - Remove skinwalker status
 */
public class CureItem extends Item {
    public CureItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverUser = (ServerPlayerEntity) user;
            
            RoleSystem.Role currentRole = RoleSystem.getPlayerRole(serverUser);
            if (currentRole == RoleSystem.Role.SKINWALKER) {
                // Cure the player
                RoleSystem.setRole(serverUser, RoleSystem.Role.SURVIVOR);
                SkinSystem.restoreSkin(serverUser);
                ParticleSystem.spawnVictoryParticles(serverUser);
                
                serverUser.sendMessage(Text.literal("§a§lYou have been cured! You are now a SURVIVOR."), true);
                
                // Consume item
                ItemStack stack = user.getStackInHand(hand);
                if (!user.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
                
                return TypedActionResult.success(stack);
            } else {
                serverUser.sendMessage(Text.literal("§7You are not a skinwalker."), false);
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}

