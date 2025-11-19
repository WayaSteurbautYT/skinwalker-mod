# Combell Website Setup Guide

## Step 1: Access Combell Control Panel

1. Log in to your Combell account
2. Go to your domain management (smp.airony.org)
3. Access the **File Manager** or **FTP** section

## Step 2: Upload Website Files

### Option A: Using File Manager (Web Interface)

1. Navigate to your domain's `public_html` or `www` folder
2. Upload the files from `website/airony-server/`:
   - `index.html`
   - `styles.css`
   - `script.js`

### Option B: Using FTP

1. Get your FTP credentials from Combell:
   - FTP Host: `ftp.airony.org` (or provided by Combell)
   - Username: (your Combell username)
   - Password: (your FTP password)
   - Port: 21

2. Use an FTP client (FileZilla, WinSCP) or PowerShell:
```powershell
# Using PowerShell FTP (if available)
# Or use FileZilla/WinSCP GUI
```

3. Upload files to the root directory (usually `public_html` or `www`)

## Step 3: Configure Domain

1. In Combell control panel, ensure your domain points to the correct directory
2. If using a subdomain (like `www.airony.org`), configure it in DNS settings
3. Wait for DNS propagation (can take up to 24 hours, usually faster)

## Step 4: Test Website

1. Visit `https://smp.airony.org` (or your domain)
2. Check that all pages load correctly
3. Test all links and buttons

## Step 5: SSL Certificate (HTTPS)

1. In Combell control panel, enable **Let's Encrypt SSL**
2. This provides free HTTPS certificate
3. Wait for activation (usually instant)

## Step 6: Update Website Content

Edit the HTML files to:
- Update server IP if needed
- Add actual shop items/prices
- Integrate Stripe payment buttons (when ready)
- Add server status widget (optional)

## Troubleshooting

- **404 Error**: Check file paths and directory structure
- **CSS not loading**: Verify file paths in HTML are correct
- **JavaScript not working**: Check browser console for errors
- **Domain not resolving**: Wait for DNS propagation or check DNS settings

