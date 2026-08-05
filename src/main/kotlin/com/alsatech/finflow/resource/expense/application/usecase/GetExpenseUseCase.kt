package com.alsatech.finflow.resource.expense.application.usecase

import com.alsatech.finflow.resource.expense.application.dto.ApprovalDto
import com.alsatech.finflow.resource.expense.application.dto.ExpenseDetailDto
import com.alsatech.finflow.resource.expense.domain.ApprovalRepository
import com.alsatech.finflow.resource.expense.domain.ExpenseRepository
import com.collicode.common.exception.BusinessException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * Use Case: Get expense details by ID with approvals.
 *
 * Returns complete expense information including all approval records.
 *
 * Flow: Query → Find Expense → Find Approvals → Map to DTO → Return
 */
@Service
class GetExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val approvalRepository: ApprovalRepository
) {
    fun execute(expenseId: Long): Mono<ExpenseDetailDto> {
        return expenseRepository.findById(expenseId)
            .switchIfEmpty(Mono.error(BusinessException(
                "EXPENSE_NOT_FOUND",
                "Expense not found with ID: $expenseId"
            )))
            .flatMap { expense ->
                approvalRepository.findByExpenseId(expense.id!!)
                    .map { approval ->
                        ApprovalDto(
                            approverId = approval.approverId,
                            decision = approval.decision,
                            comment = approval.comment,
                            decidedAt = approval.decidedAt
                        )
                    }
                    .collectList()
                    .map { approvals ->
                        ExpenseDetailDto(
                            id = expense.id!!,
                            userId = expense.userId,
                            categoryId = expense.categoryId,
                            amount = expense.amount,
                            status = expense.status,
                            receiptUrl = expense.receiptUrl,
                            notes = expense.notes,
                            approvals = approvals,
                            createdAt = expense.createdAt
                        )
                    }
            }
    }
}
