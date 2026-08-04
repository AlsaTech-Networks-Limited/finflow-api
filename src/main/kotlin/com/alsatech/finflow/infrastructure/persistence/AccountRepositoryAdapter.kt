package com.alsatech.finflow.infrastructure.persistence

import com.alsatech.finflow.domain.model.Account
import com.alsatech.finflow.domain.repository.AccountRepository
import com.alsatech.finflow.infrastructure.persistence.r2dbc.AccountR2dbcRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class AccountRepositoryAdapter(
    private val accountR2dbcRepository: AccountR2dbcRepository
) : AccountRepository {

    override suspend fun findById(id: UUID): Account? {
        return accountR2dbcRepository.findById(id)
    }

    override suspend fun findByCompanyId(companyId: UUID): List<Account> {
        return accountR2dbcRepository.findByCompanyId(companyId)
    }

    override suspend fun save(account: Account): Account {
        return accountR2dbcRepository.save(account)
    }
}