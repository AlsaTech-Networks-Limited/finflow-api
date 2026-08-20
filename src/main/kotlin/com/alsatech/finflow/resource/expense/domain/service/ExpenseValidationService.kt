package com.alsatech.finflow.resource.expense.domain.service

import com.alsatech.finflow.resource.expense.dto.ApproveExpenseCommand
import com.alsatech.finflow.resource.expense.dto.RejectExpenseCommand
import com.alsatech.finflow.resource.expense.dto.SubmitExpenseCommand
import reactor.core.publisher.Mono

/**
 * Domain validation service for expense-related commands.
 * Handles business rule validation beyond basic field constraints.
 */
interface ExpenseValidationService {

    /**
     * Validates a submit expense command.
     * Checks business rules like category existence, user permissions, etc.
     */
    fun validateSubmitExpense(command: SubmitExpenseCommand): Mono<SubmitExpenseCommand>

    /**
     * Validates an approve expense command.
     * Checks business rules like expense existence, current status, approver permissions, etc.
     */
    fun validateApproveExpense(command: ApproveExpenseCommand): Mono<ApproveExpenseCommand>

    /**
     * Validates a reject expense command.
     * Checks business rules like expense existence, current status, approver permissions, etc.
     */
    fun validateRejectExpense(command: RejectExpenseCommand): Mono<RejectExpenseCommand>
}
