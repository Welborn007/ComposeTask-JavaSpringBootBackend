# 🔐 JWT_SECRET Explained

## What is JWT_SECRET?

`JWT_SECRET` is a **secret key used to sign and verify JSON Web Tokens (JWTs)** for user authentication.

---

## Where is it Used?

### **Location in Code: `src/main/java/com/composetask/mobileapp/config/JwtUtil.java`**

```java
@Component
public class JwtUtil {
    @Value("${jwt.secret:default-value}")
    private String SECRET_KEY;  // ← This is where JWT_SECRET is injected
    
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // ← Signs token with SECRET_KEY
                .compact();
    }
    
    public boolean isTokenValid(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // ← Verifies token with SECRET_KEY
                .build()
                .parseClaimsJws(token)
                .true;
    }
}
```

---

## How the Flow Works

### **1. User Login**
```
User → Send credentials → Backend validates → generateToken(email) 
→ Uses JWT_SECRET to sign → Returns JWT token to client
```

### **2. User Makes Authenticated Request**
```
Client → Send JWT in Authorization header → Backend receives token
→ isTokenValid(token) → Uses JWT_SECRET to verify signature
→ If valid, process request
```

---

## Configuration

### **Development** (`application.properties`)
```properties
jwt.secret=dev-secret-key-change-in-production-minimum-32-characters-recommended
```

### **Production** (`application-prod.properties`)
```properties
jwt.secret=${JWT_SECRET}  # Reads from environment variable
```

### **Your `.env` File**
```bash
JWT_SECRET=your-super-secret-jwt-key-here-min-32-characters
```

---

## Why Different Values?

| Environment | Value | Source | Safe? |
|-------------|-------|--------|-------|
| **Development** | `dev-secret-key-...` | `application.properties` | ✅ (Demo only) |
| **Production** | `npg_xxxxx...` (from `.env`) | Environment variable | ✅ (Kept secret) |

**Important:** If the secret changes, all previously issued tokens become invalid.

---

## Generating a Secure JWT_SECRET

Use this command to generate a strong random secret:

```bash
# macOS/Linux
openssl rand -base64 32

# Example output:
# xK9mLpQwRtVnZaFhBgJkY2NvDsEjXwM4=
```

---

## Best Practices

✅ **DO:**
- Keep JWT_SECRET at least 32 characters long
- Store production secret in `.env` (NOT in git)
- Use different secrets for dev/staging/prod
- Rotate JWT_SECRET regularly (every 3 months)
- Use HTTPS to prevent token interception

❌ **DON'T:**
- Hardcode JWT_SECRET in source code
- Commit `.env` to Git
- Use simple/weak secrets
- Share JWT_SECRET via chat/email
- Use same secret across environments

---

## Testing

To test JWT functionality:

```bash
# 1. Start the app
source .env
java -jar target/mobileapp-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# 2. Login (get token)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'

# Response:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
# }

# 3. Use token in request
curl -X GET http://localhost:8080/api/protected \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| "Invalid token signature" | JWT_SECRET mismatch | Ensure `.env` JWT_SECRET matches what's being used |
| "Token expired" | Token generation time | Check server time, increase EXPIRATION_TIME |
| "Null pointer on SECRET_KEY" | Environment variable not loaded | Must run: `source .env` before starting app |
| "Can't decode token" | Token was signed with different secret | Use same JWT_SECRET that signed the token |

---

## Summary

- **JWT_SECRET** = Secret key for signing/verifying authentication tokens
- **Used in** = `JwtUtil.java` class for token generation and validation
- **Configured in** = `application.properties` (dev) or `application-prod.properties` (prod)
- **Value source** = `.env` file (production) or hardcoded (development only)
- **Must keep secure** = Never commit `.env` to Git

