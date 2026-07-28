package com.alsatech.finflow.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

enum class ExpenseStatus { PENDING, APPROVED, REJECTED }

@Table("expenses")
data class Expense(
    @Id val id: UUID? = null,
    val userId: UUID,
    val categoryId: UUID,
    val amount: BigDecimal,
    val status: ExpenseStatus = ExpenseStatus.PENDING,
    val receiptUrl: String? = null,
    val notes: String? = null,
    val createdAt: Instant = Instant.now(),
)
