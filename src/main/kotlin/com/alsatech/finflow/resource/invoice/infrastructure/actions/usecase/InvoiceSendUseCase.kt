package com.alsatech.finflow.resource.invoice.infrastructure.actions.usecase

import com.alsatech.finflow.resource.invoice.dto.SendInvoiceCommand
import com.alsatech.finflow.resource.invoice.infrastructure.service.InvoiceWriteService
import com.collicode.common.service.actions.ActionWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InvoiceSendUseCase(
    private val writeService: InvoiceWriteService
) : ActionWriteService<SendInvoiceCommand, Unit> {

    override fun executeAction(request: SendInvoiceCommand): Mono<Unit> {
        return writeService.sendInvoice(request)
    }

    override fun logAction(request: SendInvoiceCommand): Mono<Unit> {
        return Mono.empty()
    }
}
