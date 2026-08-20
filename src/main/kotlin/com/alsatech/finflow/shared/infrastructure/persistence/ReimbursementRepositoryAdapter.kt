package com.alsatech.finflow.shared.infrastructure.persistence

import com.alsatech.finflow.shared.domain.entity.Reimbursement
import com.alsatech.finflow.shared.domain.entity.ReimbursementStatus
import com.alsatech.finflow.shared.domain.repository.ReimbursementRepository
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
class ReimbursementRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : ReimbursementRepository {

    override fun findById(id: Long): Mono<Reimbursement> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.selectOne(query, Reimbursement::class.java)
    }

    override fun findByExpenseId(expenseId: String): Mono<Reimbursement> {
        val query = Query.query(Criteria.where("expense_id").`is`(expenseId))
        return template.selectOne(query, Reimbursement::class.java)
    }

    override fun findByStatus(status: ReimbursementStatus): Flux<Reimbursement> {
        val query = Query.query(Criteria.where("status").`is`(status.name))
            .sort(Sort.by(Sort.Direction.DESC, "created_at"))
        return template.select(query, Reimbursement::class.java)
    }

    override fun findAll(): Flux<Reimbursement> {
        val query = Query.query(Criteria.empty())
            .sort(Sort.by(Sort.Direction.DESC, "created_at"))
        return template.select(query, Reimbursement::class.java)
    }

    override fun save(reimbursement: Reimbursement): Mono<Reimbursement> {
        return if (reimbursement.id == null) {
            template.insert(Reimbursement::class.java).using(reimbursement)
        } else {
            template.update(reimbursement)
        }
    }

    override fun markAsPaid(reimbursementId: Long): Mono<Reimbursement> {
        val query = Query.query(Criteria.where("id").`is`(reimbursementId))
        val update = Update.update("status", ReimbursementStatus.PAID.name)
            .set("paid_at", LocalDateTime.now())

        return template.update(Reimbursement::class.java)
            .matching(query)
            .apply(update)
            .then(findById(reimbursementId))
    }
}
