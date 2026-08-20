package com.alsatech.finflow.resource.dashboard.dto

import com.alsatech.finflow.resource.expense.domain.entity.ExpenseStatus
import com.alsatech.finflow.shared.domain.entity.TransactionType
import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Main dashboard response DTO.
 * Aggregated summary for the current user's company.
 */
data class DashboardDto(
    val companyId: Long,
    val accountsSummary: AccountsSummaryDto,
    val expensesSummary: ExpensesSummaryDto,
    val recentTransactions: List<RecentTransactionDto>,
    val categoriesCount: Int,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val generatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Summary of all accounts for the company.
 */
data class AccountsSummaryDto(
    val totalAccounts: Int,
    val totalBalance: BigDecimal,
    val currency: String = "USD"
)

/**
 * Summary of expenses by status.
 */
data class ExpensesSummaryDto(
    val totalExpenses: Int,
    val pendingCount: Int,
    val approvedCount: Int,
    val rejectedCount: Int,
    val totalAmount: BigDecimal,
    val pendingAmount: BigDecimal,
    val approvedAmount: BigDecimal,
    val rejectedAmount: BigDecimal
)

/**
 * Recent transaction for dashboard display.
 */
data class RecentTransactionDto(
    val id: Long,
    val accountName: String,
    val type: TransactionType,
    val amount: BigDecimal,
    val description: String?,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val occurredAt: LocalDateTime
)

/**
 * Query for fetching dashboard data.
 */
data class GetDashboardQuery(
    val companyId: Long
)
