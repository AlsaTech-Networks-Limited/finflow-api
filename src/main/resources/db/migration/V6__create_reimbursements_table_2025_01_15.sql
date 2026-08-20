-- ==========================================================
-- Flyway Migration Script
-- Version: V6
-- File: V6__create_reimbursements_table_2025_01_15.sql
-- Purpose: Create reimbursements table
-- Author: System
-- Date: 2025-01-15
-- ==========================================================

-- Set search path to use exp_finlow schema
SET search_path TO exp_finlow;

-- ==========================================================
-- Drop existing table (for development resets)
-- ==========================================================
DROP TABLE IF EXISTS exp_finlow.reimbursements CASCADE;

-- ==========================================================
-- Table: reimbursements
-- Purpose: Track payout obligations for PERSONAL_FUNDS expenses
-- ==========================================================
CREATE TABLE exp_finlow.reimbursements (
    id BIGSERIAL PRIMARY KEY,
    expense_id VARCHAR(250) NOT NULL UNIQUE,
    amount NUMERIC(14, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PAID')),
    method VARCHAR(50) NOT NULL DEFAULT 'BANK_TRANSFER' CHECK (method IN ('BANK_TRANSFER', 'PAYROLL')),
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ==========================================================
-- Indexes
-- ==========================================================
CREATE INDEX IF NOT EXISTS idx_reimbursements_expense_id ON exp_finlow.reimbursements(expense_id);
CREATE INDEX IF NOT EXISTS idx_reimbursements_status ON exp_finlow.reimbursements(status);
CREATE INDEX IF NOT EXISTS idx_reimbursements_created_at ON exp_finlow.reimbursements(created_at);

-- ==========================================================
-- Comments
-- ==========================================================
COMMENT ON TABLE exp_finlow.reimbursements IS 'Payout obligations for PERSONAL_FUNDS expenses';
COMMENT ON COLUMN exp_finlow.reimbursements.expense_id IS 'Foreign key to expenses table (unique - one reimbursement per expense)';
COMMENT ON COLUMN exp_finlow.reimbursements.status IS 'PENDING = awaiting payment, PAID = payment completed';
COMMENT ON COLUMN exp_finlow.reimbursements.method IS 'Payment method: BANK_TRANSFER or PAYROLL';
