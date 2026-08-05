package com.alsatech.finflow.resource.expense.infrastructure.persistence

import com.alsatech.finflow.resource.expense.domain.Expense
import com.alsatech.finflow.resource.expense.domain.ExpenseRepository
import com.alsatech.finflow.resource.expense.domain.ExpenseStatus
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Instant

/**
 * Infrastructure adapter implementing the ExpenseRepository port.
 * Uses R2dbcEntityTemplate for simple queries and DatabaseClient for complex joins.
 */
@Repository
class ExpenseRepositoryAdapter(
    private val template: R2dbcEntityTemplate,
    private val databaseClient: DatabaseClient
) : ExpenseRepository {

    override fun findById(id: Long): Mono<Expense> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.selectOne(query, Expense::class.java)
    }

    override fun findByUserId(userId: Long): Flux<Expense> {
        val query = Query.query(Criteria.where("user_id").`is`(userId))
        return template.select(query, Expense::class.java)
    }

    override fun findPendingByCompany(companyId: Long): Flux<Expense> {
        val sql = """
            SELECT e.*
            FROM expenses e
            INNER JOIN users u ON e.user_id = u.id
            WHERE u.company_id = :companyId
            AND e.status = :status
            ORDER BY e.created_at DESC
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("companyId", companyId)
            .bind("status", ExpenseStatus.PENDING.name)
            .map { row, _ ->
                Expense(
                    id = row.get("id", java.lang.Long::class.java)?.toLong(),
                    userId = row.get("user_id", java.lang.Long::class.java)!!.toLong(),
                    categoryId = row.get("category_id", java.lang.Long::class.java)!!.toLong(),
                    amount = row.get("amount", BigDecimal::class.java)!!,
                    status = ExpenseStatus.valueOf(row.get("status", String::class.java)!!),
                    receiptUrl = row.get("receipt_url", String::class.java),
                    notes = row.get("notes", String::class.java),
                    createdAt = row.get("created_at", Instant::class.java)!!
                )
            }
            .all()
    }

    override fun save(expense: Expense): Mono<Expense> {
        return if (expense.id == null) {
            template.insert(Expense::class.java).using(expense)
        } else {
            template.update(expense)
        }
    }
}
