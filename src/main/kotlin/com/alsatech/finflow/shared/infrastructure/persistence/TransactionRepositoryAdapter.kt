package com.alsatech.finflow.shared.infrastructure.persistence

import com.alsatech.finflow.shared.domain.entity.Transaction
import com.alsatech.finflow.shared.domain.repository.TransactionRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class TransactionRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : TransactionRepository {

    override fun findById(id: Long): Mono<Transaction> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.selectOne(query, Transaction::class.java)
    }

    override fun findByAccountId(accountId: Long): Flux<Transaction> {
        val query = Query.query(Criteria.where("account_id").`is`(accountId))
        return template.select(query, Transaction::class.java)
    }

    override fun findByCompanyId(companyId: Long): Flux<Transaction> {
        // Join with accounts table to filter by company_id
        val sql = """
            SELECT t.* FROM exp_finlow.transactions t
            INNER JOIN exp_finlow.accounts a ON t.account_id = a.id
            WHERE a.company_id = $1
            ORDER BY t.occurred_at DESC
        """.trimIndent()

        return template.databaseClient.sql(sql)
            .bind(0, companyId)
            .map { row, _ ->
                Transaction(
                    id = row.get("id", java.lang.Long::class.java)?.toLong(),
                    accountId = row.get("account_id", java.lang.Long::class.java)!!.toLong(),
                    type = com.alsatech.finflow.shared.domain.entity.TransactionType.valueOf(row.get("type", String::class.java)!!),
                    amount = row.get("amount", java.math.BigDecimal::class.java)!!,
                    description = row.get("description", String::class.java),
                    reference = row.get("reference", String::class.java),
                    occurredAt = row.get("occurred_at", java.time.LocalDateTime::class.java)!!,
                    reconciled = row.get("reconciled", java.lang.Boolean::class.java)!!.booleanValue(),
                    createdAt = row.get("created_at", java.time.LocalDateTime::class.java)!!
                )
            }
            .all()
    }

    override fun save(transaction: Transaction): Mono<Transaction> {
        return if (transaction.id == null) {
            template.insert(Transaction::class.java).using(transaction)
        } else {
            template.update(transaction)
        }
    }

    override fun saveAll(transactions: List<Transaction>): Flux<Transaction> {
        return Flux.fromIterable(transactions)
            .flatMap { save(it) }
    }
}
