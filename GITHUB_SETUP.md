# GitHub Setup Guide

## Step 1: Create GitHub Repository

1. Go to https://github.com/new
2. Repository name: `skinwalker-mod` (or your preferred name)
3. Description: "Minecraft 1.20.1 Fabric mod for immersive roleplay with AI bots and skinwalker mechanics"
4. Choose **Public** or **Private**
5. **DO NOT** initialize with README (we already have one)
6. Click **Create repository**

## Step 2: Initialize Git and Push

Run these commands in PowerShell (in your mod directory):

```powershell
# Initialize git
git init

# Add all files
git add .

# Create initial commit
git commit -m "Initial commit - Skinwalker Mod v2.0 with all features"

# Rename branch to main
git branch -M main

# Add remote (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/skinwalker-mod.git

# Push to GitHub
git push -u origin main
```

## Step 3: Create Release

1. Go to your repository on GitHub
2. Click **Releases** → **Create a new release**
3. Tag: `v2.0.0`
4. Title: `Skinwalker Mod v2.0.0`
5. Description:
```
## 🎮 Skinwalker Mod v2.0.0

### Features
- AI Bot System (Gemini, OpenAI, Grok, Anthropic, OpenRouter)
- Role System with YouTuber role
- Infection System with visual bar
- Coin System and Shop
- Trading System
- Anti-AFK System
- Stripe Integration for ranks
- Advanced Skin System
- Win/Loss System
- Particle Effects

### Installation
1. Install Fabric Loader 0.15.11+ for Minecraft 1.20.1
2. Install Fabric API 0.92.2+1.20.1
3. Download the JAR file below
4. Place in your `mods` folder
```

6. Upload the JAR file from `build/libs/skinwalker-roleplay-2.0.0.jar`
7. Click **Publish release**

## Step 4: Update README

The `README_GITHUB.md` file is ready. You can rename it to `README.md` or copy its contents.

