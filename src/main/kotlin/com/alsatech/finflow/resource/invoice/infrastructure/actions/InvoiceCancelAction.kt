package com.alsatech.finflow.resource.invoice.infrastructure.actions

import com.alsatech.finflow.resource.invoice.domain.service.InvoiceValidationService
import com.alsatech.finflow.resource.invoice.dto.CancelInvoiceCommand
import com.alsatech.finflow.resource.invoice.infrastructure.actions.usecase.InvoiceCancelUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InvoiceCancelAction(
    private val useCase: InvoiceCancelUseCase,
    private val validationService: InvoiceValidationService
) : ActionWorkFlowService<CancelInvoiceCommand> {

    override fun validate(request: CancelInvoiceCommand): Mono<CancelInvoiceCommand> {
        return validationService.validateCancelInvoice(request)
    }

    override fun processRequest(request: CancelInvoiceCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
