-- ==========================================================
-- Flyway Repeatable Migration Script
-- File: R__expense_seed_data.sql
-- Purpose: Seed expense test data for development and testing
-- Author: System
-- Date: 2025-01-15
-- Note: This runs every time the checksum changes
-- ==========================================================

-- Set search path to use exp_finlow schema
SET search_path TO exp_finlow;

-- ==========================================================
-- Insert Test Companies
-- ==========================================================
INSERT INTO exp_finlow.companies (id, name, plan, created_at) VALUES
(1, 'Tech Startup Inc', 'premium', NOW()),
(2, 'Marketing Agency Ltd', 'free', NOW())
ON CONFLICT (id) DO NOTHING;

-- Reset sequence
SELECT setval('exp_finlow.companies_id_seq', (SELECT MAX(id) FROM exp_finlow.companies));

-- ==========================================================
-- Insert Test Users
-- ==========================================================
INSERT INTO exp_finlow.users (id, company_id, email, password_hash, role, created_at) VALUES
(1, 1, 'john.doe@techstartup.com', '$2a$10$dummyhashforjohn', 'USER', NOW()),
(2, 1, 'jane.manager@techstartup.com', '$2a$10$dummyhashforjane', 'MANAGER', NOW()),
(3, 1, 'admin@techstartup.com', '$2a$10$dummyhashforadmin', 'ADMIN', NOW()),
(4, 2, 'bob@marketing.com', '$2a$10$dummyhashforbob', 'USER', NOW())
ON CONFLICT (id) DO NOTHING;

-- Reset sequence
SELECT setval('exp_finlow.users_id_seq', (SELECT MAX(id) FROM exp_finlow.users));

-- ==========================================================
-- Insert Test Categories
-- ==========================================================
INSERT INTO exp_finlow.categories (id, company_id, name, parent_id) VALUES
(1, 1, 'Travel', NULL),
(2, 1, 'Meals', NULL),
(3, 1, 'Office Supplies', NULL),
(4, 1, 'Flights', 1),
(5, 1, 'Hotels', 1),
(6, 1, 'Client Meetings', 2),
(7, 2, 'Marketing', NULL)
ON CONFLICT (id) DO NOTHING;

-- Reset sequence
SELECT setval('exp_finlow.categories_id_seq', (SELECT MAX(id) FROM exp_finlow.categories));

-- ==========================================================
-- Insert Test Accounts
-- ==========================================================
INSERT INTO exp_finlow.accounts (id, company_id, name, currency, balance, type) VALUES
(1, 1, 'Operating Account', 'USD', 50000.00, 'BANK'),
(2, 1, 'Petty Cash', 'USD', 2000.00, 'CASH'),
(3, 2, 'Main Account', 'USD', 10000.00, 'BANK')
ON CONFLICT (id) DO NOTHING;

-- Reset sequence
SELECT setval('exp_finlow.accounts_id_seq', (SELECT MAX(id) FROM exp_finlow.accounts));

-- ==========================================================
-- Insert Test Expenses
-- Note: Using Snowflake-style IDs (application-generated)
-- ==========================================================
INSERT INTO exp_finlow.expenses (record_id, user_id, category_id, amount, status, receipt_url, notes, created_at, updated_at) VALUES
(1704067200001, 1, 6, 150.50, 'PENDING', 'https://example.com/receipts/001.pdf', 'Client lunch meeting with ABC Corp', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
(1704067200002, 1, 4, 850.00, 'APPROVED', 'https://example.com/receipts/002.pdf', 'Flight to San Francisco for conference', NOW() - INTERVAL '5 days', NOW() - INTERVAL '4 days'),
(1704067200003, 1, 3, 45.75, 'REJECTED', NULL, 'Office supplies - missing receipt', NOW() - INTERVAL '7 days', NOW() - INTERVAL '6 days'),
(1704067200004, 4, 7, 200.00, 'PENDING', 'https://example.com/receipts/004.pdf', 'Marketing campaign materials', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day')
ON CONFLICT (record_id) DO NOTHING;

-- ==========================================================
-- Insert Test Approvals
-- Note: Using Snowflake-style IDs (application-generated)
-- ==========================================================
INSERT INTO exp_finlow.approvals (record_id, expense_id, approver_id, decision, comment, decided_at, created_at, updated_at) VALUES
(1704067300001, 1704067200002, 2, 'APPROVED', 'Valid business expense for conference attendance', NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
(1704067300002, 1704067200003, 2, 'REJECTED', 'Receipt required for office supply purchases', NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days')
ON CONFLICT (record_id) DO NOTHING;
