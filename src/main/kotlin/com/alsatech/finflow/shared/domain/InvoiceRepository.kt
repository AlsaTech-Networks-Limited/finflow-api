package com.alsatech.finflow.shared.domain

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface InvoiceRepository {
    fun findByCompanyId(companyId: Long): Flux<Invoice>
    fun save(invoice: Invoice): Mono<Invoice>
}
