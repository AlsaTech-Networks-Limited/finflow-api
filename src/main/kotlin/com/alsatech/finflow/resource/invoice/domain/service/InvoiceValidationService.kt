package com.alsatech.finflow.resource.invoice.domain.service

import com.alsatech.finflow.resource.invoice.dto.*
import reactor.core.publisher.Mono

/**
 * Domain service for invoice validation business rules.
 */
interface InvoiceValidationService {
    fun validateCreateInvoice(command: CreateInvoiceCommand): Mono<CreateInvoiceCommand>
    fun validateUpdateInvoice(command: UpdateInvoiceCommand): Mono<UpdateInvoiceCommand>
    fun validateSendInvoice(command: SendInvoiceCommand): Mono<SendInvoiceCommand>
    fun validateMarkInvoicePaid(command: MarkInvoicePaidCommand): Mono<MarkInvoicePaidCommand>
    fun validateCancelInvoice(command: CancelInvoiceCommand): Mono<CancelInvoiceCommand>
}
