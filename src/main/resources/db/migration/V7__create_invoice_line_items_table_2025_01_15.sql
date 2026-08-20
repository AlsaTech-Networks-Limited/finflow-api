-- ==========================================================
-- Flyway Migration Script
-- Version: V7
-- File: V7__create_invoice_line_items_table_2025_01_15.sql
-- Purpose: Create invoices + invoice_line_items tables
-- Author: System
-- Date: 2025-01-15
-- Note: renumbered from V5 to V7 to avoid colliding with
-- V5 (accounts.created_at) and V6 (reimbursements), which had
-- already been added independently and already applied/pending.
-- ==========================================================

-- Set search path to use exp_finlow schema
SET search_path TO exp_finlow;

-- ==========================================================
-- Drop existing table (for development resets)
-- ==========================================================
DROP TABLE IF EXISTS exp_finlow.invoice_line_items CASCADE;
DROP TABLE IF EXISTS exp_finlow.invoices CASCADE;

-- ==========================================================
-- Table: invoices
-- Purpose: Store client invoices
-- ==========================================================
CREATE TABLE exp_finlow.invoices (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES exp_finlow.companies(id) ON DELETE CASCADE,
    invoice_number VARCHAR(100) NOT NULL UNIQUE,
    client_name VARCHAR(250) NOT NULL,
    client_email VARCHAR(250),
    amount NUMERIC(14, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'SENT', 'PAID', 'OVERDUE', 'CANCELLED')),
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ==========================================================
-- Table: invoice_line_items
-- Purpose: Store itemized invoice lines
-- ==========================================================
CREATE TABLE exp_finlow.invoice_line_items (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL REFERENCES exp_finlow.invoices(id) ON DELETE CASCADE,
    description TEXT NOT NULL,
    quantity NUMERIC(10, 2) NOT NULL,
    unit_price NUMERIC(14, 2) NOT NULL,
    amount NUMERIC(14, 2) NOT NULL,
    position INT NOT NULL
);

-- ==========================================================
-- Indexes
-- ==========================================================
CREATE INDEX IF NOT EXISTS idx_invoices_company_id ON exp_finlow.invoices(company_id);
CREATE INDEX IF NOT EXISTS idx_invoices_status ON exp_finlow.invoices(status);
CREATE INDEX IF NOT EXISTS idx_invoices_invoice_number ON exp_finlow.invoices(invoice_number);
CREATE INDEX IF NOT EXISTS idx_invoice_line_items_invoice_id ON exp_finlow.invoice_line_items(invoice_id);
