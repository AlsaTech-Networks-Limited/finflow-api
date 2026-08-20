package com.alsatech.finflow.shared.infrastructure.persistence

import com.alsatech.finflow.shared.domain.entity.InvoiceLineItem
import com.alsatech.finflow.shared.domain.repository.InvoiceLineItemRepository
import com.collicode.common.util.asUnit
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class InvoiceLineItemRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : InvoiceLineItemRepository {

    override fun findById(id: Long): Mono<InvoiceLineItem> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.selectOne(query, InvoiceLineItem::class.java)
    }

    override fun findByInvoiceId(invoiceId: Long): Flux<InvoiceLineItem> {
        val query = Query.query(Criteria.where("invoice_id").`is`(invoiceId))
            .sort(Sort.by(Sort.Direction.ASC, "position"))
        return template.select(query, InvoiceLineItem::class.java)
    }

    override fun save(lineItem: InvoiceLineItem): Mono<InvoiceLineItem> {
        return if (lineItem.id == null) {
            template.insert(InvoiceLineItem::class.java).using(lineItem)
        } else {
            template.update(lineItem)
        }
    }

    override fun saveAll(lineItems: List<InvoiceLineItem>): Flux<InvoiceLineItem> {
        return Flux.fromIterable(lineItems)
            .flatMap { save(it) }
    }

    override fun deleteByInvoiceId(invoiceId: Long): Mono<Unit> {
        val query = Query.query(Criteria.where("invoice_id").`is`(invoiceId))
        return template.delete(InvoiceLineItem::class.java)
            .matching(query)
            .all()
            .asUnit()
    }

    override fun deleteById(id: Long): Mono<Unit> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.delete(InvoiceLineItem::class.java)
            .matching(query)
            .all()
            .asUnit()
    }
}
