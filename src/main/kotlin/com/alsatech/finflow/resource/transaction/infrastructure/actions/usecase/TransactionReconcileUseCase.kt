package com.alsatech.finflow.resource.transaction.infrastructure.actions.usecase

import com.alsatech.finflow.resource.transaction.dto.ReconcileTransactionCommand
import com.alsatech.finflow.resource.transaction.infrastructure.service.TransactionWriteService
import com.collicode.common.service.actions.ActionWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TransactionReconcileUseCase(
    private val transactionWriteService: TransactionWriteService
) : ActionWriteService<ReconcileTransactionCommand, Unit> {

    override fun executeAction(request: ReconcileTransactionCommand): Mono<Unit> {
        return transactionWriteService.reconcileTransaction(request)
    }

    override fun logAction(request: ReconcileTransactionCommand): Mono<Unit> {
        return Mono.empty()
    }
}
