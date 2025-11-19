package net.skinwalker.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.skinwalker.RoleSystem;
import net.skinwalker.SkinSystem;
import net.skinwalker.ParticleSystem;
import net.skinwalker.WinLossSystem;

/**
 * Transformation Potion - Convert player to skinwalker
 */
public class TransformationPotion extends Item {
    public TransformationPotion(Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverUser = (ServerPlayerEntity) user;
            
            // Convert to skinwalker
            RoleSystem.setRole(serverUser, RoleSystem.Role.SKINWALKER);
            SkinSystem.convertToSkinwalker(serverUser, "normal");
            ParticleSystem.spawnTransformationParticles(serverUser);
            WinLossSystem.recordTransformation(serverUser.getUuid());
            
            serverUser.sendMessage(Text.literal("§4§lYou have been transformed into a SKINWALKER!"), true);
            
            // Consume item
            ItemStack stack = user.getStackInHand(hand);
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}

