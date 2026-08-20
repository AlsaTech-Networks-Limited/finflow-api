package com.alsatech.finflow.resource.invoice.infrastructure.actions

import com.alsatech.finflow.resource.invoice.domain.service.InvoiceValidationService
import com.alsatech.finflow.resource.invoice.dto.UpdateInvoiceCommand
import com.alsatech.finflow.resource.invoice.infrastructure.actions.usecase.InvoiceUpdateUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InvoiceUpdateAction(
    private val useCase: InvoiceUpdateUseCase,
    private val validationService: InvoiceValidationService
) : ActionWorkFlowService<UpdateInvoiceCommand> {

    override fun validate(request: UpdateInvoiceCommand): Mono<UpdateInvoiceCommand> {
        return validationService.validateUpdateInvoice(request)
    }

    override fun processRequest(request: UpdateInvoiceCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
