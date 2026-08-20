package com.alsatech.finflow.shared.domain.repository

import com.alsatech.finflow.shared.domain.entity.Invoice
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository interface for Invoice entity.
 * Defines data access contract owned by the domain.
 */
interface InvoiceRepository {
    fun findById(id: Long): Mono<Invoice>
    fun findByCompanyId(companyId: Long): Flux<Invoice>
    fun findByInvoiceNumber(invoiceNumber: String): Mono<Invoice>
    fun save(invoice: Invoice): Mono<Invoice>
    fun updateStatus(invoiceId: Long, status: com.alsatech.finflow.shared.domain.entity.InvoiceStatus): Mono<Invoice>
    fun deleteById(id: Long): Mono<Unit>
}
