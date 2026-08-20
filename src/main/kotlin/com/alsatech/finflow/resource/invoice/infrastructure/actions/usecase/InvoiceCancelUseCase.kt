package com.alsatech.finflow.resource.invoice.infrastructure.actions.usecase

import com.alsatech.finflow.resource.invoice.dto.CancelInvoiceCommand
import com.alsatech.finflow.resource.invoice.infrastructure.service.InvoiceWriteService
import com.collicode.common.service.actions.ActionWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InvoiceCancelUseCase(
    private val writeService: InvoiceWriteService
) : ActionWriteService<CancelInvoiceCommand, Unit> {

    override fun executeAction(request: CancelInvoiceCommand): Mono<Unit> {
        return writeService.cancelInvoice(request)
    }

    override fun logAction(request: CancelInvoiceCommand): Mono<Unit> {
        return Mono.empty()
    }
}
