package com.alsatech.finflow.shared.domain.repository

import com.alsatech.finflow.shared.domain.entity.InvoiceLineItem
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository interface for InvoiceLineItem entity.
 * Defines data access contract owned by the domain.
 */
interface InvoiceLineItemRepository {
    fun findById(id: Long): Mono<InvoiceLineItem>
    fun findByInvoiceId(invoiceId: Long): Flux<InvoiceLineItem>
    fun save(lineItem: InvoiceLineItem): Mono<InvoiceLineItem>
    fun saveAll(lineItems: List<InvoiceLineItem>): Flux<InvoiceLineItem>
    fun deleteByInvoiceId(invoiceId: Long): Mono<Unit>
    fun deleteById(id: Long): Mono<Unit>
}
