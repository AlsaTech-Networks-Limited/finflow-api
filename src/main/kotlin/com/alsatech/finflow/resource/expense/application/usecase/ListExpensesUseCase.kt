package com.alsatech.finflow.resource.expense.application.usecase

import com.alsatech.finflow.resource.expense.application.dto.ExpenseDto
import com.alsatech.finflow.resource.expense.application.dto.ListExpensesQuery
import com.alsatech.finflow.resource.expense.domain.ExpenseRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

/**
 * Use Case: List expenses with optional filters.
 *
 * Supports filtering by:
 * - User ID (all expenses for a user)
 * - Company ID (pending expenses for a company)
 * - Status (future enhancement)
 *
 * Flow: Query → Filter → Map to DTOs → Return
 */
@Service
class ListExpensesUseCase(
    private val expenseRepository: ExpenseRepository
) {
    fun execute(query: ListExpensesQuery): Flux<ExpenseDto> {
        val expensesFlux = when {
            query.userId != null -> expenseRepository.findByUserId(query.userId)
            query.companyId != null -> expenseRepository.findPendingByCompany(query.companyId)
            else -> Flux.empty()
        }

        return expensesFlux
            .map { expense ->
                ExpenseDto(
                    id = expense.id!!,
                    userId = expense.userId,
                    categoryId = expense.categoryId,
                    amount = expense.amount,
                    status = expense.status,
                    receiptUrl = expense.receiptUrl,
                    notes = expense.notes,
                    createdAt = expense.createdAt
                )
            }
    }
}
