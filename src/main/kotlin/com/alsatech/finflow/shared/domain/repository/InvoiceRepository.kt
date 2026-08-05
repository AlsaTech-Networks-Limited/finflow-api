package com.alsatech.finflow.shared.domain.repository

import com.alsatech.finflow.shared.domain.entity.Invoice
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository interface for Invoice entity.
 * Defines data access contract owned by the domain.
 */
interface InvoiceRepository {
    fun findByCompanyId(companyId: Long): Flux<Invoice>
    fun save(invoice: Invoice): Mono<Invoice>
}
