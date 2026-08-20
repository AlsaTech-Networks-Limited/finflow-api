-- ==========================================================
-- Flyway Migration Script
-- Version: V2
-- File: V2__create_expense_tables_2025_01_15.sql
-- Purpose: Create expense and approval tables
-- Author: System
-- Date: 2025-01-15
-- ==========================================================

-- Set search path to use exp_finlow schema
SET search_path TO exp_finlow;

-- ==========================================================
-- Drop existing tables (for development resets)
-- ==========================================================
DROP TABLE IF EXISTS exp_finlow.approvals CASCADE;
DROP TABLE IF EXISTS exp_finlow.expenses CASCADE;

-- ==========================================================
-- Table: expenses
-- Purpose: Store employee expense submissions
-- Note: Uses application-generated IDs (Snowflake algorithm)
-- ==========================================================
CREATE TABLE exp_finlow.expenses (
    record_id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES exp_finlow.users(id),
    category_id BIGINT NOT NULL REFERENCES exp_finlow.categories(id),
    amount NUMERIC(14, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    receipt_url TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ==========================================================
-- Table: approvals
-- Purpose: Store expense approval/rejection records
-- Note: Uses application-generated IDs (Snowflake algorithm)
-- ==========================================================
CREATE TABLE exp_finlow.approvals (
    record_id BIGINT PRIMARY KEY,
    expense_id BIGINT NOT NULL REFERENCES exp_finlow.expenses(record_id),
    approver_id BIGINT NOT NULL REFERENCES exp_finlow.users(id),
    decision VARCHAR(50) NOT NULL CHECK (decision IN ('APPROVED', 'REJECTED')),
    comment TEXT,
    decided_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ==========================================================
-- Indexes
-- ==========================================================
CREATE INDEX IF NOT EXISTS idx_expenses_user_id ON exp_finlow.expenses(user_id);
CREATE INDEX IF NOT EXISTS idx_expenses_category_id ON exp_finlow.expenses(category_id);
CREATE INDEX IF NOT EXISTS idx_expenses_status ON exp_finlow.expenses(status);
CREATE INDEX IF NOT EXISTS idx_expenses_created_at ON exp_finlow.expenses(created_at);
CREATE INDEX IF NOT EXISTS idx_approvals_expense_id ON exp_finlow.approvals(expense_id);
CREATE INDEX IF NOT EXISTS idx_approvals_approver_id ON exp_finlow.approvals(approver_id);
CREATE INDEX IF NOT EXISTS idx_approvals_decided_at ON exp_finlow.approvals(decided_at);
