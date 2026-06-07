# 🔐 Quick Reference - Environment Setup

## For New Team Members

### 1. Copy the template
```bash
cp .env.example .env
```

### 2. Edit .env with your Neon DB credentials
```bash
nano .env
```
- Get your credentials from: https://console.neon.tech
- Format: `DB_URL=jdbc:postgresql://your-endpoint.neon.tech/neondb?sslmode=require&channelBinding=require`

### 3. Load variables
```bash
source .env
```

### 4. Build & Run
```bash
mvn clean package -DskipTests
java -jar target/mobileapp-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## For IntelliJ IDE Users

1. Run → Edit Configurations
2. Select "MobileappApplication"
3. Environment variables → Add:
   - `DB_URL=jdbc:postgresql://...`
   - `DB_USERNAME=xxx`
   - `DB_PASSWORD=xxx`
   - `JWT_SECRET=xxx`
   - `SPRING_PROFILES_ACTIVE=prod`
4. Apply → Click Run

---

## Important Files

| File | Purpose | Commit? |
|------|---------|---------|
| `.env` | Your local credentials | ❌ NO - In .gitignore |
| `.env.example` | Template for new devs | ✅ YES |
| `application-prod.properties` | Production config (uses env vars) | ✅ YES |
| `SECURITY-SETUP.md` | Full security guide | ✅ YES |

---

## Troubleshooting

**Variables not loading?**
```bash
# Check if loaded
echo $DB_URL

# Reload
source .env
```

**Can't connect to DB?**
- Verify `.env` has correct values from Neon console
- Check Neon DB is not sleeping
- Try connection string directly in psql

---

See `SECURITY-SETUP.md` for detailed guide

