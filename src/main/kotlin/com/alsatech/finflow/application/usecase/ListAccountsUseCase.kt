package com.alsatech.finflow.application.usecase

import com.alsatech.finflow.domain.model.Account
import com.alsatech.finflow.domain.repository.AccountRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.UUID

@Service
class ListAccountsUseCase(
    private val accountRepository: AccountRepository
) {
    fun execute(companyId: UUID): Flux<Account> {
        return accountRepository.findByCompanyId(companyId)
    }
}