package com.alsatech.finflow.resource.transaction.infrastructure.service.impl

import com.alsatech.finflow.resource.transaction.dto.TransactionDetailDto
import com.alsatech.finflow.resource.transaction.dto.TransactionDto
import com.alsatech.finflow.resource.transaction.infrastructure.service.TransactionReadService
import com.alsatech.finflow.shared.domain.repository.AccountRepository
import com.alsatech.finflow.shared.domain.repository.TransactionRepository
import com.collicode.common.exception.BusinessException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TransactionReadServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) : TransactionReadService {

    override fun fetchTransactionById(transactionId: Long): Mono<TransactionDetailDto> {
        return transactionRepository.findById(transactionId)
            .switchIfEmpty(Mono.error(BusinessException.exception(
                "TRANSACTION_NOT_FOUND",
                "Transaction not found with ID: $transactionId"
            )))
            .flatMap { transaction ->
                accountRepository.findById(transaction.accountId)
                    .map { account ->
                        TransactionDetailDto(
                            id = transaction.id!!,
                            accountId = transaction.accountId,
                            accountName = account.name,
                            type = transaction.type,
                            amount = transaction.amount,
                            description = transaction.description,
                            reference = transaction.reference,
                            reconciled = transaction.reconciled,
                            linkedExpenseId = null, // TODO: Implement expense linking
                            occurredAt = transaction.occurredAt,
                            createdAt = transaction.createdAt
                        )
                    }
            }
    }

    override fun fetchTransactionsByAccount(accountId: Long): Flux<TransactionDto> {
        return transactionRepository.findByAccountId(accountId)
            .map { transaction ->
                TransactionDto(
                    id = transaction.id!!,
                    accountId = transaction.accountId,
                    type = transaction.type,
                    amount = transaction.amount,
                    description = transaction.description,
                    reference = transaction.reference,
                    reconciled = transaction.reconciled,
                    occurredAt = transaction.occurredAt,
                    createdAt = transaction.createdAt
                )
            }
    }

    override fun fetchTransactionsByCompany(companyId: Long): Flux<TransactionDto> {
        return transactionRepository.findByCompanyId(companyId)
            .map { transaction ->
                TransactionDto(
                    id = transaction.id!!,
                    accountId = transaction.accountId,
                    type = transaction.type,
                    amount = transaction.amount,
                    description = transaction.description,
                    reference = transaction.reference,
                    reconciled = transaction.reconciled,
                    occurredAt = transaction.occurredAt,
                    createdAt = transaction.createdAt
                )
            }
    }
}
