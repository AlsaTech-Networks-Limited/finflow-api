package com.alsatech.finflow.resource.account.infrastructure.service

import com.alsatech.finflow.resource.account.domain.service.AccountValidationService
import com.alsatech.finflow.resource.account.dto.CreateAccountCommand
import com.alsatech.finflow.resource.account.dto.UpdateAccountCommand
import com.alsatech.finflow.shared.domain.repository.AccountRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class AccountValidationServiceImpl(
    private val accountRepository: AccountRepository
) : AccountValidationService {

    override fun validateCreateAccount(command: CreateAccountCommand): Mono<CreateAccountCommand> {
        return Mono.just(command)
            .flatMap { validateAccountName(it.name).thenReturn(it) }
            .flatMap { validateCurrency(it.currency).thenReturn(it) }
            .flatMap { validateInitialBalance(it.initialBalance).thenReturn(it) }
    }

    override fun validateUpdateAccount(command: UpdateAccountCommand): Mono<UpdateAccountCommand> {
        return Mono.just(command)
            .flatMap { validateAccountExists(it.accountId).thenReturn(it) }
            .flatMap {
                if (it.name != null) validateAccountName(it.name).thenReturn(it)
                else Mono.just(it)
            }
            .flatMap {
                if (it.balance != null) validateBalance(it.balance).thenReturn(it)
                else Mono.just(it)
            }
    }

    // =================== Private Validation Helpers ===================

    private fun validateAccountName(name: String): Mono<Unit> {
        return if (name.isBlank()) {
            Mono.error(IllegalArgumentException("Account name cannot be blank"))
        } else if (name.length > 250) {
            Mono.error(IllegalArgumentException("Account name must not exceed 250 characters"))
        } else {
            Mono.just(Unit)
        }
    }

    private fun validateCurrency(currency: String): Mono<Unit> {
        val validCurrencies = setOf("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "INR")
        return if (currency.length != 3) {
            Mono.error(IllegalArgumentException("Currency must be a 3-letter code"))
        } else if (!validCurrencies.contains(currency.uppercase())) {
            Mono.error(IllegalArgumentException("Currency $currency is not supported"))
        } else {
            Mono.just(Unit)
        }
    }

    private fun validateInitialBalance(balance: BigDecimal): Mono<Unit> {
        return validateBalance(balance)
    }

    private fun validateBalance(balance: BigDecimal): Mono<Unit> {
        return if (balance < BigDecimal("-999999999.99")) {
            Mono.error(IllegalArgumentException("Balance is below minimum allowed value"))
        } else if (balance > BigDecimal("999999999.99")) {
            Mono.error(IllegalArgumentException("Balance exceeds maximum allowed value"))
        } else {
            Mono.just(Unit)
        }
    }

    private fun validateAccountExists(accountId: Long): Mono<Unit> {
        return accountRepository.findById(accountId)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Account with ID $accountId not found")))
            .then(Mono.just(Unit))
    }
}
