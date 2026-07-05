-- V1: Baseline migration - create all existing tables and the new reviews table

BEGIN;

-- Users table
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    CONSTRAINT users_role_check CHECK (role IN ('USER','ADMIN','VENDOR','CUSTOMER'))
    );

-- Vendors table
CREATE TABLE IF NOT EXISTS vendors (
                                       id BIGSERIAL PRIMARY KEY,
                                       business_name VARCHAR(255),
    description TEXT,
    category VARCHAR(100),
    location VARCHAR(255),
    gst_number VARCHAR(50) UNIQUE,
    verified BOOLEAN DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vendor_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Verification table
CREATE TABLE IF NOT EXISTS verification (
                                            id BIGSERIAL PRIMARY KEY,
                                            document_type VARCHAR(100),
    document_url VARCHAR(500),
    status VARCHAR(50) DEFAULT 'PENDING',
    vendor_id BIGINT NOT NULL,
    reviewed_by VARCHAR(255),
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_verification_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
    );

-- Refresh tokens table
CREATE TABLE IF NOT EXISTS refresh_token (
                                             id BIGSERIAL PRIMARY KEY,
                                             token VARCHAR(500) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    is_revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Posts table
CREATE TABLE IF NOT EXISTS posts (
                                     id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(255),
    content TEXT,
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Reviews table (NEW)
CREATE TABLE IF NOT EXISTS reviews (
                                       id BIGSERIAL PRIMARY KEY,
                                       vendor_id BIGINT NOT NULL,
                                       customer_id BIGINT NOT NULL,
                                       rating INT NOT NULL,
                                       title TEXT NOT NULL,
                                       comment TEXT,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       CONSTRAINT fk_review_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_vendor_customer UNIQUE (vendor_id, customer_id),
    CONSTRAINT chk_rating CHECK (rating >= 1 AND rating <= 5)
    );

COMMIT;