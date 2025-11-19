# Skinwalker Mod - Comprehensive Development Plan

## Overview
A Minecraft 1.20.1 Fabric mod that transforms the game into an immersive roleplay experience with AI bots, skinwalker mechanics, story events, and multiplayer features.

## Core Features

### 1. Skin System
- **Skin Checking**: Detect and analyze player skins
- **Skinwalker Conversion**: Transform any player skin into a corrupted skinwalker version
- **Skin Database**: Store all skinwalker variants from commands file
- **Dynamic Skin Loading**: Support for URL-based skins and player name lookups

### 2. Role System
- **Roles**: DREAM, TECHNO, BEAST, CRAFTEE, SKINWALKER, SURVIVOR, PRESTON
- **Role Abilities**: Each role has unique passive and active abilities
- **Role Assignment**: Commands and GUI for assigning roles
- **Role Persistence**: Save roles across server restarts

### 3. AI Bot System
- **Bot Spawning**: Summon AI-controlled players
- **Gemini AI Integration**: Use Google Gemini API for bot responses
- **Bot Personalities**: Each bot has unique personality based on role
- **Chat Interaction**: Bots respond to mentions and conversations
- **Normal/Corrupted Modes**: Bots can switch between normal and corrupted states

### 4. Story System
- **Chapter-Based Events**: Organized story progression
- **Broadcast Messages**: Send formatted messages to all players
- **Sound Effects**: Play atmospheric sounds
- **Title/Subtitle System**: Display dramatic titles
- **Command Sequences**: Execute complex command chains
- **All Commands from TXT**: Implement every command from the commands file

### 5. Player Check System
- **Behavior Analysis**: Detect suspicious player behavior
- **Skin Verification**: Check if player skin matches expected
- **Movement Patterns**: Analyze movement for anomalies
- **Chat Pattern Detection**: Identify unusual chat patterns
- **Trust Score**: Calculate player trustworthiness

### 6. Mod Menu Integration
- **Cloth Config**: Settings menu for mod configuration
- **GUI Controls**: Easy-to-use interface for directors
- **Keybindings**: Hotkeys for quick actions
- **Visual Feedback**: Icons and animations

### 7. Abilities & Mechanics
- **Vanish/Reveal**: Invisibility toggle with fake join/leave messages
- **Enderman Transform**: Identity system integration
- **Status Effects**: Role-based effects (speed, strength, etc.)
- **Sneak Abilities**: Special actions while sneaking
- **Cooldowns**: Balance abilities with cooldowns

### 8. Event System
- **Phase Management**: Control story progression
- **Automated Events**: Scheduled story beats
- **Player Triggers**: Events triggered by player actions
- **End Game Sequence**: Final boss-style ending

## File Structure

```
src/main/java/net/skinwalker/
├── SkinwalkerMod.java          # Main mod class
├── RoleSystem.java             # Role management
├── StorySystem.java            # Story events and commands
├── BotSystem.java              # AI bot integration
├── SkinSystem.java             # Skin checking and conversion
├── PlayerCheckSystem.java      # Player verification
├── DirectorWand.java           # Director control item
├── ModMenuConfig.java          # Cloth Config integration
├── AbilitySystem.java          # Ability management
└── EventManager.java           # Event coordination

src/main/resources/
├── fabric.mod.json
├── assets/skinwalker/
│   ├── icon.png
│   └── lang/en_us.json
└── skinwalker-config.json
```

## Implementation Phases

### Phase 1: Core Systems
- [x] Fix Unicode encoding issue
- [ ] SkinSystem with skin checking
- [ ] Enhanced RoleSystem with all roles
- [ ] Complete StorySystem with all commands

### Phase 2: AI & Bots
- [ ] BotSystem with Gemini integration
- [ ] Bot spawning and management
- [ ] Chat interaction system
- [ ] Bot state management (normal/corrupted)

### Phase 3: Player Features
- [ ] PlayerCheckSystem
- [ ] AbilitySystem with cooldowns
- [ ] Vanish/reveal mechanics
- [ ] Enderman transform

### Phase 4: UI & Polish
- [ ] ModMenu integration
- [ ] DirectorWand GUI
- [ ] Keybindings
- [ ] Localization

### Phase 5: Events & Story
- [ ] All story chapters from commands
- [ ] Sound effects integration
- [ ] Title/subtitle system
- [ ] End game sequence

## Technical Details

### Dependencies
- Fabric API 0.92.2+1.20.1
- Fabric Loader 0.15.11
- Cloth Config (for mod menu)
- Gson (for JSON parsing)
- Java HTTP Client (for API calls)

### API Keys
- Gemini API key stored in config file
- Should be user-configurable

### Commands
- `/role set <player> <role>` - Assign role
- `/skinwalker summon <name>` - Spawn bot
- `/skinwalker check <player>` - Check player
- `/story <chapter>` - Play story chapter
- `/skinwalker convert <player>` - Convert to skinwalker
- `/vanish` - Toggle invisibility
- `/reveal` - Remove invisibility

## Skinwalker Skin URLs (from commands)
- Normal: https://files.catbox.moe/cb8c8a.png
- Waya: https://files.catbox.moe/qxdjf2.png
- Lucas: https://files.catbox.moe/7fs0c4.png
- Babi: https://files.catbox.moe/5mbyi0.png
- Dream: https://files.catbox.moe/9gf4xa.png
- TechnoBlade: https://files.catbox.moe/6v0y8i.png
- Craftee: https://files.catbox.moe/v2ibhg.png
- MrBeast: https://files.catbox.moe/o456fl.png
- PrestonPlays: https://files.catbox.moe/fbjewy.png
- Steve: https://files.catbox.moe/zq4kyt.png

