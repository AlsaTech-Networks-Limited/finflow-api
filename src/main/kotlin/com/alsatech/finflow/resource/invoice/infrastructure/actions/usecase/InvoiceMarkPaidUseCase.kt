package com.alsatech.finflow.resource.invoice.infrastructure.actions.usecase

import com.alsatech.finflow.resource.invoice.dto.MarkInvoicePaidCommand
import com.alsatech.finflow.resource.invoice.infrastructure.service.InvoiceWriteService
import com.collicode.common.service.actions.ActionWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InvoiceMarkPaidUseCase(
    private val writeService: InvoiceWriteService
) : ActionWriteService<MarkInvoicePaidCommand, Unit> {

    override fun executeAction(request: MarkInvoicePaidCommand): Mono<Unit> {
        return writeService.markInvoicePaid(request)
    }

    override fun logAction(request: MarkInvoicePaidCommand): Mono<Unit> {
        return Mono.empty()
    }
}
