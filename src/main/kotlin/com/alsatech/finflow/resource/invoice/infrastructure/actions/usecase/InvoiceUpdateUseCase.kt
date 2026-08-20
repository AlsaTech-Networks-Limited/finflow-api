package com.alsatech.finflow.resource.invoice.infrastructure.actions.usecase

import com.alsatech.finflow.resource.invoice.dto.InvoiceDetailDto
import com.alsatech.finflow.resource.invoice.dto.UpdateInvoiceCommand
import com.alsatech.finflow.resource.invoice.infrastructure.service.InvoiceWriteService
import com.collicode.common.service.actions.ActionWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InvoiceUpdateUseCase(
    private val writeService: InvoiceWriteService
) : ActionWriteService<UpdateInvoiceCommand, InvoiceDetailDto> {

    override fun executeAction(request: UpdateInvoiceCommand): Mono<InvoiceDetailDto> {
        return writeService.updateInvoice(request)
    }

    override fun logAction(request: UpdateInvoiceCommand): Mono<InvoiceDetailDto> {
        return Mono.empty()
    }
}
