package com.alsatech.finflow.resource.account.infrastructure.actions.usecase

import com.alsatech.finflow.resource.account.dto.AccountDto
import com.alsatech.finflow.resource.account.dto.CreateAccountCommand
import com.alsatech.finflow.resource.account.infrastructure.service.AccountWriteService
import com.collicode.common.service.actions.ActionWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AccountCreateUseCase(
    private val accountWriteService: AccountWriteService
) : ActionWriteService<CreateAccountCommand, AccountDto> {

    override fun executeAction(request: CreateAccountCommand): Mono<AccountDto> {
        return accountWriteService.createAccount(request)
    }

    override fun logAction(request: CreateAccountCommand): Mono<AccountDto> {
        return Mono.empty()
    }
}
