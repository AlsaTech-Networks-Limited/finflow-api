package com.alsatech.finflow.resource.invoice.infrastructure.actions

import com.alsatech.finflow.resource.invoice.domain.service.InvoiceValidationService
import com.alsatech.finflow.resource.invoice.dto.MarkInvoicePaidCommand
import com.alsatech.finflow.resource.invoice.infrastructure.actions.usecase.InvoiceMarkPaidUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InvoiceMarkPaidAction(
    private val useCase: InvoiceMarkPaidUseCase,
    private val validationService: InvoiceValidationService
) : ActionWorkFlowService<MarkInvoicePaidCommand> {

    override fun validate(request: MarkInvoicePaidCommand): Mono<MarkInvoicePaidCommand> {
        return validationService.validateMarkInvoicePaid(request)
    }

    override fun processRequest(request: MarkInvoicePaidCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
