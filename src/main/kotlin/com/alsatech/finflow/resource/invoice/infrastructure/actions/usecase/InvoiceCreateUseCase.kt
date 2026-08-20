package com.alsatech.finflow.resource.invoice.infrastructure.actions.usecase

import com.alsatech.finflow.resource.invoice.dto.CreateInvoiceCommand
import com.alsatech.finflow.resource.invoice.dto.InvoiceDetailDto
import com.alsatech.finflow.resource.invoice.infrastructure.service.InvoiceWriteService
import com.collicode.common.service.actions.ActionWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InvoiceCreateUseCase(
    private val writeService: InvoiceWriteService
) : ActionWriteService<CreateInvoiceCommand, InvoiceDetailDto> {

    override fun executeAction(request: CreateInvoiceCommand): Mono<InvoiceDetailDto> {
        return writeService.createInvoice(request)
    }

    override fun logAction(request: CreateInvoiceCommand): Mono<InvoiceDetailDto> {
        return Mono.empty()
    }
}
