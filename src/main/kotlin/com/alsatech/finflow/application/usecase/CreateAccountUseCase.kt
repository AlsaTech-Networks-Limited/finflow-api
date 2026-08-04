package com.alsatech.finflow.application.usecase

import com.alsatech.finflow.domain.model.Account
import com.alsatech.finflow.domain.repository.AccountRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.UUID

@Service
class CreateAccountUseCase(
    private val accountRepository: AccountRepository
) {
    fun execute(
        companyId: UUID,
        name: String,
        currency: String,
        balance: BigDecimal,
        type: String
    ): Mono<Account> {
        val account = Account(
            id = null,
            companyId = companyId,
            name = name,
            currency = currency,
            balance = balance,
            type = type
        )
        return accountRepository.save(account)
    }
}