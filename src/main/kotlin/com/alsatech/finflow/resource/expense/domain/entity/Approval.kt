package com.alsatech.finflow.resource.expense.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * Approval domain entity.
 *
 * The ID field is named `approvalId` in the domain model but mapped to `record_id` column in the database.
 * IDs are entity-identifiable with prefix 'app_' (e.g., "app_1704067300001").
 * Generated using Snowflake algorithm following Stripe/GitHub patterns.
 */
@Table(name = "approvals", schema = "exp_finlow")
data class Approval(
    @Id
    @Column("record_id")
    val approvalId: String,

    val expenseId: String,
    val approverId: Long,
    val decision: String,
    val comment: String? = null,
    val decidedAt: LocalDateTime = LocalDateTime.now(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
