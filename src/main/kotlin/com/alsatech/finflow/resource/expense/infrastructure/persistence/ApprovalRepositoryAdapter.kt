package com.alsatech.finflow.resource.expense.infrastructure.persistence

import com.alsatech.finflow.resource.expense.domain.Approval
import com.alsatech.finflow.resource.expense.domain.ApprovalRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Infrastructure adapter implementing the ApprovalRepository port.
 * Uses R2dbcEntityTemplate for reactive database operations.
 */
@Repository
class ApprovalRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : ApprovalRepository {

    override fun findByExpenseId(expenseId: Long): Flux<Approval> {
        val query = Query.query(Criteria.where("expense_id").`is`(expenseId))
        return template.select(query, Approval::class.java)
    }

    override fun save(approval: Approval): Mono<Approval> {
        return if (approval.id == null) {
            template.insert(Approval::class.java).using(approval)
        } else {
            template.update(approval)
        }
    }
}
