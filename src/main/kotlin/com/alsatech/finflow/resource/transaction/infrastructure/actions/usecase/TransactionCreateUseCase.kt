package com.alsatech.finflow.resource.transaction.infrastructure.actions.usecase

import com.alsatech.finflow.resource.transaction.dto.CreateTransactionCommand
import com.alsatech.finflow.resource.transaction.dto.TransactionDto
import com.alsatech.finflow.resource.transaction.infrastructure.service.TransactionWriteService
import com.collicode.common.service.actions.ActionWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TransactionCreateUseCase(
    private val transactionWriteService: TransactionWriteService
) : ActionWriteService<CreateTransactionCommand, TransactionDto> {

    override fun executeAction(request: CreateTransactionCommand): Mono<TransactionDto> {
        return transactionWriteService.createTransaction(request)
    }

    override fun logAction(request: CreateTransactionCommand): Mono<TransactionDto> {
        return Mono.empty()
    }
}
