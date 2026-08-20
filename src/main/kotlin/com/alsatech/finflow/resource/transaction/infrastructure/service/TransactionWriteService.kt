package com.alsatech.finflow.resource.transaction.infrastructure.service

import com.alsatech.finflow.resource.transaction.dto.*
import reactor.core.publisher.Mono

/**
 * Infrastructure service for transaction write operations.
 *
 * This service handles persistence of commands.
 * It is called by command handlers AFTER validation.
 *
 * Responsibilities:
 * - Execute business logic (creating transactions, importing bulk, reconciling)
 * - Persist changes to repository
 * - Update account balances
 */
interface TransactionWriteService {
    fun createTransaction(command: CreateTransactionCommand): Mono<TransactionDto>
    fun importTransactions(command: ImportTransactionsCommand): Mono<TransactionImportResponse>
    fun reconcileTransaction(command: ReconcileTransactionCommand): Mono<Unit>
}
