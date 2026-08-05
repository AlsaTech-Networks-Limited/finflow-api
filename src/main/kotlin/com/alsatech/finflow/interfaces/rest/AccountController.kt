package com.alsatech.finflow.interfaces.rest

import com.alsatech.finflow.application.mapper.AccountMapper
import com.alsatech.finflow.application.usecase.CreateAccountUseCase
import com.alsatech.finflow.application.usecase.ListAccountsUseCase
import com.alsatech.finflow.interfaces.dto.AccountResponse
import com.alsatech.finflow.interfaces.dto.CreateAccountRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val createAccountUseCase: CreateAccountUseCase,
    private val listAccountsUseCase: ListAccountsUseCase
) {

    @GetMapping
    fun listAccounts(
        @RequestParam companyId: UUID
    ): Flux<AccountResponse> {
        return listAccountsUseCase.execute(companyId)
            .map { AccountMapper.toResponse(it) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAccount(
        @RequestParam companyId: UUID,
        @Valid @RequestBody request: CreateAccountRequest
    ): Mono<AccountResponse> {
        return createAccountUseCase.execute(
            companyId = companyId,
            name = request.name,
            currency = request.currency,
            balance = request.balance,
            type = request.type
        ).map { AccountMapper.toResponse(it) }
    }
}
