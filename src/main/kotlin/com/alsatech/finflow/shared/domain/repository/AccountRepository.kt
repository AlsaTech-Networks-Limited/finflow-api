package com.alsatech.finflow.shared.domain.repository

import com.alsatech.finflow.shared.domain.entity.Account
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository interface for Account entity.
 * Defines data access contract owned by the domain.
 */
interface AccountRepository {
    fun findById(id: Long): Mono<Account>
    fun findByCompanyId(companyId: Long): Flux<Account>
    fun save(account: Account): Mono<Account>
}
