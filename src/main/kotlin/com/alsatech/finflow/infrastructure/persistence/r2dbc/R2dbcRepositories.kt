package com.alsatech.finflow.infrastructure.persistence.r2dbc

import com.alsatech.finflow.domain.model.*
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

/**
 * Spring Data R2DBC repositories — one per table, auto-implemented by Spring
 * at runtime (no method bodies needed). These are wrapped by adapter classes
 * (e.g. ExpenseRepositoryAdapter) that implement the domain repository ports,
 * so `application`/`domain` never see Spring Data types directly.
 */
interface CompanyR2dbcRepository : CoroutineCrudRepository<Company, UUID>

interface UserR2dbcRepository : CoroutineCrudRepository<User, UUID> {
    suspend fun findByEmail(email: String): User?
}

interface AccountR2dbcRepository : CoroutineCrudRepository<Account, UUID> {
    suspend fun findByCompanyId(companyId: UUID): List<Account>
}

interface TransactionR2dbcRepository : CoroutineCrudRepository<Transaction, UUID> {
    suspend fun findByAccountId(accountId: UUID): List<Transaction>
}

interface CategoryR2dbcRepository : CoroutineCrudRepository<Category, UUID> {
    suspend fun findByCompanyId(companyId: UUID): List<Category>
}

interface ExpenseR2dbcRepository : CoroutineCrudRepository<Expense, UUID> {
    suspend fun findByUserId(userId: UUID): List<Expense>
}

interface InvoiceR2dbcRepository : CoroutineCrudRepository<Invoice, UUID> {
    suspend fun findByCompanyId(companyId: UUID): List<Invoice>
}

interface ApprovalR2dbcRepository : CoroutineCrudRepository<Approval, UUID> {
    suspend fun findByExpenseId(expenseId: UUID): List<Approval>
}
