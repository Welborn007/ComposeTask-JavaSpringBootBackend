#!/bin/bash

# Setup script for local development environment
# This script helps you prepare your local .env file with credentials

echo "🔒 ComposeTask Mobile App - Secure Configuration Setup"
echo "=================================================="
echo ""
echo "This script will help you create your local .env file"
echo "Your credentials will NOT be committed to GitHub"
echo ""

# Check if .env already exists
if [ -f ".env" ]; then
    echo "⚠️  .env file already exists. Skipping creation..."
    echo "   To reconfigure, delete .env and run this script again"
    exit 0
fi

# Create .env from template
cp .env.example .env

echo "✅ .env file created from .env.example"
echo ""
echo "📝 Next steps:"
echo "1. Open .env in your editor"
echo "2. Replace the placeholder values with your actual Neon DB credentials:"
echo "   - DB_URL: Your Neon database connection string"
echo "   - DB_USERNAME: Your Neon username"
echo "   - DB_PASSWORD: Your Neon password"
echo "   - JWT_SECRET: A strong random secret (min 32 characters)"
echo ""
echo "3. Save the file (do NOT commit it to git)"
echo ""
echo "4. Load variables before running:"
echo "   source .env"
echo "   mvn spring-boot:run"
echo ""
echo "4. Or in IDE: Add variables to Run Configuration → Environment variables"
echo ""

