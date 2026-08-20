package com.alsatech.finflow.resource.transaction.infrastructure.service.impl

import com.alsatech.finflow.resource.transaction.dto.*
import com.alsatech.finflow.resource.transaction.infrastructure.service.TransactionWriteService
import com.alsatech.finflow.shared.domain.entity.Transaction
import com.alsatech.finflow.shared.domain.entity.TransactionType
import com.alsatech.finflow.shared.domain.repository.AccountRepository
import com.alsatech.finflow.shared.domain.repository.TransactionRepository
import com.collicode.common.exception.BusinessException
import com.collicode.common.util.asUnit
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class TransactionWriteServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) : TransactionWriteService {

    override fun createTransaction(command: CreateTransactionCommand): Mono<TransactionDto> {
        val now = LocalDateTime.now()

        val transaction = Transaction(
            id = null,
            accountId = command.accountId,
            type = command.type,
            amount = command.amount,
            description = command.description,
            reference = command.reference,
            occurredAt = command.occurredAt,
            reconciled = false,
            createdAt = now
        )

        return transactionRepository.save(transaction)
            .flatMap { savedTransaction ->
                updateAccountBalance(savedTransaction)
                    .thenReturn(savedTransaction)
            }
            .map { savedTransaction ->
                TransactionDto(
                    id = savedTransaction.id!!,
                    accountId = savedTransaction.accountId,
                    type = savedTransaction.type,
                    amount = savedTransaction.amount,
                    description = savedTransaction.description,
                    reference = savedTransaction.reference,
                    reconciled = savedTransaction.reconciled,
                    occurredAt = savedTransaction.occurredAt,
                    createdAt = savedTransaction.createdAt
                )
            }
    }

    override fun importTransactions(command: ImportTransactionsCommand): Mono<TransactionImportResponse> {
        val now = LocalDateTime.now()

        val transactions = command.transactions.map { item ->
            Transaction(
                id = null,
                accountId = command.accountId,
                type = item.type,
                amount = item.amount,
                description = item.description,
                reference = item.reference,
                occurredAt = item.occurredAt,
                reconciled = false,
                createdAt = now
            )
        }

        return transactionRepository.saveAll(transactions)
            .collectList()
            .flatMap { savedTransactions ->
                // Update account balance for all imported transactions
                updateAccountBalanceForMultiple(command.accountId, savedTransactions)
                    .thenReturn(TransactionImportResponse(
                        imported = savedTransactions.size,
                        failed = 0,
                        errors = emptyList()
                    ))
            }
    }

    override fun reconcileTransaction(command: ReconcileTransactionCommand): Mono<Unit> {
        return transactionRepository.findById(command.transactionId)
            .switchIfEmpty(Mono.error(BusinessException.exception(
                "TRANSACTION_NOT_FOUND",
                "Transaction not found with ID: ${command.transactionId}"
            )))
            .flatMap { transaction ->
                if (transaction.reconciled) {
                    Mono.error(BusinessException.exception(
                        "TRANSACTION_ALREADY_RECONCILED",
                        "Transaction is already reconciled"
                    ))
                } else {
                    val reconciledTransaction = transaction.copy(reconciled = true)
                    transactionRepository.save(reconciledTransaction)
                        .asUnit()
                }
            }
    }

    // =================== Private Helpers ===================

    private fun updateAccountBalance(transaction: Transaction): Mono<Unit> {
        return accountRepository.findById(transaction.accountId)
            .flatMap { account ->
                val newBalance = when (transaction.type) {
                    TransactionType.CREDIT -> account.balance + transaction.amount
                    TransactionType.DEBIT -> account.balance - transaction.amount
                }

                val updatedAccount = account.copy(balance = newBalance)
                accountRepository.save(updatedAccount)
                    .asUnit()
            }
    }

    private fun updateAccountBalanceForMultiple(accountId: Long, transactions: List<Transaction>): Mono<Unit> {
        return accountRepository.findById(accountId)
            .flatMap { account ->
                var newBalance = account.balance

                transactions.forEach { transaction ->
                    newBalance = when (transaction.type) {
                        TransactionType.CREDIT -> newBalance + transaction.amount
                        TransactionType.DEBIT -> newBalance - transaction.amount
                    }
                }

                val updatedAccount = account.copy(balance = newBalance)
                accountRepository.save(updatedAccount)
                    .asUnit()
            }
    }
}
