package com.alsatech.finflow.resource.expense.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

enum class ExpenseStatus { PENDING, APPROVED, REJECTED }

@Table("expenses")
data class Expense(
    @Id val id: Long? = null,
    val userId: Long,
    val categoryId: Long,
    val amount: BigDecimal,
    val status: ExpenseStatus = ExpenseStatus.PENDING,
    val receiptUrl: String? = null,
    val notes: String? = null,
    val createdAt: Instant = Instant.now(),
)
