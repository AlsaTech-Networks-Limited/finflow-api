package com.alsatech.finflow.resource.expense.infrastructure.actions

import com.alsatech.finflow.resource.expense.domain.service.ExpenseValidationService
import com.alsatech.finflow.resource.expense.dto.ApproveExpenseCommand
import com.alsatech.finflow.resource.expense.infrastructure.actions.usecase.ExpenseApproveUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ExpenseApproveAction(
    private val useCase: ExpenseApproveUseCase,
    private val validationService: ExpenseValidationService
) : ActionWorkFlowService<ApproveExpenseCommand> {
    override fun validate(request: ApproveExpenseCommand): Mono<ApproveExpenseCommand> {
        return validationService.validateApproveExpense(request)
    }

    override fun processRequest(request: ApproveExpenseCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )

    }


}
