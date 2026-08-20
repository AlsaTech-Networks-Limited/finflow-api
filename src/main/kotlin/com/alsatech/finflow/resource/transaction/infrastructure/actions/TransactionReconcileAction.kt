package com.alsatech.finflow.resource.transaction.infrastructure.actions

import com.alsatech.finflow.resource.transaction.domain.service.TransactionValidationService
import com.alsatech.finflow.resource.transaction.dto.ReconcileTransactionCommand
import com.alsatech.finflow.resource.transaction.infrastructure.actions.usecase.TransactionReconcileUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TransactionReconcileAction(
    private val useCase: TransactionReconcileUseCase,
    private val validationService: TransactionValidationService
) : ActionWorkFlowService<ReconcileTransactionCommand> {

    override fun validate(request: ReconcileTransactionCommand): Mono<ReconcileTransactionCommand> {
        return validationService.validateReconcileTransaction(request)
    }

    override fun processRequest(request: ReconcileTransactionCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
