package com.alsatech.finflow.domain.repository

import com.alsatech.finflow.domain.model.Account
import java.util.UUID

interface AccountRepository {
    suspend fun findById(id: UUID): Account?
    suspend fun findByCompanyId(companyId: UUID): List<Account>
    suspend fun save(account: Account): Account
}
