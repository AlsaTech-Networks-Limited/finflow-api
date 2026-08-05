package com.alsatech.finflow.resource.expense.application.usecase

import com.alsatech.finflow.resource.expense.application.dto.RejectExpenseCommand
import com.alsatech.finflow.resource.expense.domain.Approval
import com.alsatech.finflow.resource.expense.domain.ApprovalRepository
import com.alsatech.finflow.resource.expense.domain.ExpenseRepository
import com.alsatech.finflow.resource.expense.domain.ExpenseStatus
import com.collicode.common.exception.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.Instant

/**
 * Use Case: Reject a pending expense.
 *
 * Business Rules:
 * - Expense must exist
 * - Expense must be in PENDING status
 * - Users cannot reject their own expenses
 * - Rejection comment is mandatory
 *
 * Flow: Command → Validate → Update Expense → Create Approval → Return
 */
@Service
@Transactional
class RejectExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val approvalRepository: ApprovalRepository
) {
    fun execute(command: RejectExpenseCommand): Mono<Void> {
        return expenseRepository.findById(command.expenseId)
            .switchIfEmpty(Mono.error(BusinessException(
                "EXPENSE_NOT_FOUND",
                "Expense not found with ID: ${command.expenseId}"
            )))
            .flatMap { expense ->
                // Validate business rules
                when {
                    expense.status != ExpenseStatus.PENDING ->
                        Mono.error(BusinessException(
                            "EXPENSE_NOT_PENDING",
                            "Expense is not in PENDING status"
                        ))

                    expense.userId == command.approverId ->
                        Mono.error(BusinessException(
                            "SELF_REJECTION_NOT_ALLOWED",
                            "Users cannot reject their own expenses"
                        ))

                    else -> {
                        // Update expense status
                        val updatedExpense = expense.copy(status = ExpenseStatus.REJECTED)
                        expenseRepository.save(updatedExpense)
                            .flatMap { savedExpense ->
                                // Create approval record
                                val approval = Approval(
                                    id = null,
                                    expenseId = savedExpense.id!!,
                                    approverId = command.approverId,
                                    decision = "REJECTED",
                                    comment = command.comment,
                                    decidedAt = Instant.now()
                                )
                                approvalRepository.save(approval)
                            }
                            .then()
                    }
                }
            }
    }
}
