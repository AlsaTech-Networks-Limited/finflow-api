package com.alsatech.finflow.resource.expense.application.usecase

import com.alsatech.finflow.resource.expense.application.dto.SubmitExpenseCommand
import com.alsatech.finflow.resource.expense.domain.Expense
import com.alsatech.finflow.resource.expense.domain.ExpenseRepository
import com.alsatech.finflow.resource.expense.domain.ExpenseStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.Instant

/**
 * Use Case: Submit an expense for approval.
 *
 * Business Rules:
 * - Amount must be positive
 * - Category and User must exist (validated at API level)
 * - New expenses start with PENDING status
 *
 * Flow: Command → Validate → Create Expense → Save → Return
 */
@Service
@Transactional
class SubmitExpenseUseCase(
    private val expenseRepository: ExpenseRepository
) {
    fun execute(command: SubmitExpenseCommand): Mono<Long> {
        // TODO: Add business validation
        // - Validate user exists
        // - Validate category exists
        // - Check expense limits if needed

        val expense = Expense(
            id = null,
            userId = command.userId,
            categoryId = command.categoryId,
            amount = command.amount,
            status = ExpenseStatus.PENDING,
            receiptUrl = command.receiptUrl,
            notes = command.notes,
            createdAt = Instant.now()
        )

        return expenseRepository.save(expense)
            .map { it.id!! }
    }
}
