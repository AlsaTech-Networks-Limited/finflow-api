package com.alsatech.finflow.resource.expense.infrastructure.service.impl

import com.alsatech.finflow.resource.expense.dto.ApprovalDto
import com.alsatech.finflow.resource.expense.dto.ExpenseDetailDto
import com.alsatech.finflow.resource.expense.dto.ExpenseDto
import com.alsatech.finflow.resource.expense.domain.repository.ApprovalRepository
import com.alsatech.finflow.resource.expense.domain.repository.ExpenseRepository
import com.alsatech.finflow.resource.expense.infrastructure.service.ExpenseReadService
import com.collicode.common.exception.BusinessException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ExpenseReadServiceImpl(
    private val expenseRepository: ExpenseRepository,
    private val approvalRepository: ApprovalRepository
) : ExpenseReadService {

    override fun fetchExpenseById(expenseId: String): Mono<ExpenseDetailDto> {
        return expenseRepository.findById(expenseId)
            .switchIfEmpty(Mono.error(BusinessException.exception(
                "EXPENSE_NOT_FOUND",
                "Expense not found with ID: $expenseId"
            )))
            .flatMap { expense ->
                approvalRepository.findByExpenseId(expense.expenseId)
                    .map { approval ->
                        ApprovalDto(
                            approvalId = approval.approvalId,
                            expenseId = approval.expenseId,
                            approverId = approval.approverId,
                            decision = approval.decision,
                            comment = approval.comment,
                            decidedAt = approval.decidedAt,
                            createdAt = approval.createdAt,
                            updatedAt = approval.updatedAt
                        )
                    }
                    .collectList()
                    .map { approvals ->
                        ExpenseDetailDto(
                            expenseId = expense.expenseId,
                            userId = expense.userId,
                            categoryId = expense.categoryId,
                            amount = expense.amount,
                            status = expense.status,
                            receiptUrl = expense.receiptUrl,
                            notes = expense.notes,
                            approvals = approvals,
                            createdAt = expense.createdAt,
                            updatedAt = expense.updatedAt
                        )
                    }
            }
    }

    override fun fetchAllExpenses(queryParams: Map<String, String>): Flux<ExpenseDto> {
        val userId = queryParams["userId"]?.toLongOrNull()
        val companyId = queryParams["companyId"]?.toLongOrNull()

        val expensesFlux = when {
            userId != null -> expenseRepository.findByUserId(userId)
            companyId != null -> expenseRepository.findPendingByCompany(companyId)
            else -> Flux.empty()
        }

        return expensesFlux
            .map { expense ->
                ExpenseDto(
                    expenseId = expense.expenseId,
                    userId = expense.userId,
                    categoryId = expense.categoryId,
                    amount = expense.amount,
                    status = expense.status,
                    receiptUrl = expense.receiptUrl,
                    notes = expense.notes,
                    createdAt = expense.createdAt,
                    updatedAt = expense.updatedAt
                )
            }
    }
}
