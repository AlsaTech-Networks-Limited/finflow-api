package com.alsatech.finflow.resource.transaction.infrastructure.actions

import com.alsatech.finflow.resource.transaction.domain.service.TransactionValidationService
import com.alsatech.finflow.resource.transaction.dto.ImportTransactionsCommand
import com.alsatech.finflow.resource.transaction.infrastructure.actions.usecase.TransactionImportUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TransactionImportAction(
    private val useCase: TransactionImportUseCase,
    private val validationService: TransactionValidationService
) : ActionWorkFlowService<ImportTransactionsCommand> {

    override fun validate(request: ImportTransactionsCommand): Mono<ImportTransactionsCommand> {
        return validationService.validateImportTransactions(request)
    }

    override fun processRequest(request: ImportTransactionsCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
