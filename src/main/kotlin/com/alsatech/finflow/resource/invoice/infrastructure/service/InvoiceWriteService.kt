package com.alsatech.finflow.resource.invoice.infrastructure.service

import com.alsatech.finflow.resource.invoice.dto.*
import reactor.core.publisher.Mono

/**
 * Write service for invoice mutations.
 */
interface InvoiceWriteService {
    fun createInvoice(command: CreateInvoiceCommand): Mono<InvoiceDetailDto>
    fun updateInvoice(command: UpdateInvoiceCommand): Mono<InvoiceDetailDto>
    fun sendInvoice(command: SendInvoiceCommand): Mono<Unit>
    fun markInvoicePaid(command: MarkInvoicePaidCommand): Mono<Unit>
    fun cancelInvoice(command: CancelInvoiceCommand): Mono<Unit>
}
