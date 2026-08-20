package com.alsatech.finflow.resource.invoice.infrastructure.service

import com.alsatech.finflow.resource.invoice.dto.InvoiceDetailDto
import com.alsatech.finflow.resource.invoice.dto.InvoiceDto
import com.alsatech.finflow.resource.invoice.dto.ListInvoicesQuery
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Read service for invoice queries.
 */
interface InvoiceReadService {
    fun fetchInvoiceById(invoiceId: Long): Mono<InvoiceDetailDto>
    fun fetchInvoicesByCompany(query: ListInvoicesQuery): Flux<InvoiceDto>
}
