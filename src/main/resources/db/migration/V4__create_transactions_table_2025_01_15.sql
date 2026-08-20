-- ==========================================================
-- Flyway Migration Script
-- Version: V4
-- File: V4__create_transactions_table_2025_01_15.sql
-- Purpose: Create transactions table for account ledger entries
-- Author: System
-- Date: 2025-01-15
-- ==========================================================

-- Set search path to use exp_finlow schema
SET search_path TO exp_finlow;

-- ==========================================================
-- Drop existing table (for development resets)
-- ==========================================================
DROP TABLE IF EXISTS exp_finlow.transactions CASCADE;

-- ==========================================================
-- Table: transactions
-- Purpose: Store raw ledger entries (imported or manual)
-- Note: Tracks all money movements in/out of accounts
-- ==========================================================
CREATE TABLE exp_finlow.transactions (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES exp_finlow.accounts(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL CHECK (type IN ('DEBIT', 'CREDIT')),
    amount NUMERIC(14, 2) NOT NULL,
    description TEXT,
    reference VARCHAR(250),
    occurred_at TIMESTAMP NOT NULL,
    reconciled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ==========================================================
-- Indexes
-- ==========================================================
CREATE INDEX IF NOT EXISTS idx_transactions_account_id ON exp_finlow.transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_occurred_at ON exp_finlow.transactions(occurred_at);
CREATE INDEX IF NOT EXISTS idx_transactions_reconciled ON exp_finlow.transactions(reconciled);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON exp_finlow.transactions(type);
