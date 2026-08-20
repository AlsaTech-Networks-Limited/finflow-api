package com.alsatech.finflow.resource.transaction.infrastructure.service

import com.alsatech.finflow.resource.transaction.dto.TransactionDetailDto
import com.alsatech.finflow.resource.transaction.dto.TransactionDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Infrastructure service for transaction read operations.
 *
 * This service handles data retrieval for queries.
 * It is called by query handlers.
 *
 * Responsibilities:
 * - Fetch data from repositories
 * - Map domain entities to DTOs
 * - Aggregate data from multiple sources (e.g., transaction + account)
 *
 * Follows CQRS pattern - read operations only (no state changes).
 */
interface TransactionReadService {
    fun fetchTransactionById(transactionId: Long): Mono<TransactionDetailDto>
    fun fetchTransactionsByAccount(accountId: Long): Flux<TransactionDto>
    fun fetchTransactionsByCompany(companyId: Long): Flux<TransactionDto>
}
