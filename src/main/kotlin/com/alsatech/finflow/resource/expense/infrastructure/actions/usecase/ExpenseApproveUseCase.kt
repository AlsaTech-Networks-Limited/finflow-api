package com.alsatech.finflow.resource.expense.infrastructure.actions.usecase

import com.alsatech.finflow.resource.expense.dto.ApproveExpenseCommand
import com.alsatech.finflow.resource.expense.infrastructure.service.ExpenseWriteService
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import com.collicode.common.service.actions.ActionWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ExpenseApproveUseCase(
    private val expenseWriteService: ExpenseWriteService
) : ActionWriteService<ApproveExpenseCommand, Unit>{
    override fun executeAction(request: ApproveExpenseCommand): Mono<Unit> {
        return expenseWriteService.approveExpense(command = request)
    }


    override fun logAction(request: ApproveExpenseCommand): Mono<Unit> {
        return Mono.empty()
    }



}
