package com.alsatech.finflow.shared.infrastructure.persistence

import com.alsatech.finflow.shared.domain.entity.Invoice
import com.alsatech.finflow.shared.domain.repository.InvoiceRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class InvoiceRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : InvoiceRepository {

    override fun findByCompanyId(companyId: Long): Flux<Invoice> {
        val query = Query.query(Criteria.where("company_id").`is`(companyId))
        return template.select(query, Invoice::class.java)
    }

    override fun save(invoice: Invoice): Mono<Invoice> {
        return if (invoice.id == null) {
            template.insert(Invoice::class.java).using(invoice)
        } else {
            template.update(invoice)
        }
    }
}
