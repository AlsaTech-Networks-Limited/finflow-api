package com.alsatech.finflow.resource.expense.infrastructure.actions

import com.alsatech.finflow.resource.expense.domain.service.ExpenseValidationService
import com.alsatech.finflow.resource.expense.dto.SubmitExpenseCommand
import com.alsatech.finflow.resource.expense.infrastructure.actions.usecase.ExpenseSubmitUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ExpenseSubmitAction(
    private val useCase: ExpenseSubmitUseCase,
    private val validationService: ExpenseValidationService
) : ActionWorkFlowService<SubmitExpenseCommand> {
    override fun validate(request: SubmitExpenseCommand): Mono<SubmitExpenseCommand> {
        return validationService.validateSubmitExpense(request)
    }

    override fun processRequest(request: SubmitExpenseCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
