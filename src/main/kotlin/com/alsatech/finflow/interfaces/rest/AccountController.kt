package com.alsatech.finflow.interfaces.rest

import com.alsatech.finflow.application.mapper.AccountMapper
import com.alsatech.finflow.application.usecase.CreateAccountUseCase
import com.alsatech.finflow.application.usecase.ListAccountsUseCase
import com.alsatech.finflow.interfaces.dto.AccountResponse
import com.alsatech.finflow.interfaces.dto.CreateAccountRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val createAccountUseCase: CreateAccountUseCase,
    private val listAccountsUseCase: ListAccountsUseCase
) {

    @GetMapping
    suspend fun listAccounts(
        @RequestParam companyId: UUID
    ): List<AccountResponse> {
        val accounts = listAccountsUseCase.execute(companyId)
        return accounts.map { AccountMapper.toResponse(it) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createAccount(
        @RequestParam companyId: UUID,
        @Valid @RequestBody request: CreateAccountRequest
    ): AccountResponse {
        val account = createAccountUseCase.execute(
            companyId = companyId,
            name = request.name,
            currency = request.currency,
            balance = request.balance,
            type = request.type
        )
        return AccountMapper.toResponse(account)
    }
}
