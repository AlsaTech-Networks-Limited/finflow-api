package com.alsatech.finflow.resource.expense.infrastructure.service

import com.alsatech.finflow.resource.expense.domain.entity.ExpenseStatus
import com.alsatech.finflow.resource.expense.domain.repository.ExpenseRepository
import com.alsatech.finflow.resource.expense.domain.service.ExpenseValidationService
import com.alsatech.finflow.resource.expense.dto.ApproveExpenseCommand
import com.alsatech.finflow.resource.expense.dto.RejectExpenseCommand
import com.alsatech.finflow.resource.expense.dto.SubmitExpenseCommand
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class ExpenseValidationServiceImpl(
    private val expenseRepository: ExpenseRepository
) : ExpenseValidationService {

    override fun validateSubmitExpense(command: SubmitExpenseCommand): Mono<SubmitExpenseCommand> {
        return Mono.just(command)
            .flatMap { validateAmount(it.amount).thenReturn(it) }
            .flatMap { validateNotes(it.notes).thenReturn(it) }
    }

    override fun validateApproveExpense(command: ApproveExpenseCommand): Mono<ApproveExpenseCommand> {
        return Mono.just(command)
            .flatMap { validateExpenseExists(it.expenseId).thenReturn(it) }
            .flatMap { validateExpenseStatus(it.expenseId, ExpenseStatus.PENDING).thenReturn(it) }
            .flatMap { validateComment(it.comment).thenReturn(it) }
    }

    override fun validateRejectExpense(command: RejectExpenseCommand): Mono<RejectExpenseCommand> {
        return Mono.just(command)
            .flatMap { validateExpenseExists(it.expenseId).thenReturn(it) }
            .flatMap { validateExpenseStatus(it.expenseId, ExpenseStatus.PENDING).thenReturn(it) }
            .flatMap { validateComment(it.comment).thenReturn(it) }
    }

    // =================== Private Validation Helpers ===================

    private fun validateAmount(amount: BigDecimal): Mono<Unit> {
        return if (amount <= BigDecimal.ZERO) {
            Mono.error(IllegalArgumentException("Amount must be greater than zero"))
        } else if (amount > BigDecimal("999999999.99")) {
            Mono.error(IllegalArgumentException("Amount exceeds maximum allowed value"))
        } else {
            Mono.just(Unit)
        }
    }

    private fun validateNotes(notes: String?): Mono<Unit> {
        return if (notes != null && notes.length > 500) {
            Mono.error(IllegalArgumentException("Notes must not exceed 500 characters"))
        } else {
            Mono.just(Unit)
        }
    }

    private fun validateComment(comment: String?): Mono<Unit> {
        return if (comment != null && comment.length > 500) {
            Mono.error(IllegalArgumentException("Comment must not exceed 500 characters"))
        } else {
            Mono.just(Unit)
        }
    }

    private fun validateExpenseExists(expenseId: String): Mono<Unit> {
        return expenseRepository.findById(expenseId)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Expense with ID $expenseId not found")))
            .then(Mono.just(Unit))
    }

    private fun validateExpenseStatus(expenseId: String, expectedStatus: ExpenseStatus): Mono<Unit> {
        return expenseRepository.findById(expenseId)
            .flatMap { expense ->
                if (expense.status != expectedStatus) {
                    Mono.error(IllegalStateException("Expense must be in $expectedStatus status to perform this action. Current status: ${expense.status}"))
                } else {
                    Mono.just(Unit)
                }
            }
    }
}
