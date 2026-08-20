-- ==========================================================
-- Flyway Migration Script
-- Version: V5
-- File: V5__add_created_at_to_accounts_2026_08_18.sql
-- Purpose: shared.domain.entity.Account has a `createdAt` field that Spring
--          Data R2DBC maps to a `created_at` column by convention, but the
--          `accounts` table (V1__init.sql) never defined one. Every UPDATE
--          through R2dbcEntityTemplate (e.g. the account-balance write in
--          TransactionWriteServiceImpl.updateAccountBalance) fails with
--          PostgresqlBadGrammarException: column "created_at" of relation
--          "accounts" does not exist. INSERT happens not to reference it in
--          the same way, which is why account creation alone didn't surface
--          this. Add the column the entity has always expected.
-- ==========================================================

SET search_path TO exp_finlow;

ALTER TABLE exp_finlow.accounts
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
