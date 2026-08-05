package com.alsatech.finflow.domain.repository

import com.alsatech.finflow.domain.model.Account
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface AccountRepository {
    suspend fun findById(id: UUID): Mono<Account>
    suspend fun findByCompanyId(companyId: UUID): Flux<Account>
    suspend fun save(account: Account): Mono<Account>
}
