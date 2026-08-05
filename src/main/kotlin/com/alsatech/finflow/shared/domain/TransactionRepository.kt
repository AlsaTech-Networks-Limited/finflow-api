package com.alsatech.finflow.shared.domain

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TransactionRepository {
    fun findByAccountId(accountId: Long): Flux<Transaction>
    fun save(transaction: Transaction): Mono<Transaction>
    fun saveAll(transactions: List<Transaction>): Flux<Transaction>
}
