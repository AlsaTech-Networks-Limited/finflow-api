package com.alsatech.finflow.resource.expense.infrastructure.actions.usecase

import com.alsatech.finflow.resource.expense.dto.ExpenseDto
import com.alsatech.finflow.resource.expense.dto.SubmitExpenseCommand
import com.alsatech.finflow.resource.expense.infrastructure.service.ExpenseWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ExpenseSubmitUseCase(
    private val expenseWriteService: ExpenseWriteService
) {
    fun executeAction(command: SubmitExpenseCommand): Mono<ExpenseDto> {
        return expenseWriteService.submitExpense(command)
    }

    fun logForPendingApproval(command: SubmitExpenseCommand): Mono<Unit> {
        return Mono.empty()
    }
}
