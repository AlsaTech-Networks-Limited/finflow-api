package com.alsatech.finflow.resource.transaction.infrastructure.service

import com.alsatech.finflow.resource.transaction.domain.service.TransactionValidationService
import com.alsatech.finflow.resource.transaction.dto.CreateTransactionCommand
import com.alsatech.finflow.resource.transaction.dto.ImportTransactionsCommand
import com.alsatech.finflow.resource.transaction.dto.ReconcileTransactionCommand
import com.alsatech.finflow.shared.domain.repository.AccountRepository
import com.alsatech.finflow.shared.domain.repository.TransactionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class TransactionValidationServiceImpl(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : TransactionValidationService {

    override fun validateCreateTransaction(command: CreateTransactionCommand): Mono<CreateTransactionCommand> {
        return Mono.just(command)
            .flatMap { validateAccountExists(it.accountId).thenReturn(it) }
            .flatMap { validateAmount(it.amount).thenReturn(it) }
            .flatMap { validateDescription(it.description).thenReturn(it) }
    }

    override fun validateImportTransactions(command: ImportTransactionsCommand): Mono<ImportTransactionsCommand> {
        return Mono.just(command)
            .flatMap { validateAccountExists(it.accountId).thenReturn(it) }
            .flatMap { validateTransactionCount(it.transactions.size).thenReturn(it) }
            .flatMap { cmd ->
                // Validate each transaction in the import
                cmd.transactions.forEach { txn ->
                    validateAmount(txn.amount).subscribe()
                    validateDescription(txn.description).subscribe()
                }
                Mono.just(cmd)
            }
    }

    override fun validateReconcileTransaction(command: ReconcileTransactionCommand): Mono<ReconcileTransactionCommand> {
        return Mono.just(command)
            .flatMap { validateTransactionExists(it.transactionId).thenReturn(it) }
            .flatMap { validateNotAlreadyReconciled(it.transactionId).thenReturn(it) }
    }

    // =================== Private Validation Helpers ===================

    private fun validateAccountExists(accountId: Long): Mono<Unit> {
        return accountRepository.findById(accountId)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Account with ID $accountId not found")))
            .then(Mono.just(Unit))
    }

    private fun validateAmount(amount: BigDecimal): Mono<Unit> {
        return if (amount <= BigDecimal.ZERO) {
            Mono.error(IllegalArgumentException("Transaction amount must be greater than zero"))
        } else if (amount > BigDecimal("999999999.99")) {
            Mono.error(IllegalArgumentException("Transaction amount exceeds maximum allowed value"))
        } else {
            Mono.just(Unit)
        }
    }

    private fun validateDescription(description: String?): Mono<Unit> {
        return if (description != null && description.length > 1000) {
            Mono.error(IllegalArgumentException("Description must not exceed 1000 characters"))
        } else {
            Mono.just(Unit)
        }
    }

    private fun validateTransactionCount(count: Int): Mono<Unit> {
        return if (count == 0) {
            Mono.error(IllegalArgumentException("Import must contain at least one transaction"))
        } else if (count > 1000) {
            Mono.error(IllegalArgumentException("Cannot import more than 1000 transactions at once"))
        } else {
            Mono.just(Unit)
        }
    }

    private fun validateTransactionExists(transactionId: Long): Mono<Unit> {
        return transactionRepository.findById(transactionId)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Transaction with ID $transactionId not found")))
            .then(Mono.just(Unit))
    }

    private fun validateNotAlreadyReconciled(transactionId: Long): Mono<Unit> {
        return transactionRepository.findById(transactionId)
            .flatMap { transaction ->
                if (transaction.reconciled) {
                    Mono.error(IllegalStateException("Transaction is already reconciled"))
                } else {
                    Mono.just(Unit)
                }
            }
    }
}
