package net.skinwalker;

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
    
    // Register Items
    public static final Item DIRECTOR_WAND = new DirectorWand(new FabricItemSettings().maxCount(1));
    public static final Item SKINWALKER_DETECTOR = new net.skinwalker.items.SkinwalkerDetector(new FabricItemSettings().maxCount(1));
    public static final Item TRANSFORMATION_POTION = new net.skinwalker.items.TransformationPotion(new FabricItemSettings().maxCount(16));
    public static final Item CURE_ITEM = new net.skinwalker.items.CureItem(new FabricItemSettings().maxCount(16));

    @Override
    public void onInitialize() {
        System.out.println("Skinwalker Roleplay v2 Loading...");
        
        // Register Items
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "director_wand"), DIRECTOR_WAND);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "skinwalker_detector"), SKINWALKER_DETECTOR);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "transformation_potion"), TRANSFORMATION_POTION);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "cure_item"), CURE_ITEM);
        
        // Initialize Core Systems
        RoleSystem.init();
        StorySystem.init();
        BotSystem.init();
        SkinSystem.init();
        PlayerCheckSystem.init();
        AbilitySystem.init();
        
        // Initialize Enhanced Systems
        AdvancedSkinSystem.init();
        ParticleSystem.init();
        WinLossSystem.init();
        PlayerDataTracker.init();
        GameModeSystem.init();
        
        // Initialize New Systems
        MultiAPISystem.init();
        InfectionSystem.init();
        CoinSystem.init();
        ShopSystem.init();
        TradingSystem.init();
        AntiAFKSystem.init();
        StripeSystem.init();
        
        // Give wand on join if OP
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.player.hasPermissionLevel(2)) {
                handler.player.sendMessage(Text.literal("§c[System] §7Run /give @s skinwalker:director_wand to control the event."), false);
            }
        });
    }
}
