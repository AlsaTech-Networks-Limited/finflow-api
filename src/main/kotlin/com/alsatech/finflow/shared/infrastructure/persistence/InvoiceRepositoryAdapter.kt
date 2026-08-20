package com.alsatech.finflow.shared.infrastructure.persistence

import com.alsatech.finflow.shared.domain.entity.Invoice
import com.alsatech.finflow.shared.domain.entity.InvoiceStatus
import com.alsatech.finflow.shared.domain.repository.InvoiceRepository
import com.collicode.common.util.asUnit
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class InvoiceRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : InvoiceRepository {

    override fun findById(id: Long): Mono<Invoice> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.selectOne(query, Invoice::class.java)
    }

    override fun findByCompanyId(companyId: Long): Flux<Invoice> {
        val query = Query.query(Criteria.where("company_id").`is`(companyId))
        return template.select(query, Invoice::class.java)
    }

    override fun findByInvoiceNumber(invoiceNumber: String): Mono<Invoice> {
        val query = Query.query(Criteria.where("invoice_number").`is`(invoiceNumber))
        return template.selectOne(query, Invoice::class.java)
    }

    override fun save(invoice: Invoice): Mono<Invoice> {
        return if (invoice.id == null) {
            template.insert(Invoice::class.java).using(invoice)
        } else {
            template.update(invoice)
        }
    }

    override fun updateStatus(invoiceId: Long, status: InvoiceStatus): Mono<Invoice> {
        val query = Query.query(Criteria.where("id").`is`(invoiceId))
        val update = Update.update("status", status.name)

        return template.update(Invoice::class.java)
            .matching(query)
            .apply(update)
            .then(findById(invoiceId))
    }

    override fun deleteById(id: Long): Mono<Unit> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.delete(Invoice::class.java)
            .matching(query)
            .all()
            .asUnit()
    }
}
