package net.skinwalker;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.concurrent.CompletableFuture;

/**
 * Story System - Handles all story events and commands from the commands file
 */
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
        switch (chapter.toLowerCase()) {
            case "intro":
                playIntro(source);
                break;
            case "phase1":
            case "happy":
                playPhase1(source);
                break;
            case "corrupt":
            case "phase2":
                playCorrupt(source);
                break;
            case "news":
                playNews(source);
                break;
            case "warnings":
                playWarnings(source);
                break;
            case "warnings2":
                playWarnings2(source);
                break;
            case "waya_join":
                playWayaJoin(source);
                break;
            case "endgame":
            case "final":
                playEndgame(source);
                break;
            case "dream_normal":
                playDreamNormal(source);
                break;
            case "dream_corrupt":
                playDreamCorrupt(source);
                break;
            case "dream_corrupt2":
                playDreamCorrupt2(source);
                break;
            case "mrbeast_normal":
                playMrBeastNormal(source);
                break;
            case "mrbeast_corrupt":
                playMrBeastCorrupt(source);
                break;
            case "mrbeast_weird":
                playMrBeastWeird(source);
                break;
            case "craftee_normal":
                playCrafteeNormal(source);
                break;
            case "craftee_corrupt":
                playCrafteeCorrupt(source);
                break;
            case "techno_normal":
                playTechnoNormal(source);
                break;
            case "techno_corrupt":
                playTechnoCorrupt(source);
                break;
            case "skinwalker_plan1":
                playSkinwalkerPlan1(source);
                break;
            case "skinwalker_plan2":
                playSkinwalkerPlan2(source);
                break;
            case "paranoia":
                playParanoia(source);
                break;
            default:
                source.sendFeedback(() -> Text.literal("Unknown chapter. Use: intro, phase1, corrupt, news, warnings, endgame"), false);
        }
        return 1;
    }

    // Intro - Fake joins
    private static void playIntro(ServerCommandSource source) {
        broadcast(source, "§eDream joined the game");
        broadcast(source, "§eMrBeast joined the game");
        broadcast(source, "§eCraftee joined the game");
        broadcast(source, "§eTechnoblade joined the game");
        broadcast(source, "§ePrestonPlayz joined the game");
        broadcast(source, "§eUdnof007 joined the game");
        broadcast(source, "§eWayaSteurbautYTR joined the game");
    }

    // Phase 1 - Happy/Normal messages
    private static void playPhase1(ServerCommandSource source) {
        broadcast(source, "§b[Dream] Yo what's up — heard someone's hacking this world.");
        broadcast(source, "§6[MrBeast] First one to find 3 diamonds gets OP!");
        broadcast(source, "§a[Craftee] Something's not crafting right here...");
        broadcast(source, "§c[Technoblade] What the hell is this seed.");
        executeCommand(source, "tellraw @a {\"text\":\"[SERVER] Admin override: WayaSteurbautYTR has been accessed by Technoblade.\",\"color\":\"gray\"}");
    }

    // Corrupt Phase
    private static void playCorrupt(ServerCommandSource source) {
        // Dream corrupted
        executeCommand(source, "tellraw @a {\"text\":\"[Dream] H̷͍̐̈́͆ě̷͈̲͉͔̬l̷̢̖͠p̴̭̘̮̐̒̕\",\"color\":\"dark_red\",\"italic\":true}");
        
        // Dream corrupted part 2
        executeCommand(source, "tellraw @a {\"text\":\"[Dream] W̸̪̥̲̩̱̜̅ḣ̶̻͎̊̽̒́̓̐̐y̷̻̹͈̎̏́͒͂̆͋̔ ̶̢̥̯͛͐̾̽͝d̸̠̠̘̟͓͙́͒͗i̷̺̪̖̮̫͓͛͌͆̄̈́̾̚͝d̶̘̳̫̫̜̏̄͆̓͒͛̈́ ̶̰͐̈́y̵̳͖̋̔̈́͑o̶̡͙͕̤͕̘͈̎͂̽́̓̀͘ư̴̩̩̞̲̱̍̇̽͆͝͝͝ͅ ̶̯͛b̶̜́͗̌̋͗͝r̷̰̘̤͙̜̯̈́͒͂̔͘͝͝į̶̪̀̑͊̎̓̇̕n̶̢̯̹̟̈́̅͘͠͠g̵̛̻̟̞̯̎̏̈́̚ ̶̛̜̤͙̪̤̪͒̓͂͛͐̕̕h̴̢̦̱̮̳̩̾́͗͐ͅi̵̢̟͓͊m̶̩̀͊͘ ̶͓͈̩̲͇̺̍b̶̞̜̍̀̄̾͒̏͒̕͝ȧ̵̺̩͂̄͠c̵̨̍̐͋k̴̡̺̱̪̮̓̽̈́̑̈́ͅ.̵͓̲̼̯̏̚͘\",\"color\":\"dark_purple\"}");
        
        broadcast(source, "§4[MrBeast] That wasn't part of the plan...");
        playSound(source, "minecraft:entity.ender_dragon.growl", 1.0f, 0.5f);
    }

    // News broadcasts
    private static void playNews(ServerCommandSource source) {
        broadcast(source, "§c[NEWS] Craftee is missing. Technoblade corrupted. Dream overwritten. MrBeast... unknown.");
        broadcast(source, "§4§l[BREAKING NEWS] SkinWalker Entities are breached on the Server. Trust no one. If your friends disappear and come back, check them immediately. Even YouTubers can't be trusted.");
        broadcast(source, "§c[INFO] MrBeast has rejoined... but something feels off. He's not speaking.");
        executeCommand(source, "tellraw @a {\"text\":\"[SERVER] Warning: Unknown player data signature detected. Player \\\"Technoblade\\\" integrity = CORRUPTED.\",\"color\":\"dark_gray\"}");
        broadcast(source, "§6[TIP] If a player stops talking, won't sleep, and stares at you—RUN.");
        broadcast(source, "§4[ALERT] Multiple disappearances confirmed. SkinWalkers can copy skins, voices, and habits...");
        executeCommand(source, "tellraw @a {\"text\":\"[SYSTEM LOG] Dream sent a private message to the Skinwalker. Message content: \\\"How do I join you?\\\"\",\"color\":\"gray\"}");
        broadcast(source, "§4§l[ADMIN WARNING] Do NOT trust names. Check behavior. Check movement. Check chat patterns.");
    }

    // Warnings Part 1
    private static void playWarnings(ServerCommandSource source) {
        executeCommand(source, "title @a title {\"text\":\"HE'S BACK\",\"color\":\"dark_red\",\"bold\":true}");
        executeCommand(source, "title @a subtitle {\"text\":\"You let him in...\",\"color\":\"gray\",\"italic\":true}");
        playSound(source, "minecraft:entity.ender_dragon.growl", 1.0f, 0.5f);
    }

    // Warnings Part 2
    private static void playWarnings2(ServerCommandSource source) {
        executeCommand(source, "title @a title {\"text\":\"RUN.\",\"color\":\"red\",\"bold\":true}");
        executeCommand(source, "title @a subtitle {\"text\":\"He's becoming all of us...\",\"color\":\"dark_gray\",\"italic\":true}");
        playSound(source, "minecraft:entity.ender_dragon.growl", 1.0f, 0.5f);
        playSound(source, "minecraft:block.portal.trigger", 1.0f, 0.5f);
        broadcast(source, "§c[NEWS] Craftee is missing. Technoblade corrupted. Dream overwritten. MrBeast... unknown.");
        broadcast(source, "§7[WARNING] If he gets to the End... it's over.");
    }

    // Waya Join
    private static void playWayaJoin(ServerCommandSource source) {
        executeCommand(source, "title @a title {\"text\":\"IT'S ME\",\"color\":\"red\",\"bold\":true}");
        executeCommand(source, "title @a subtitle {\"text\":\"But not the way you remember...\",\"color\":\"gray\",\"italic\":true}");
        playSound(source, "minecraft:entity.warden.angry", 1.0f, 1.0f);
    }

    // Endgame
    private static void playEndgame(ServerCommandSource source) {
        executeCommand(source, "gamemode adventure @a");
        executeCommand(source, "execute in minecraft:the_end run tp @a ~ ~ ~");
        executeCommand(source, "title @a title {\"text\":\"The End Begins\",\"color\":\"dark_red\",\"bold\":true}");
        playSound(source, "minecraft:entity.ender_dragon.growl", 1.0f, 0.8f);
        broadcast(source, "§7[Skinwalker] I used all their faces. Now I wear yours.");
        executeCommand(source, "title @a title {\"text\":\"You Let Him In\",\"color\":\"dark_red\",\"bold\":true}");
        executeCommand(source, "title @a subtitle {\"text\":\"Now… the world ends.\",\"color\":\"gray\"}");
        
        // Delay kill command
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
                source.getServer().execute(() -> {
                    executeCommand(source, "kill @a");
                    playSound(source, "minecraft:entity.wither.spawn", 1.0f, 1.0f);
                    broadcast(source, "§7[BREAKING NEWS] WayaSteurbautYTR has vanished. Last seen entering the End.");
                    broadcast(source, "§c[NEWS] All YouTubers presumed deleted.");
                    broadcast(source, "§4[SYSTEM] The Skinwalker has won.");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    // Dream messages
    private static void playDreamNormal(ServerCommandSource source) {
        broadcast(source, "§b[Dream] Yo what's up — heard someone's hacking this world.");
    }

    private static void playDreamCorrupt(ServerCommandSource source) {
        executeCommand(source, "tellraw @a {\"text\":\"[Dream] H̷͍̐̈́͆ě̷͈̲͉͔̬l̷̢̖͠p̴̭̘̮̐̒̕\",\"color\":\"dark_red\",\"italic\":true}");
    }

    private static void playDreamCorrupt2(ServerCommandSource source) {
        executeCommand(source, "tellraw @a {\"text\":\"[Dream] W̸̪̥̲̩̱̜̅ḣ̶̻͎̊̽̒́̓̐̐y̷̻̹͈̎̏́͒͂̆͋̔ ̶̢̥̯͛͐̾̽͝d̸̠̠̘̟͓͙́͒͗i̷̺̪̖̮̫͓͛͌͆̄̈́̾̚͝d̶̘̳̫̫̜̏̄͆̓͒͛̈́ ̶̰͐̈́y̵̳͖̋̔̈́͑o̶̡͙͕̤͕̘͈̎͂̽́̓̀͘ư̴̩̩̞̲̱̍̇̽͆͝͝͝ͅ ̶̯͛b̶̜́͗̌̋͗͝r̷̰̘̤͙̜̯̈́͒͂̔͘͝͝į̶̪̀̑͊̎̓̇̕n̶̢̯̹̟̈́̅͘͠͠g̵̛̻̟̞̯̎̏̈́̚ ̶̛̜̤͙̪̤̪͒̓͂͛͐̕̕h̴̢̦̱̮̳̩̾́͗͐ͅi̵̢̟͓͊m̶̩̀͊͘ ̶͓͈̩̲͇̺̍b̶̞̜̍̀̄̾͒̏͒̕͝ȧ̵̺̩͂̄͠c̵̨̍̐͋k̴̡̺̱̪̮̓̽̈́̑̈́ͅ.̵͓̲̼̯̏̚͘\",\"color\":\"dark_purple\"}");
    }

    // MrBeast messages
    private static void playMrBeastNormal(ServerCommandSource source) {
        broadcast(source, "§6[MrBeast] First one to find 3 diamonds gets OP!");
    }

    private static void playMrBeastCorrupt(ServerCommandSource source) {
        broadcast(source, "§4[MrBeast] That wasn't part of the plan...");
    }

    private static void playMrBeastWeird(ServerCommandSource source) {
        broadcast(source, "§6[MrBeast] Go to Y level 0. Trust me.");
        executeCommand(source, "tellraw @a {\"text\":\"[MrBeast] 404 Player Not Found\",\"color\":\"dark_red\",\"italic\":true}");
    }

    // Craftee messages
    private static void playCrafteeNormal(ServerCommandSource source) {
        broadcast(source, "§a[Craftee] Something's not crafting right here...");
    }

    private static void playCrafteeCorrupt(ServerCommandSource source) {
        executeCommand(source, "tellraw @a {\"text\":\"[Craftee] Cra...Cra...C̵r̵a̷f̵t̵e̶e̸ i̵s̵ ̵n̸o̴t̸ ̵f̸u̵n̸n̵y̷ ̸a̵n̶y̴m̴o̷r̸e̴.̸.̴.̵\",\"color\":\"red\",\"italic\":true}");
        playSound(source, "minecraft:entity.wither.ambient", 1.0f, 1.0f);
    }

    // Technoblade messages
    private static void playTechnoNormal(ServerCommandSource source) {
        broadcast(source, "§c[Technoblade] What the hell is this seed.");
        executeCommand(source, "tellraw @a {\"text\":\"[SERVER] Admin override: WayaSteurbautYTR has been accessed by Technoblade.\",\"color\":\"gray\"}");
    }

    private static void playTechnoCorrupt(ServerCommandSource source) {
        executeCommand(source, "tellraw @a {\"text\":\"[Dream] H̷͍̐̈́͆ě̷͈̲͉͔̬l̷̢̖͠p̴̭̘̮̐̒̕\",\"color\":\"dark_red\",\"italic\":true}");
    }

    // Skinwalker plan messages
    private static void playSkinwalkerPlan1(ServerCommandSource source) {
        executeCommand(source, "tellraw @a {\"text\":\"[??]: Phase One is complete. The player base is confused.\",\"color\":\"dark_gray\"}");
        executeCommand(source, "tellraw @a {\"text\":\"[??]: Good. Let their trust rot. The more fear, the easier they break.\",\"color\":\"dark_gray\"}");
        executeCommand(source, "tellraw @a {\"text\":\"[??]: I have already taken one of the big ones. They don't even suspect.\",\"color\":\"dark_gray\"}");
        executeCommand(source, "tellraw @a {\"text\":\"[??]: Excellent. Continue copying the YouTubers. When the event begins, we strike together.\",\"color\":\"dark_gray\"}");
        executeCommand(source, "tellraw @a {\"text\":\"[??]: The End will be their last hope. And their final mistake.\",\"color\":\"dark_gray\"}");
    }

    private static void playSkinwalkerPlan2(ServerCommandSource source) {
        executeCommand(source, "tellraw @a {\"text\":\"[Skinwalker]: They welcomed us. Laughed with us. Gave us roles, gear, trust...\",\"color\":\"dark_purple\"}");
        executeCommand(source, "tellraw @a {\"text\":\"[Skinwalker]: Now we wear their faces. Speak in their tone. We ARE them.\",\"color\":\"dark_purple\"}");
        executeCommand(source, "tellraw @a {\"text\":\"[Skinwalker]: Soon, not even the real YouTubers will remember who they are.\",\"color\":\"dark_purple\"}");
        executeCommand(source, "tellraw @a {\"text\":\"[Skinwalker]: When the End opens, we'll gather them. One final hunt. No respawns.\",\"color\":\"dark_purple\"}");
    }

    // Paranoia messages
    private static void playParanoia(ServerCommandSource source) {
        playNews(source);
    }

    // Achievement messages
    public static void broadcastAchievement(ServerCommandSource source, String player, String achievement) {
        executeCommand(source, "tellraw @a {\"text\":\"[Advancement Made] " + player + " has unlocked: \\\"" + achievement + "\\\"\",\"color\":\"yellow\"}");
    }

    // Helper methods
    private static void broadcast(ServerCommandSource source, String msg) {
        source.getServer().getPlayerManager().broadcast(Text.literal(msg), false);
    }
    
    private static void playSound(ServerCommandSource source, String sound, float volume, float pitch) {
        executeCommand(source, "playsound " + sound + " master @a ~ ~ ~ " + volume + " " + pitch);
    }
    
    private static void executeCommand(ServerCommandSource source, String command) {
        try {
            source.getServer().getCommandManager().executeWithPrefix(source, command);
        } catch (Exception e) {
            System.err.println("Error executing command: " + command);
            e.printStackTrace();
        }
    }
}

