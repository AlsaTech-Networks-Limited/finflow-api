package com.alsatech.finflow.shared.domain

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AccountRepository {
    fun findById(id: Long): Mono<Account>
    fun findByCompanyId(companyId: Long): Flux<Account>
    fun save(account: Account): Mono<Account>
}
