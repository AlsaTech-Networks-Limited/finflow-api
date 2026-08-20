package com.alsatech.finflow.resource.invoice.infrastructure.actions

import com.alsatech.finflow.resource.invoice.domain.service.InvoiceValidationService
import com.alsatech.finflow.resource.invoice.dto.CreateInvoiceCommand
import com.alsatech.finflow.resource.invoice.infrastructure.actions.usecase.InvoiceCreateUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InvoiceCreateAction(
    private val useCase: InvoiceCreateUseCase,
    private val validationService: InvoiceValidationService
) : ActionWorkFlowService<CreateInvoiceCommand> {

    override fun validate(request: CreateInvoiceCommand): Mono<CreateInvoiceCommand> {
        return validationService.validateCreateInvoice(request)
    }

    override fun processRequest(request: CreateInvoiceCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
