package com.alsatech.finflow.resource.expense.infrastructure.service.impl

import com.alsatech.finflow.shared.infrastructure.id.IdGenerator
import com.alsatech.finflow.shared.infrastructure.id.EntityType
import com.alsatech.finflow.resource.expense.domain.entity.Approval
import com.alsatech.finflow.resource.expense.domain.entity.Expense
import com.alsatech.finflow.resource.expense.domain.entity.ExpenseStatus
import com.alsatech.finflow.resource.expense.domain.repository.ApprovalRepository
import com.alsatech.finflow.resource.expense.domain.repository.ExpenseRepository
import com.alsatech.finflow.resource.expense.dto.ApproveExpenseCommand
import com.alsatech.finflow.resource.expense.dto.ExpenseDto
import com.alsatech.finflow.resource.expense.dto.RejectExpenseCommand
import com.alsatech.finflow.resource.expense.dto.SubmitExpenseCommand
import com.alsatech.finflow.resource.expense.infrastructure.service.ExpenseWriteService
import com.collicode.common.exception.BusinessException
import com.collicode.common.util.asUnit
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class ExpenseWriteServiceImpl(
    private val expenseRepository: ExpenseRepository,
    private val approvalRepository: ApprovalRepository,
    private val idGenerator: IdGenerator
) : ExpenseWriteService {

    override fun submitExpense(command: SubmitExpenseCommand): Mono<ExpenseDto> {
        val expenseId = idGenerator.generateId(EntityType.EXPENSE)
        val now = LocalDateTime.now()

        val expense = Expense(
            expenseId = expenseId,
            userId = command.userId,
            categoryId = command.categoryId,
            amount = command.amount,
            status = ExpenseStatus.PENDING,
            receiptUrl = command.receiptUrl,
            notes = command.notes,
            createdAt = now,
            updatedAt = now
        )

        return expenseRepository.save(expense)
            .map { savedExpense ->
                ExpenseDto(
                    expenseId = savedExpense.expenseId,
                    userId = savedExpense.userId,
                    categoryId = savedExpense.categoryId,
                    amount = savedExpense.amount,
                    status = savedExpense.status,
                    receiptUrl = savedExpense.receiptUrl,
                    notes = savedExpense.notes,
                    createdAt = savedExpense.createdAt,
                    updatedAt = savedExpense.updatedAt
                )
            }
    }

    override fun approveExpense(command: ApproveExpenseCommand): Mono<Unit> {
        return expenseRepository.findById(command.expenseId)
            .switchIfEmpty(Mono.error(BusinessException.exception(
                "EXPENSE_NOT_FOUND",
                "Expense not found with ID: ${command.expenseId}"
            )))
            .flatMap { expense ->
                when {
                    expense.status != ExpenseStatus.PENDING ->
                        Mono.error(BusinessException.exception(
                            "EXPENSE_NOT_PENDING",
                            "Expense is not in PENDING status"
                        ))

                    expense.userId == command.approverId ->
                        Mono.error(BusinessException.exception(
                            "SELF_APPROVAL_NOT_ALLOWED",
                            "Users cannot approve their own expenses"
                        ))

                    else -> {
                        val approvalId = idGenerator.generateId(EntityType.APPROVAL)
                        val now = LocalDateTime.now()

                        val updatedExpense = expense.copy(
                            status = ExpenseStatus.APPROVED,
                            updatedAt = now
                        )
                        expenseRepository.save(updatedExpense)
                            .flatMap { savedExpense ->
                                val approval = Approval(
                                    approvalId = approvalId,
                                    expenseId = savedExpense.expenseId,
                                    approverId = command.approverId,
                                    decision = "APPROVED",
                                    comment = command.comment,
                                    decidedAt = now,
                                    createdAt = now,
                                    updatedAt = now
                                )
                                approvalRepository.save(approval)
                            }
                            .asUnit()
                    }
                }
            }
    }

    override fun rejectExpense(command: RejectExpenseCommand): Mono<Unit> {
        return expenseRepository.findById(command.expenseId)
            .switchIfEmpty(Mono.error(BusinessException.exception(
                "EXPENSE_NOT_FOUND",
                "Expense not found with ID: ${command.expenseId}"
            )))
            .flatMap { expense ->
                when {
                    expense.status != ExpenseStatus.PENDING ->
                        Mono.error(BusinessException.exception(
                            "EXPENSE_NOT_PENDING",
                            "Expense is not in PENDING status"
                        ))

                    expense.userId == command.approverId ->
                        Mono.error(BusinessException.exception(
                            "SELF_APPROVAL_NOT_ALLOWED",
                            "Users cannot approve their own expenses"
                        ))

                    else -> {
                        val approvalId = idGenerator.generateId(EntityType.APPROVAL)
                        val now = LocalDateTime.now()

                        val updatedExpense = expense.copy(
                            status = ExpenseStatus.REJECTED,
                            updatedAt = now
                        )
                        expenseRepository.save(updatedExpense)
                            .flatMap { savedExpense ->
                                val approval = Approval(
                                    approvalId = approvalId,
                                    expenseId = savedExpense.expenseId,
                                    approverId = command.approverId,
                                    decision = "REJECTED",
                                    comment = command.comment,
                                    decidedAt = now,
                                    createdAt = now,
                                    updatedAt = now
                                )
                                approvalRepository.save(approval)
                            }
                            .asUnit()
                    }
                }
            }
    }
}
