package com.alsatech.finflow.resource.expense.infrastructure.persistence

import com.alsatech.finflow.resource.expense.domain.entity.Approval
import com.alsatech.finflow.resource.expense.domain.repository.ApprovalRepository
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

/**
 * Infrastructure adapter implementing the ApprovalRepository port.
 * Uses R2dbcEntityTemplate for database operations following the Mulab pattern.
 * Separates read and write concerns internally.
 */
@Repository
class ApprovalRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : ApprovalRepository {

    // ==================== READ OPERATIONS ====================

    override fun findByExpenseId(expenseId: String): Flux<Approval> {
        val query = Query.query(Criteria.where("expense_id").`is`(expenseId))
            .sort(Sort.by(Sort.Direction.DESC, "decided_at"))
        return template.select(query, Approval::class.java)
    }

    private fun findById(approvalId: String): Mono<Approval> {
        val query = Query.query(Criteria.where("record_id").`is`(approvalId))
        return template.selectOne(query, Approval::class.java)
    }

    // ==================== WRITE OPERATIONS ====================

    override fun save(approval: Approval): Mono<Approval> {
        return findById(approval.approvalId)
            .flatMap { existing ->
                // Approval exists - update it
                update(approval).thenReturn(approval)
            }
            .switchIfEmpty(
                // Approval doesn't exist - insert it
                insert(approval).thenReturn(approval)
            )
    }

    private fun insert(approval: Approval): Mono<Approval> {
        return template.insert(Approval::class.java)
            .using(approval)
    }

    private fun update(approval: Approval): Mono<Approval> {
        val query = Query.query(Criteria.where("record_id").`is`(approval.approvalId))

        val update = Update.update("expense_id", approval.expenseId)
            .set("approver_id", approval.approverId)
            .set("decision", approval.decision)
            .set("comment", approval.comment)
            .set("decided_at", approval.decidedAt)
            .set("updated_at", LocalDateTime.now())

        return template.update(Approval::class.java)
            .matching(query)
            .apply(update)
            .thenReturn(approval.copy(updatedAt = LocalDateTime.now()))
    }
}
