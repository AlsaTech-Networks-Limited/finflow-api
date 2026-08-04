package com.alsatech.finflow.application.usecase

import com.alsatech.finflow.domain.model.Account
import com.alsatech.finflow.domain.repository.AccountRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListAccountsUseCase(
    private val accountRepository: AccountRepository
) {
    suspend fun execute(companyId: UUID): List<Account> {
        return accountRepository.findByCompanyId(companyId)
    }
}