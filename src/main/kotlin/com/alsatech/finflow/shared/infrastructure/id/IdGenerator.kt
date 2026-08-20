package com.alsatech.finflow.shared.infrastructure.id

import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Production-grade distributed ID generator using Snowflake algorithm with entity type prefixes.
 *
 * Follows industry best practices from Stripe, GitHub, Slack:
 * - Entity-identifiable IDs with prefixes (exp_, app_, acc_, etc.)
 * - Time-ordered Snowflake IDs for sortability
 * - Human-readable and debuggable
 * - Type-safe with EntityType enum
 *
 * 64-bit Snowflake ID structure:
 * - 1 bit: unused (always 0)
 * - 41 bits: timestamp in milliseconds (since custom epoch)
 * - 10 bits: worker/machine ID (supports 1024 workers)
 * - 12 bits: sequence number (4096 IDs per millisecond per worker)
 *
 * Final ID format: {prefix}_{snowflake_id}
 * Examples:
 * - exp_1704067200001 (Expense)
 * - app_1704067300001 (Approval)
 * - acc_1704068400001 (Account)
 *
 * Usage:
 * ```kotlin
 * val expenseId = idGenerator.generateId(EntityType.EXPENSE)   // "exp_1704067200001"
 * val approvalId = idGenerator.generateId(EntityType.APPROVAL) // "app_1704067300001"
 * ```
 */
@Component
class IdGenerator(
    private val workerId: Long = getWorkerId()
) {
    companion object {
        // Custom epoch (2024-01-01 00:00:00 UTC) - saves bits for ~69 years from this date
        private const val CUSTOM_EPOCH = 1704067200000L

        // Bit lengths
        private const val WORKER_ID_BITS = 10L
        private const val SEQUENCE_BITS = 12L

        // Max values
        private const val MAX_WORKER_ID = (1L shl WORKER_ID_BITS.toInt()) - 1  // 1023
        private const val MAX_SEQUENCE = (1L shl SEQUENCE_BITS.toInt()) - 1    // 4095

        // Shifts
        private const val WORKER_ID_SHIFT = SEQUENCE_BITS
        private const val TIMESTAMP_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS

        /**
         * Get worker ID from environment or use default.
         * In production, set via environment variable: WORKER_ID
         * In Kubernetes, can use pod ordinal or node hash.
         */
        private fun getWorkerId(): Long {
            val workerIdFromEnv = System.getenv("WORKER_ID")?.toLongOrNull()
            val workerId = workerIdFromEnv ?: (System.currentTimeMillis() % MAX_WORKER_ID)

            require(workerId in 0..MAX_WORKER_ID) {
                "Worker ID must be between 0 and $MAX_WORKER_ID, got: $workerId"
            }

            return workerId
        }
    }

    @Volatile
    private var lastTimestamp = -1L

    @Volatile
    private var sequence = 0L

    /**
     * Generate a new unique ID with entity type prefix (RECOMMENDED).
     *
     * This is the primary method for generating IDs in production.
     * Returns a prefixed ID following industry best practices.
     *
     * Examples:
     * - generateId(EntityType.EXPENSE)  → "exp_1704067200001"
     * - generateId(EntityType.APPROVAL) → "app_1704067300001"
     * - generateId(EntityType.ACCOUNT)  → "acc_1704068400001"
     *
     * @param entityType The type of entity being created
     * @return Prefixed ID string (e.g., "exp_1704067200001")
     */
    @Synchronized
    fun generateId(entityType: EntityType): String {
        val snowflakeId = generateSnowflake()
        return "${entityType.prefix}_$snowflakeId"
    }

    /**
     * Generate a raw Snowflake ID without prefix (internal use).
     * Thread-safe and blocks if clock moves backwards.
     *
     * @return 64-bit Snowflake ID
     */
    @Synchronized
    private fun generateSnowflake(): Long {
        var timestamp = currentTimestamp()

        // Clock moved backwards - wait until it catches up
        if (timestamp < lastTimestamp) {
            val diff = lastTimestamp - timestamp
            throw IllegalStateException(
                "Clock moved backwards by $diff ms. Refusing to generate ID."
            )
        }

        // Same millisecond - increment sequence
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) and MAX_SEQUENCE

            // Sequence exhausted - wait for next millisecond
            if (sequence == 0L) {
                timestamp = waitNextMillis(lastTimestamp)
            }
        } else {
            // New millisecond - reset sequence
            sequence = 0L
        }

        lastTimestamp = timestamp

        // Construct the ID
        return ((timestamp - CUSTOM_EPOCH) shl TIMESTAMP_SHIFT.toInt()) or
                (workerId shl WORKER_ID_SHIFT.toInt()) or
                sequence
    }

    /**
     * Generate a raw Snowflake ID (DEPRECATED - use generateId(EntityType) instead).
     *
     * @deprecated Use generateId(EntityType) for entity-identifiable IDs
     * @return 64-bit Snowflake ID
     */
    @Deprecated(
        message = "Use generateId(EntityType) for production code",
        replaceWith = ReplaceWith("generateId(EntityType.EXPENSE)")
    )
    @Synchronized
    fun generate(): Long = generateSnowflake()

    /**
     * Generate multiple prefixed IDs at once (for batch operations).
     *
     * @param entityType The type of entity
     * @param count Number of IDs to generate
     * @return List of prefixed IDs
     */
    fun generateBatch(entityType: EntityType, count: Int): List<String> {
        require(count > 0) { "Count must be positive" }
        return List(count) { generateId(entityType) }
    }

    /**
     * Extract timestamp from a prefixed ID.
     *
     * @param id Prefixed ID (e.g., "exp_1704067200001")
     * @return Instant when the ID was created
     */
    fun extractTimestamp(id: String): Instant? {
        val snowflakeId = EntityType.extractSnowflakeId(id) ?: return null
        val timestampMillis = (snowflakeId shr TIMESTAMP_SHIFT.toInt()) + CUSTOM_EPOCH
        return Instant.ofEpochMilli(timestampMillis)
    }

    /**
     * Extract worker ID from a prefixed ID.
     *
     * @param id Prefixed ID (e.g., "exp_1704067200001")
     * @return Worker ID (0-1023)
     */
    fun extractWorkerId(id: String): Long? {
        val snowflakeId = EntityType.extractSnowflakeId(id) ?: return null
        return (snowflakeId shr WORKER_ID_SHIFT.toInt()) and MAX_WORKER_ID
    }

    /**
     * Extract sequence number from a prefixed ID.
     *
     * @param id Prefixed ID (e.g., "exp_1704067200001")
     * @return Sequence number (0-4095)
     */
    fun extractSequence(id: String): Long? {
        val snowflakeId = EntityType.extractSnowflakeId(id) ?: return null
        return snowflakeId and MAX_SEQUENCE
    }

    /**
     * Extract entity type from a prefixed ID.
     *
     * @param id Prefixed ID (e.g., "exp_1704067200001")
     * @return EntityType or null if invalid
     */
    fun extractEntityType(id: String): EntityType? {
        return EntityType.fromId(id)
    }

    /**
     * Validate ID format.
     *
     * @param id The ID to validate
     * @return true if valid prefixed ID format
     */
    fun isValidId(id: String): Boolean {
        return EntityType.isValidId(id)
    }

    private fun currentTimestamp(): Long = System.currentTimeMillis()

    private fun waitNextMillis(lastTimestamp: Long): Long {
        var timestamp = currentTimestamp()
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimestamp()
        }
        return timestamp
    }
}