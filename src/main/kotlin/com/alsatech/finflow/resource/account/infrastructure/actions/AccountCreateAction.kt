package com.alsatech.finflow.resource.account.infrastructure.actions

import com.alsatech.finflow.resource.account.domain.service.AccountValidationService
import com.alsatech.finflow.resource.account.dto.CreateAccountCommand
import com.alsatech.finflow.resource.account.infrastructure.actions.usecase.AccountCreateUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AccountCreateAction(
    private val useCase: AccountCreateUseCase,
    private val validationService: AccountValidationService
) : ActionWorkFlowService<CreateAccountCommand> {

    override fun validate(request: CreateAccountCommand): Mono<CreateAccountCommand> {
        return validationService.validateCreateAccount(request)
    }

    override fun processRequest(request: CreateAccountCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
