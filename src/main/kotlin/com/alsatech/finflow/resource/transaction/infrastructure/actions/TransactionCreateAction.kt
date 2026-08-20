package com.alsatech.finflow.resource.transaction.infrastructure.actions

import com.alsatech.finflow.resource.transaction.domain.service.TransactionValidationService
import com.alsatech.finflow.resource.transaction.dto.CreateTransactionCommand
import com.alsatech.finflow.resource.transaction.infrastructure.actions.usecase.TransactionCreateUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TransactionCreateAction(
    private val useCase: TransactionCreateUseCase,
    private val validationService: TransactionValidationService
) : ActionWorkFlowService<CreateTransactionCommand> {

    override fun validate(request: CreateTransactionCommand): Mono<CreateTransactionCommand> {
        return validationService.validateCreateTransaction(request)
    }

    override fun processRequest(request: CreateTransactionCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
