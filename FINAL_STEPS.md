# Final Steps - Complete Your Mod Release

## ✅ Build Complete!

Your mod JAR file is ready: **`build/libs/skinwalker-roleplay-2.0.0.jar`** (2.4 MB)

---

## 🔐 Step 1: Secure Your API Keys (IMPORTANT!)

**Before pushing to GitHub**, move the Stripe secret key to a config file:

1. Create `src/main/resources/skinwalker-config.json`:
```json
{
  "stripe": {
    "public_key": "pk_live_51QrfklLAtCHhSH6tbCq3DCB2uRaL2MeInHfbmnsUFgF9W3MnzcrNEcKPCxC9xhhP2XMrdxe8aOyI5vbbhNfaHXC300bUnvFvtS",
    "secret_key": "YOUR_SECRET_KEY_HERE"
  }
}
```

2. Update `StripeSystem.java` to load from config (or use environment variables)
3. Add to `.gitignore`: `src/main/resources/skinwalker-config.json`

---

## 📤 Step 2: Push to GitHub

### A. Create Repository on GitHub
1. Go to: https://github.com/new
2. Repository name: `skinwalker-mod`
3. Description: "Minecraft 1.20.1 Fabric mod for immersive roleplay"
4. Choose **Public**
5. **Don't** initialize with README
6. Click **Create repository**

### B. Push Your Code
Open PowerShell in your mod directory and run:

```powershell
# Initialize git
git init

# Add all files
git add .

# Create commit
git commit -m "Initial commit - Skinwalker Mod v2.0.0

Features:
- AI Bot System (Multi-API support)
- Role System with YouTuber role
- Infection System
- Coin System & Shop
- Trading System
- Anti-AFK System
- Stripe Integration
- Advanced Skin System
- Win/Loss System
- Particle Effects"

# Set branch to main
git branch -M main

# Add remote (REPLACE YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/skinwalker-mod.git

# Push to GitHub
git push -u origin main
```

### C. Create Release
1. Go to your repository → **Releases** → **Create a new release**
2. Tag: `v2.0.0`
3. Title: `Skinwalker Mod v2.0.0`
4. Upload: `build/libs/skinwalker-roleplay-2.0.0.jar`
5. Description: (Use the one from CURSEFORGE_SETUP.md)
6. Click **Publish release**

---

## 🌐 Step 3: Upload Website to Combell

### A. Access Combell
1. Log in to your Combell account
2. Go to your domain: `smp.airony.org`
3. Open **File Manager** or access **FTP**

### B. Upload Files
Upload these files from `website/airony-server/`:
- `index.html`
- `styles.css`
- `script.js`

**Upload location**: Usually `public_html/` or `www/` folder

### C. Test Website
1. Visit: `https://smp.airony.org`
2. Check all pages load
3. Test buttons and links

### D. Enable SSL (HTTPS)
1. In Combell control panel → **SSL/TLS**
2. Enable **Let's Encrypt** (free)
3. Wait for activation

---

## 📦 Step 4: Publish to CurseForge

### A. Create Account/Log In
1. Go to: https://authors.curseforge.com/
2. Sign up or log in
3. Complete verification if needed

### B. Create Project
1. Click **Create Project**
2. Select **Minecraft Mods**
3. Fill in:
   - **Name**: `Skinwalker Mod`
   - **Summary**: "Immersive roleplay mod with AI bots and skinwalker mechanics"
   - **Category**: Roleplay / Adventure
   - **Game Version**: Minecraft 1.20.1

### C. Add Description
Copy the description from `CURSEFORGE_SETUP.md` or use:

```markdown
# Skinwalker Mod

Transform your Minecraft server into an immersive roleplay experience!

## Features
- 🤖 AI Bot System (Gemini, OpenAI, Grok, Anthropic, OpenRouter)
- 🎭 Role System (Dream, Technoblade, MrBeast, Craftee, YouTuber, Skinwalker, Survivor)
- 📖 Complete Story System
- 🎨 Advanced Skin System
- 💰 Coin System & Shop
- 🛒 Trading System
- 🛡️ Anti-AFK System
- ✨ Particle Effects

## Installation
1. Install Fabric Loader 0.15.11+ for Minecraft 1.20.1
2. Install Fabric API 0.92.2+1.20.1
3. Download and place in `mods` folder
```

### D. Upload Release
1. Click **Create New File** or **Upload File**
2. Upload: `build/libs/skinwalker-roleplay-2.0.0.jar`
3. Version: `2.0.0`
4. Release Type: **Beta** (or **Release** if fully tested)
5. Game Versions: `1.20.1`
6. Loaders: `Fabric`
7. Changelog: (See CURSEFORGE_SETUP.md)
8. Click **Submit for Review**

### E. Add Links
- **Source Code**: Your GitHub repository URL
- **Website**: `https://smp.airony.org`
- **Issue Tracker**: Your GitHub issues URL

### F. Add Media
- Upload at least 1 screenshot
- Upload icon/logo (256x256 or 512x512 PNG)
- Optional: Video showcase

---

## ✅ Final Checklist

- [ ] Moved Stripe keys to config file
- [ ] Added config to .gitignore
- [ ] Created GitHub repository
- [ ] Pushed code to GitHub
- [ ] Created GitHub release
- [ ] Uploaded website to Combell
- [ ] Tested website
- [ ] Created CurseForge account
- [ ] Created CurseForge project
- [ ] Uploaded mod to CurseForge
- [ ] Added screenshots to CurseForge
- [ ] Submitted for review

---

## 🎉 You're Done!

Once CurseForge approves (usually 1-3 days), your mod will be live!

**Share your mod:**
- GitHub: `https://github.com/YOUR_USERNAME/skinwalker-mod`
- CurseForge: `https://www.curseforge.com/minecraft/mc-mods/skinwalker-mod`
- Website: `https://smp.airony.org`

Good luck! 🚀

