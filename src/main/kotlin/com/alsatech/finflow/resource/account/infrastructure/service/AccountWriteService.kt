package com.alsatech.finflow.resource.account.infrastructure.service

import com.alsatech.finflow.resource.account.dto.AccountDto
import com.alsatech.finflow.resource.account.dto.CreateAccountCommand
import com.alsatech.finflow.resource.account.dto.UpdateAccountCommand
import reactor.core.publisher.Mono

/**
 * Infrastructure service for account write operations.
 *
 * This service handles persistence of commands.
 * It is called by command handlers AFTER validation.
 *
 * Responsibilities:
 * - Execute business logic (creating accounts, updating balances)
 * - Persist changes to repository
 */
interface AccountWriteService {
    fun createAccount(command: CreateAccountCommand): Mono<AccountDto>
    fun updateAccount(command: UpdateAccountCommand): Mono<AccountDto>
}
