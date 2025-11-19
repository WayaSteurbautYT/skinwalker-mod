# Git Installation Required

Git is not currently installed or not in your system PATH. Here's how to fix it:

## Option 1: Install Git for Windows (Recommended)

1. **Download Git**: Go to https://git-scm.com/download/win
2. **Install**: Run the installer with default settings
3. **Restart**: Close and reopen PowerShell/Command Prompt
4. **Verify**: Run `git --version` to confirm installation

## Option 2: Use GitHub Desktop (Easier GUI)

1. **Download**: Go to https://desktop.github.com/
2. **Install**: Run the installer
3. **Sign in**: Use your GitHub account
4. **Clone/Add Repository**: Use the GUI to add your repository

## Option 3: Use GitHub Web Interface

1. Go to https://github.com/WayaSteurbautYT/skinwalker-mod
2. Click **"uploading an existing file"** or use the web editor
3. Drag and drop your files

## After Installing Git

Once Git is installed, run these commands in PowerShell:

```powershell
cd "C:\Users\steur\Desktop\mod test\Skinwalker_Mod_Source"

git add .

git commit -m "Initial commit - Skinwalker Mod v2.0.0"

git branch -M main

git remote add origin https://github.com/WayaSteurbautYT/skinwalker-mod.git

git push -u origin main
```

## Quick Install Command

If you have Chocolatey installed:
```powershell
choco install git -y
```

Or using winget:
```powershell
winget install --id Git.Git -e --source winget
```

