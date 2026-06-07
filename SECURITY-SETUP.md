# 🔒 Secure Configuration Guide

This guide explains how to safely manage production credentials for the ComposeTask Mobile App without exposing them on GitHub.

## ⚠️ Important Security Notice

**NEVER commit the following to Git:**
- `.env` (contains real credentials)
- Hardcoded database passwords
- API keys or secrets

---

## 🚀 Quick Start for Development

### Step 1: Copy Environment Template

```bash
cp .env.example .env
```

### Step 2: Add Your Credentials

Edit `.env` and replace placeholders:

```bash
# Database Configuration (Neon DB)
DB_URL=jdbc:postgresql://ep-xxxxx.neon.tech/neondb?sslmode=require&channelBinding=require
DB_USERNAME=neondb_owner
DB_PASSWORD=npg_xxxxxxxxxxxxx

# JWT Configuration
JWT_SECRET=your-secret-key-here-minimum-32-characters-recommended
```

### Step 3: Load in Terminal

```bash
# Option A: Load for current session
source .env

# Then run the app
java -jar target/mobileapp-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Option B: Run with env file
java -a target/mobileapp-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Step 4: Verify

```bash
# Check variables are loaded
echo $DB_URL
echo $DB_USERNAME
```

---

## 🛠️ IDE Setup (JetBrains IntelliJ IDEA)

1. **Open Run Configurations**
   - Click "Run" → "Edit Configurations"
   
2. **Select your Spring Boot configuration** (or create new)

3. **Go to "Environment variables" section**

4. **Add these variables:**
   ```
   DB_URL=jdbc:postgresql://...
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   JWT_SECRET=your_secret
   SPRING_PROFILES_ACTIVE=prod
   ```

5. **Apply and save**

Now when you run from IDE, your credentials are automatically loaded.

---

## 🔄 Production Deployment

### AWS EC2 / Linux Server

```bash
# Set as system environment variables
export DB_URL="jdbc:postgresql://prod-db-host/neondb?sslmode=require"
export DB_USERNAME="prod_user"
export DB_PASSWORD="secure_password"
export JWT_SECRET="production_secret_key"

# Or add to .bashrc / .zshrc for persistence
echo 'export DB_URL="..."' >> ~/.zshrc
source ~/.zshrc
```

### Docker

Create `docker-compose.yml`:

```yaml
version: '3.8'
services:
  app:
    image: mobileapp:latest
    environment:
      DB_URL: ${DB_URL}
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      SPRING_PROFILES_ACTIVE: prod
    ports:
      - "8080:8080"
```

Run with:
```bash
DB_URL=... DB_USERNAME=... DB_PASSWORD=... DB_SECRET=... docker-compose up
```

### AWS Secrets Manager / Parameter Store

Store secrets securely and retrieve them at runtime:

```bash
# Using AWS Systems Manager Parameter Store
aws ssm get-parameter --name /mobileapp/db-url --query 'Parameter.Value'
```

---

## 📋 Git Protection Checklist

✅ `.env` added to `.gitignore` (already done)
✅ `.env.example` committed (safe - no real values)
✅ Never commit `application.properties` with credentials
✅ Use environment variables in `application-prod.properties`
✅ Never hardcode passwords in source code

### Verify Git Setup

```bash
# This should NOT show .env file
git status

# This should show no credentials
git log --all -p | grep -i "password\|secret" | head

# Double-check
ls -la | grep .env  # Should show .env (not committed) and .env.example (committed)
```

---

## 📝 Variable Reference

| Variable | Purpose | Example |
|----------|---------|---------|
| `DB_URL` | PostgreSQL connection string | `jdbc:postgresql://host/db?sslmode=require` |
| `DB_USERNAME` | Database username | `neondb_owner` |
| `DB_PASSWORD` | Database password | `npg_xxxxx` |
| `JWT_SECRET` | JWT token signing key | Min 32 chars, use `openssl rand -base64 32` |
| `SPRING_PROFILES_ACTIVE` | Active profile | `prod` or `dev` |

Generate a strong JWT secret:
```bash
openssl rand -base64 32
```

---

## 🚨 If Credentials Were Accidentally Committed

1. **Immediately rotate credentials** (change passwords in Neon DB)
2. **Remove from history**:
   ```bash
   git filter-branch --tree-filter 'rm -f .env' -- --all
   git push origin --force
   ```
3. **Force-update all team members** (they need to rebased)
4. **Use a tool** like `git-secrets` or GitHub secret scanning to prevent future leaks

---

## ✨ Best Practices

- ✅ Use strong, unique passwords
- ✅ Rotate secrets quarterly
- ✅ Use environment-specific configs (dev, staging, prod)
- ✅ Monitor access logs for suspicious activity
- ✅ Use HTTPS/SSL for all connections
- ✅ Keep JWT_SECRET secure (never share in chat/email)
- ✅ Limit database user permissions (read-only for some services)

---

## 🆘 Troubleshooting

**"Spring can't find environment variables"**
```bash
# Check if variables are loaded
printenv | grep DB_

# Reload if using shell
source .env

# For IDE, restart the IDE after adding variables
```

**"Connection refused to database"**
- Verify `DB_URL` is correct
- Check database is running
- Verify username/password are correct
- Check firewall rules allow connection

**"JWT token validation fails"**
- Ensure `JWT_SECRET` is the same in all environments
- Check JWT library is correctly configured
- Verify token hasn't been modified

---

For more info, see `application-prod.properties` for all Spring Boot configuration options.

