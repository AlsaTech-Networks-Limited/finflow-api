package com.alsatech.finflow.resource.transaction.infrastructure.actions.usecase

import com.alsatech.finflow.resource.transaction.dto.ImportTransactionsCommand
import com.alsatech.finflow.resource.transaction.dto.TransactionImportResponse
import com.alsatech.finflow.resource.transaction.infrastructure.service.TransactionWriteService
import com.collicode.common.service.actions.ActionWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TransactionImportUseCase(
    private val transactionWriteService: TransactionWriteService
) : ActionWriteService<ImportTransactionsCommand, TransactionImportResponse> {

    override fun executeAction(request: ImportTransactionsCommand): Mono<TransactionImportResponse> {
        return transactionWriteService.importTransactions(request)
    }

    override fun logAction(request: ImportTransactionsCommand): Mono<TransactionImportResponse> {
        return Mono.empty()
    }
}
