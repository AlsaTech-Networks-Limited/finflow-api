package com.alsatech.finflow.resource.invoice.infrastructure.actions

import com.alsatech.finflow.resource.invoice.domain.service.InvoiceValidationService
import com.alsatech.finflow.resource.invoice.dto.SendInvoiceCommand
import com.alsatech.finflow.resource.invoice.infrastructure.actions.usecase.InvoiceSendUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InvoiceSendAction(
    private val useCase: InvoiceSendUseCase,
    private val validationService: InvoiceValidationService
) : ActionWorkFlowService<SendInvoiceCommand> {

    override fun validate(request: SendInvoiceCommand): Mono<SendInvoiceCommand> {
        return validationService.validateSendInvoice(request)
    }

    override fun processRequest(request: SendInvoiceCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
