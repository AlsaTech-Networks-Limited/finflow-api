package com.alsatech.finflow.resource.expense.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

enum class ExpenseStatus { PENDING, APPROVED, REJECTED }

/**
 * Expense domain entity.
 *
 * The ID field is named `expenseId` in the domain model but mapped to `record_id` column in the database.
 * IDs are entity-identifiable with prefix 'exp_' (e.g., "exp_1704067200001").
 * Generated using Snowflake algorithm following Stripe/GitHub patterns.
 */
@Table(name = "expenses", schema = "exp_finlow")
data class Expense(
    @Id
    @Column("record_id")
    val expenseId: String,

    val userId: Long,
    val categoryId: Long,
    val amount: BigDecimal,
    val status: ExpenseStatus = ExpenseStatus.PENDING,
    val receiptUrl: String? = null,
    val notes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)
