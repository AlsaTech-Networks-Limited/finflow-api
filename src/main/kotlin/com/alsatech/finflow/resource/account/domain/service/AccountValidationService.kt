package com.alsatech.finflow.resource.account.domain.service

import com.alsatech.finflow.resource.account.dto.CreateAccountCommand
import com.alsatech.finflow.resource.account.dto.UpdateAccountCommand
import reactor.core.publisher.Mono

/**
 * Domain validation service for account-related commands.
 * Handles business rule validation beyond basic field constraints.
 */
interface AccountValidationService {

    /**
     * Validates a create account command.
     * Checks business rules like account name uniqueness, currency validity, etc.
     */
    fun validateCreateAccount(command: CreateAccountCommand): Mono<CreateAccountCommand>

    /**
     * Validates an update account command.
     * Checks business rules like account existence, valid updates, etc.
     */
    fun validateUpdateAccount(command: UpdateAccountCommand): Mono<UpdateAccountCommand>
}
