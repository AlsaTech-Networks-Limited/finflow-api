package com.alsatech.finflow.resource.expense.infrastructure.persistence

import com.alsatech.finflow.resource.expense.domain.entity.Expense
import com.alsatech.finflow.resource.expense.domain.entity.ExpenseStatus
import com.alsatech.finflow.resource.expense.domain.repository.ExpenseRepository
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Infrastructure adapter implementing the ExpenseRepository port.
 * Uses R2dbcEntityTemplate for database operations following the Mulab pattern.
 * Separates read and write concerns internally.
 */
@Repository
class ExpenseRepositoryAdapter(
    private val template: R2dbcEntityTemplate,
    private val databaseClient: DatabaseClient
) : ExpenseRepository {

    // ==================== READ OPERATIONS ====================

    override fun findById(id: String): Mono<Expense> {
        val query = Query.query(Criteria.where("record_id").`is`(id))
        return template.selectOne(query, Expense::class.java)
    }

    override fun findByUserId(userId: Long): Flux<Expense> {
        val query = Query.query(Criteria.where("user_id").`is`(userId))
            .sort(Sort.by(Sort.Direction.DESC, "created_at"))
        return template.select(query, Expense::class.java)
    }

    override fun findPendingByCompany(companyId: Long): Flux<Expense> {
        val sql = """
            SELECT e.*
            FROM exp_finlow.expenses e
            INNER JOIN exp_finlow.users u ON e.user_id = u.id
            WHERE u.company_id = :companyId
            AND e.status = :status
            ORDER BY e.created_at DESC
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("companyId", companyId)
            .bind("status", ExpenseStatus.PENDING.name)
            .map { row, _ -> mapRow(row) }
            .all()
    }

    // Same join as findPendingByCompany but without the status filter — needed
    // by DashboardServiceImpl's expense summary, which has to see ALL statuses
    // to compute pendingCount/approvedCount/rejectedCount. Reusing
    // findPendingByCompany there (as the dashboard code originally did) silently
    // made totalExpenses/approvedCount/rejectedCount wrong: every non-pending
    // expense was invisible to it.
    override fun findByCompany(companyId: Long): Flux<Expense> {
        val sql = """
            SELECT e.*
            FROM exp_finlow.expenses e
            INNER JOIN exp_finlow.users u ON e.user_id = u.id
            WHERE u.company_id = :companyId
            ORDER BY e.created_at DESC
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("companyId", companyId)
            .map { row, _ -> mapRow(row) }
            .all()
    }

    private fun mapRow(row: io.r2dbc.spi.Row): Expense {
        return Expense(
            expenseId = row.get("record_id", String::class.java)!!,
            userId = row.get("user_id", java.lang.Long::class.java)!!.toLong(),
            categoryId = row.get("category_id", java.lang.Long::class.java)!!.toLong(),
            amount = row.get("amount", BigDecimal::class.java)!!,
            status = ExpenseStatus.valueOf(row.get("status", String::class.java)!!),
            receiptUrl = row.get("receipt_url", String::class.java),
            notes = row.get("notes", String::class.java),
            createdAt = row.get("created_at", LocalDateTime::class.java)!!,
            updatedAt = row.get("updated_at", LocalDateTime::class.java)!!
        )
    }

    // ==================== WRITE OPERATIONS ====================

    override fun save(expense: Expense): Mono<Expense> {
        return findById(expense.expenseId)
            .flatMap { existing ->
                // Expense exists - update it
                update(expense).thenReturn(expense)
            }
            .switchIfEmpty(
                // Expense doesn't exist - insert it
                insert(expense).thenReturn(expense)
            )
    }

    private fun insert(expense: Expense): Mono<Expense> {
        return template.insert(Expense::class.java)
            .using(expense)
    }

    private fun update(expense: Expense): Mono<Expense> {
        val query = Query.query(Criteria.where("record_id").`is`(expense.expenseId))

        val update = Update.update("user_id", expense.userId)
            .set("category_id", expense.categoryId)
            .set("amount", expense.amount)
            .set("status", expense.status.name)
            .set("receipt_url", expense.receiptUrl)
            .set("notes", expense.notes)
            .set("updated_at", LocalDateTime.now())

        return template.update(Expense::class.java)
            .matching(query)
            .apply(update)
            .thenReturn(expense.copy(updatedAt = LocalDateTime.now()))
    }
}
