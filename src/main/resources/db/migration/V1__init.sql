-- ==========================================================
-- Flyway Migration Script
-- Version: V1
-- File: V1__init.sql
-- Purpose: Create base schema (companies, users, categories, accounts)
-- Author: System
-- Date: 2025-01-15
-- ==========================================================

-- ==========================================================
-- Create schema if it doesn't exist
-- ==========================================================
CREATE SCHEMA IF NOT EXISTS exp_finlow;

-- Set search path to use exp_finlow schema
SET search_path TO exp_finlow;

-- ==========================================================
-- Drop existing tables (for development resets)
-- ==========================================================
DROP TABLE IF EXISTS exp_finlow.accounts CASCADE;
DROP TABLE IF EXISTS exp_finlow.categories CASCADE;
DROP TABLE IF EXISTS exp_finlow.users CASCADE;
DROP TABLE IF EXISTS exp_finlow.companies CASCADE;

-- ==========================================================
-- Table: companies
-- Purpose: Store company/organization master records
-- ==========================================================
CREATE TABLE exp_finlow.companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    plan VARCHAR(50) NOT NULL DEFAULT 'free',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ==========================================================
-- Table: users
-- Purpose: Store user accounts and authentication
-- ==========================================================
CREATE TABLE exp_finlow.users (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES exp_finlow.companies(id),
    email VARCHAR(250) NOT NULL UNIQUE,
    password_hash VARCHAR(500) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('USER', 'MANAGER', 'ADMIN')),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ==========================================================
-- Table: categories
-- Purpose: Store expense/transaction categories
-- ==========================================================
CREATE TABLE exp_finlow.categories (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES exp_finlow.companies(id),
    name VARCHAR(250) NOT NULL,
    parent_id BIGINT REFERENCES exp_finlow.categories(id)
);

-- ==========================================================
-- Table: accounts
-- Purpose: Store financial accounts for each company
-- ==========================================================
CREATE TABLE exp_finlow.accounts (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES exp_finlow.companies(id),
    name VARCHAR(250) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    balance NUMERIC(14, 2) NOT NULL DEFAULT 0,
    type VARCHAR(50) NOT NULL
);

-- ==========================================================
-- Indexes
-- ==========================================================
CREATE INDEX IF NOT EXISTS idx_users_company_id ON exp_finlow.users(company_id);
CREATE INDEX IF NOT EXISTS idx_users_email ON exp_finlow.users(email);
CREATE INDEX IF NOT EXISTS idx_categories_company_id ON exp_finlow.categories(company_id);
CREATE INDEX IF NOT EXISTS idx_categories_parent_id ON exp_finlow.categories(parent_id);
CREATE INDEX IF NOT EXISTS idx_accounts_company_id ON exp_finlow.accounts(company_id);
