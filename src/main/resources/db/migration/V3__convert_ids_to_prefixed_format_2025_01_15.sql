-- ==========================================================
-- Flyway Migration Script
-- Version: V3
-- File: V3__convert_ids_to_prefixed_format_2025_01_15.sql
-- Purpose: Convert ID columns from BIGINT to VARCHAR(30) for entity-identifiable IDs
-- Author: System
-- Date: 2025-01-15
-- Description:
--   Converts ID fields to use entity-identifiable prefixed format:
--   - Expense IDs: exp_1704067200001
--   - Approval IDs: app_1704067300001
--   Following industry patterns from Stripe, GitHub, and Slack
-- ==========================================================

-- Set search path to use exp_finlow schema
SET search_path TO exp_finlow;

-- ==========================================================
-- Drop foreign key constraint first (approvals.expense_id -> expenses.record_id)
-- ==========================================================
ALTER TABLE exp_finlow.approvals DROP CONSTRAINT IF EXISTS approvals_expense_id_fkey;

-- ==========================================================
-- Alter expenses table
-- ==========================================================
-- Change record_id from BIGINT to VARCHAR(30)
ALTER TABLE exp_finlow.expenses ALTER COLUMN record_id TYPE VARCHAR(30);

-- ==========================================================
-- Alter approvals table
-- ==========================================================
-- Change record_id from BIGINT to VARCHAR(30)
ALTER TABLE exp_finlow.approvals ALTER COLUMN record_id TYPE VARCHAR(30);

-- Change expense_id from BIGINT to VARCHAR(30) to match expenses.record_id
ALTER TABLE exp_finlow.approvals ALTER COLUMN expense_id TYPE VARCHAR(30);

-- ==========================================================
-- Recreate foreign key constraint
-- ==========================================================
ALTER TABLE exp_finlow.approvals ADD CONSTRAINT approvals_expense_id_fkey
    FOREIGN KEY (expense_id) REFERENCES exp_finlow.expenses(record_id)
    ON DELETE CASCADE;

-- ==========================================================
-- Notes
-- ==========================================================
-- This migration changes ID storage format from numeric to prefixed strings.
-- Application code now generates IDs with entity-specific prefixes:
--   - exp_ for expenses
--   - app_ for approvals
--   - acc_ for accounts
--   - etc.
--
-- Benefits:
--   - IDs are self-documenting (instantly recognizable entity type)
--   - Better debugging experience in logs
--   - Follows production-grade patterns from big tech companies
--   - Still uses Snowflake algorithm for time-ordering and uniqueness
-- ==========================================================
