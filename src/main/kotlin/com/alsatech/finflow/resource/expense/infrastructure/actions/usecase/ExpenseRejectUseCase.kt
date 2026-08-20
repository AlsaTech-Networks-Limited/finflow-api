package com.alsatech.finflow.resource.expense.infrastructure.actions.usecase

import com.alsatech.finflow.resource.expense.dto.RejectExpenseCommand
import com.alsatech.finflow.resource.expense.infrastructure.service.ExpenseWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ExpenseRejectUseCase(
    private val expenseWriteService: ExpenseWriteService
) {
    fun executeAction(command: RejectExpenseCommand): Mono<Unit> {
        return expenseWriteService.rejectExpense(command)
    }

    fun logForPendingApproval(command: RejectExpenseCommand): Mono<Unit> {
        return Mono.empty()
    }
}
