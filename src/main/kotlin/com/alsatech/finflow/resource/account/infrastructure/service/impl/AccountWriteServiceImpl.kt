package com.alsatech.finflow.resource.account.infrastructure.service.impl

import com.alsatech.finflow.resource.account.dto.AccountDto
import com.alsatech.finflow.resource.account.dto.CreateAccountCommand
import com.alsatech.finflow.resource.account.dto.UpdateAccountCommand
import com.alsatech.finflow.resource.account.infrastructure.service.AccountWriteService
import com.alsatech.finflow.shared.domain.entity.Account
import com.alsatech.finflow.shared.domain.repository.AccountRepository
import com.collicode.common.exception.BusinessException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class AccountWriteServiceImpl(
    private val accountRepository: AccountRepository
) : AccountWriteService {

    override fun createAccount(command: CreateAccountCommand): Mono<AccountDto> {
        val now = LocalDateTime.now()

        val account = Account(
            id = null,
            companyId = command.companyId,
            name = command.name,
            type = command.type,
            balance = command.initialBalance,
            currency = command.currency,
            createdAt = now
        )

        return accountRepository.save(account)
            .map { savedAccount ->
                AccountDto(
                    id = savedAccount.id!!,
                    companyId = savedAccount.companyId,
                    name = savedAccount.name,
                    type = savedAccount.type,
                    balance = savedAccount.balance,
                    currency = savedAccount.currency,
                    createdAt = savedAccount.createdAt
                )
            }
    }

    override fun updateAccount(command: UpdateAccountCommand): Mono<AccountDto> {
        return accountRepository.findById(command.accountId)
            .switchIfEmpty(Mono.error(BusinessException.exception(
                "ACCOUNT_NOT_FOUND",
                "Account not found with ID: ${command.accountId}"
            )))
            .flatMap { account ->
                val updatedAccount = account.copy(
                    name = command.name ?: account.name,
                    balance = command.balance ?: account.balance
                )

                accountRepository.save(updatedAccount)
                    .map { savedAccount ->
                        AccountDto(
                            id = savedAccount.id!!,
                            companyId = savedAccount.companyId,
                            name = savedAccount.name,
                            type = savedAccount.type,
                            balance = savedAccount.balance,
                            currency = savedAccount.currency,
                            createdAt = savedAccount.createdAt
                        )
                    }
            }
    }
}
