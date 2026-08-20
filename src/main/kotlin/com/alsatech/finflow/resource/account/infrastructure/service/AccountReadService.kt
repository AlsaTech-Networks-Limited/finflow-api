package com.alsatech.finflow.resource.account.infrastructure.service

import com.alsatech.finflow.resource.account.dto.AccountDetailDto
import com.alsatech.finflow.resource.account.dto.AccountDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Infrastructure service for account read operations.
 *
 * This service handles data retrieval for queries.
 * It is called by query handlers.
 *
 * Responsibilities:
 * - Fetch data from repositories
 * - Map domain entities to DTOs
 * - Aggregate data from multiple sources (e.g., account + transactions)
 *
 * Follows CQRS pattern - read operations only (no state changes).
 */
interface AccountReadService {
    fun fetchAccountById(accountId: Long): Mono<AccountDetailDto>
    fun fetchAccountsByCompany(companyId: Long): Flux<AccountDto>
}
