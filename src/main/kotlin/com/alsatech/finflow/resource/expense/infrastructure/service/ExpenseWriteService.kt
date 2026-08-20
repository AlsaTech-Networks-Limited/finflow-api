package com.alsatech.finflow.resource.expense.infrastructure.service

import com.alsatech.finflow.resource.expense.dto.ApproveExpenseCommand
import com.alsatech.finflow.resource.expense.dto.ExpenseDto
import com.alsatech.finflow.resource.expense.dto.RejectExpenseCommand
import com.alsatech.finflow.resource.expense.dto.SubmitExpenseCommand
import reactor.core.publisher.Mono

/**
 * Infrastructure service for expense write operations.
 *
 * This service handles persistence of commands.
 * It is called by command handlers AFTER validation.
 *
 * Responsibilities:
 * - Execute business logic (state transitions, generating IDs, creating aggregates)
 * - Persist changes to repository
 * - Additional validation that requires database access (expense exists, status checks, etc.)
 */
interface ExpenseWriteService {
    fun submitExpense(command: SubmitExpenseCommand): Mono<ExpenseDto>
    fun approveExpense(command: ApproveExpenseCommand): Mono<Unit>
    fun rejectExpense(command: RejectExpenseCommand): Mono<Unit>
}
