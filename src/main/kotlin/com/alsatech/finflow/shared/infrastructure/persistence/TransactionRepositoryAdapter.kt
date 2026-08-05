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

    override fun findByAccountId(accountId: Long): Flux<Transaction> {
        val query = Query.query(Criteria.where("account_id").`is`(accountId))
        return template.select(query, Transaction::class.java)
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
