package com.alsatech.finflow.resource.dashboard.infrastructure.service.impl

import com.alsatech.finflow.resource.dashboard.dto.*
import com.alsatech.finflow.resource.dashboard.infrastructure.service.DashboardService
import com.alsatech.finflow.resource.expense.domain.entity.ExpenseStatus
import com.alsatech.finflow.resource.expense.domain.repository.ExpenseRepository
import com.alsatech.finflow.shared.domain.repository.AccountRepository
import com.alsatech.finflow.shared.domain.repository.CategoryRepository
import com.alsatech.finflow.shared.domain.repository.TransactionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class DashboardServiceImpl(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : DashboardService {

    override fun fetchDashboard(companyId: Long): Mono<DashboardDto> {
        return Mono.zip(
            buildAccountsSummary(companyId),
            buildExpensesSummary(companyId),
            buildRecentTransactions(companyId),
            getCategoriesCount(companyId)
        ).map { tuple ->
            DashboardDto(
                companyId = companyId,
                accountsSummary = tuple.t1,
                expensesSummary = tuple.t2,
                recentTransactions = tuple.t3,
                categoriesCount = tuple.t4,
                generatedAt = LocalDateTime.now()
            )
        }
    }

    private fun buildAccountsSummary(companyId: Long): Mono<AccountsSummaryDto> {
        return accountRepository.findByCompanyId(companyId)
            .collectList()
            .map { accounts ->
                val totalBalance = accounts.fold(BigDecimal.ZERO) { acc, account ->
                    acc + account.balance
                }
                AccountsSummaryDto(
                    totalAccounts = accounts.size,
                    totalBalance = totalBalance,
                    currency = accounts.firstOrNull()?.currency ?: "USD"
                )
            }
            .defaultIfEmpty(
                AccountsSummaryDto(
                    totalAccounts = 0,
                    totalBalance = BigDecimal.ZERO,
                    currency = "USD"
                )
            )
    }

    private fun buildExpensesSummary(companyId: Long): Mono<ExpensesSummaryDto> {
        return expenseRepository.findByCompany(companyId)
            .collectList()
            .flatMap { allExpenses ->
                val totalExpenses = allExpenses.size
                val pendingExpenses = allExpenses.filter { it.status == ExpenseStatus.PENDING }
                val approvedExpenses = allExpenses.filter { it.status == ExpenseStatus.APPROVED }
                val rejectedExpenses = allExpenses.filter { it.status == ExpenseStatus.REJECTED }

                val totalAmount = allExpenses.fold(BigDecimal.ZERO) { acc, expense -> acc + expense.amount }
                val pendingAmount = pendingExpenses.fold(BigDecimal.ZERO) { acc, expense -> acc + expense.amount }
                val approvedAmount = approvedExpenses.fold(BigDecimal.ZERO) { acc, expense -> acc + expense.amount }
                val rejectedAmount = rejectedExpenses.fold(BigDecimal.ZERO) { acc, expense -> acc + expense.amount }

                Mono.just(
                    ExpensesSummaryDto(
                        totalExpenses = totalExpenses,
                        pendingCount = pendingExpenses.size,
                        approvedCount = approvedExpenses.size,
                        rejectedCount = rejectedExpenses.size,
                        totalAmount = totalAmount,
                        pendingAmount = pendingAmount,
                        approvedAmount = approvedAmount,
                        rejectedAmount = rejectedAmount
                    )
                )
            }
            .defaultIfEmpty(
                ExpensesSummaryDto(
                    totalExpenses = 0,
                    pendingCount = 0,
                    approvedCount = 0,
                    rejectedCount = 0,
                    totalAmount = BigDecimal.ZERO,
                    pendingAmount = BigDecimal.ZERO,
                    approvedAmount = BigDecimal.ZERO,
                    rejectedAmount = BigDecimal.ZERO
                )
            )
    }

    private fun buildRecentTransactions(companyId: Long): Mono<List<RecentTransactionDto>> {
        return transactionRepository.findByCompanyId(companyId)
            .take(10) // Get last 10 transactions
            .flatMap { transaction ->
                accountRepository.findById(transaction.accountId)
                    .map { account ->
                        RecentTransactionDto(
                            id = transaction.id!!,
                            accountName = account.name,
                            type = transaction.type,
                            amount = transaction.amount,
                            description = transaction.description,
                            occurredAt = transaction.occurredAt
                        )
                    }
            }
            .collectList()
    }

    private fun getCategoriesCount(companyId: Long): Mono<Int> {
        return categoryRepository.findByCompanyId(companyId)
            .count()
            .map { it.toInt() }
            .defaultIfEmpty(0)
    }
}
