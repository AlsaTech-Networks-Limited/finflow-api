package com.alsatech.finflow.infrastructure.persistence

import com.alsatech.finflow.domain.model.Account
import com.alsatech.finflow.domain.repository.AccountRepository
import com.alsatech.finflow.infrastructure.persistence.r2dbc.AccountR2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
class AccountRepositoryAdapter(
    private val accountR2dbcRepository: AccountR2dbcRepository
) : AccountRepository {

    override fun findById(id: UUID): Mono<Account> {
        return accountR2dbcRepository.findById(id)
    }

    override fun findByCompanyId(companyId: UUID): Flux<Account> {
        return accountR2dbcRepository.findByCompanyId(companyId)
    }

    override fun save(account: Account): Mono<Account> {
        return accountR2dbcRepository.save(account)
    }
}