package com.alsatech.finflow.resource.expense.application.usecase

import com.alsatech.finflow.resource.expense.application.dto.ApproveExpenseCommand
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
 * Use Case: Approve a pending expense.
 *
 * Business Rules:
 * - Expense must exist
 * - Expense must be in PENDING status
 * - Users cannot approve their own expenses
 *
 * Flow: Command → Validate → Update Expense → Create Approval → Return
 */
@Service
@Transactional
class ApproveExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val approvalRepository: ApprovalRepository
) {
    fun execute(command: ApproveExpenseCommand): Mono<Void> {
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
                            "SELF_APPROVAL_NOT_ALLOWED",
                            "Users cannot approve their own expenses"
                        ))

                    else -> {
                        // Update expense status
                        val updatedExpense = expense.copy(status = ExpenseStatus.APPROVED)
                        expenseRepository.save(updatedExpense)
                            .flatMap { savedExpense ->
                                // Create approval record
                                val approval = Approval(
                                    id = null,
                                    expenseId = savedExpense.id!!,
                                    approverId = command.approverId,
                                    decision = "APPROVED",
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
