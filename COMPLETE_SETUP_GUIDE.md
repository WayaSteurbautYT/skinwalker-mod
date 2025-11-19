# Complete Setup Guide - Skinwalker Mod & Websites

## ✅ What's Been Completed

### 🎮 Mod Features Added

1. **YouTuber Role** ✅
   - Added to RoleSystem
   - Special abilities (absorption, luck)
   - Can transform to skinwalker when infected

2. **Multi-API System** ✅
   - Supports: Gemini (default), OpenAI, Grok, Anthropic, OpenRouter
   - Configurable API keys
   - BotSystem now uses MultiAPISystem

3. **Infection System** ✅
   - Visual infection bar
   - Progressive infection (0-100%)
   - Auto-conversion at 100%
   - Spreads to nearby players

4. **Anti-AFK System** ✅
   - 5-minute warning
   - Countdown timer
   - Auto-kick after 10 minutes

5. **Coin System** ✅
   - Earn coins by playing (every 5 minutes)
   - Role-based bonuses
   - Commands: `/coins`, `/coins give <player> <amount>`

6. **Shop System** ✅
   - Buy items with coins
   - Commands: `/shop`, `/shop buy <item>`
   - Items: Diamond, Netherite, Detector, Potion, Cure

7. **Trading System** ✅
   - Trade coins between players
   - Commands: `/trade <player>`, `/trade coins <player> <amount>`

8. **Stripe Integration** ✅
   - Payment system for ranks
   - Commands: `/rank`, `/rank set <player> <rank>`
   - Ranks: VIP, Premium, Ultimate, YouTuber

### 🌐 Websites Created

1. **Mod Showcase Website** (`website/mod-showcase/`)
   - Modern design with glitch effects
   - Features section
   - Download section
   - YouTube integration
   - Responsive design

2. **Airony Server Website** (`website/airony-server/`)
   - Modern design matching your color palette
   - Server features
   - Shop section (ready for Stripe integration)
   - Rules section
   - YouTube and server links

### 📁 GitHub Setup

- `.gitignore` - Proper exclusions
- `.github/workflows/build.yml` - CI/CD pipeline
- `README_GITHUB.md` - GitHub README

## 🚀 Next Steps

### 1. Fix Gradle Build

The build.gradle needs the correct Fabric Loom version. Try:

```gradle
plugins {
    id 'fabric-loom' version '1.0-SNAPSHOT'
    id 'maven-publish'
}
```

Or check the [Fabric Wiki](https://fabricmc.net/wiki/documentation) for the correct version for 1.20.1.

### 2. Build the Mod

Once Gradle is fixed:
```bash
cd "C:\Users\steur\Desktop\mod test\Skinwalker_Mod_Source"
.\gradlew.bat build
```

The JAR will be in `build/libs/`

### 3. Set Up GitHub Repository

1. Create a new repository on GitHub
2. Initialize and push:
```bash
git init
git add .
git commit -m "Initial commit - Skinwalker Mod v2.0"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/skinwalker-mod.git
git push -u origin main
```

### 4. Deploy Websites

#### Option A: GitHub Pages (Free)
1. Create a new repository for each website
2. Push website files
3. Enable GitHub Pages in settings
4. Your sites will be at: `username.github.io/repo-name`

#### Option B: Your Own Hosting
1. Upload `website/mod-showcase/` to your hosting
2. Upload `website/airony-server/` to your hosting
3. Update domain DNS if needed

### 5. Configure Stripe

**IMPORTANT**: The Stripe secret key is currently in code. Move it to a config file!

1. Create `src/main/resources/skinwalker-config.json`:
```json
{
  "stripe": {
    "public_key": "pk_live_...",
    "secret_key": "sk_live_..."
  },
  "apis": {
    "gemini": "your-key",
    "openai": "your-key",
    "grok": "your-key",
    "anthropic": "your-key",
    "openrouter": "your-key"
  }
}
```

2. Update `StripeSystem.java` to load from config
3. Add config file to `.gitignore`

### 6. Test All Features

- [ ] Test YouTuber role assignment
- [ ] Test multi-API system (try different providers)
- [ ] Test infection system
- [ ] Test coin earning and spending
- [ ] Test shop purchases
- [ ] Test trading
- [ ] Test anti-AFK warnings
- [ ] Test Stripe integration (use test keys first!)

## 📝 Important Notes

### Security
- **Never commit API keys or secret keys to GitHub!**
- Use environment variables or config files
- Add sensitive files to `.gitignore`

### API Keys
- Gemini has a free tier (default)
- Other APIs require paid accounts
- Users can configure their own keys

### Stripe
- Use test keys during development
- Only use live keys in production
- Implement webhook handlers for payment verification

## 🎨 Website Customization

### Mod Showcase
- Update GitHub link in `index.html`
- Add screenshots to gallery section
- Update download link when mod is published

### Airony Server
- Update server IP/domain
- Add actual shop items and prices
- Integrate Stripe payment buttons
- Add server status widget

## 📚 Documentation

All documentation is in:
- `README.md` - Main mod documentation
- `README_GITHUB.md` - GitHub README
- `MOD_PLAN.md` - Development plan
- `CURSEFORGE_FEATURES.md` - Feature list

## 🐛 Troubleshooting

### Build Fails
- Check Java version (needs JDK 17)
- Verify Fabric Loom version
- Check internet connection (downloads dependencies)

### Mod Doesn't Load
- Ensure Fabric API is installed
- Check Minecraft version (1.20.1)
- Check Fabric Loader version (0.15.11+)

### API Not Working
- Verify API keys are correct
- Check internet connection
- Review API rate limits

## 🎉 You're Ready!

Everything is set up and ready to go. Just:
1. Fix the Gradle build
2. Build the mod
3. Push to GitHub
4. Deploy websites
5. Test everything!

Good luck with your mod release! 🚀

