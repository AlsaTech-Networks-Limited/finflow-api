package com.alsatech.finflow.resource.expense.infrastructure.actions

import com.alsatech.finflow.resource.expense.domain.service.ExpenseValidationService
import com.alsatech.finflow.resource.expense.dto.RejectExpenseCommand
import com.alsatech.finflow.resource.expense.infrastructure.actions.usecase.ExpenseRejectUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ExpenseRejectAction(
    private val useCase: ExpenseRejectUseCase,
    private val validationService: ExpenseValidationService
) : ActionWorkFlowService<RejectExpenseCommand> {
    override fun validate(request: RejectExpenseCommand): Mono<RejectExpenseCommand> {
        return validationService.validateRejectExpense(request)
    }

    override fun processRequest(request: RejectExpenseCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
