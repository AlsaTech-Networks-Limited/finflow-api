package com.alsatech.finflow.resource.transaction.domain.service

import com.alsatech.finflow.resource.transaction.dto.CreateTransactionCommand
import com.alsatech.finflow.resource.transaction.dto.ImportTransactionsCommand
import com.alsatech.finflow.resource.transaction.dto.ReconcileTransactionCommand
import reactor.core.publisher.Mono

/**
 * Domain validation service for transaction-related commands.
 * Handles business rule validation beyond basic field constraints.
 */
interface TransactionValidationService {

    /**
     * Validates a create transaction command.
     * Checks business rules like account existence, amount validity, etc.
     */
    fun validateCreateTransaction(command: CreateTransactionCommand): Mono<CreateTransactionCommand>

    /**
     * Validates an import transactions command.
     * Checks business rules like account existence, transaction limits, etc.
     */
    fun validateImportTransactions(command: ImportTransactionsCommand): Mono<ImportTransactionsCommand>

    /**
     * Validates a reconcile transaction command.
     * Checks business rules like transaction existence, not already reconciled, etc.
     */
    fun validateReconcileTransaction(command: ReconcileTransactionCommand): Mono<ReconcileTransactionCommand>
}
