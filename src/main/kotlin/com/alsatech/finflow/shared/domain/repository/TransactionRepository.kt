package com.alsatech.finflow.shared.domain.repository

import com.alsatech.finflow.shared.domain.entity.Transaction
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository interface for Transaction entity.
 * Defines data access contract owned by the domain.
 */
interface TransactionRepository {
    fun findById(id: Long): Mono<Transaction>
    fun findByAccountId(accountId: Long): Flux<Transaction>
    fun findByCompanyId(companyId: Long): Flux<Transaction>
    fun save(transaction: Transaction): Mono<Transaction>
    fun saveAll(transactions: List<Transaction>): Flux<Transaction>
}
