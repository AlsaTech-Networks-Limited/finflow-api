package com.alsatech.finflow.shared.infrastructure.id

/**
 * Defines entity types with their ID prefixes.
 *
 * Following industry best practices from Stripe, GitHub, Slack:
 * - Short, memorable prefixes (3 chars)
 * - Lowercase for consistency
 * - Instantly recognizable
 *
 * Format: {prefix}_{snowflake_id}
 * Example: exp_1704067200001
 */
enum class EntityType(
    val prefix: String,
    val description: String
) {
    // Expense Module
    EXPENSE("exp", "Expense record"),
    APPROVAL("app", "Expense approval/rejection"),

    // Account Module
    ACCOUNT("acc", "Financial account"),
    TRANSACTION("txn", "Financial transaction"),
    INVOICE("inv", "Invoice"),

    // Category Module
    CATEGORY("cat", "Expense category"),

    // User Module
    USER("usr", "User account"),
    COMPANY("cmp", "Company/Organization"),

    // Future modules
    BUDGET("bud", "Budget"),
    REPORT("rpt", "Report"),
    ATTACHMENT("att", "File attachment");

    companion object {
        /**
         * Parse entity type from a prefixed ID.
         *
         * @param id The prefixed ID (e.g., "exp_1704067200001")
         * @return The EntityType or null if invalid
         */
        fun fromId(id: String): EntityType? {
            if (!id.contains("_")) return null
            val prefix = id.substringBefore("_")
            return values().find { it.prefix == prefix }
        }

        /**
         * Extract the numeric Snowflake ID from a prefixed ID.
         *
         * @param id The prefixed ID (e.g., "exp_1704067200001")
         * @return The numeric Snowflake ID or null if invalid
         */
        fun extractSnowflakeId(id: String): Long? {
            if (!id.contains("_")) return null
            return id.substringAfter("_").toLongOrNull()
        }

        /**
         * Validate ID format.
         *
         * @param id The prefixed ID to validate
         * @return true if valid format
         */
        fun isValidId(id: String): Boolean {
            if (!id.contains("_")) return false
            val parts = id.split("_")
            if (parts.size != 2) return false

            val prefix = parts[0]
            val snowflakeId = parts[1].toLongOrNull() ?: return false

            return values().any { it.prefix == prefix } && snowflakeId > 0
        }
    }
}
