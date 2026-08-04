package com.alsatech.finflow.infrastructure.persistence.r2dbc

import com.alsatech.finflow.domain.model.*
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * Spring Data R2DBC repositories — one per table, auto-implemented by Spring
 * at runtime (no method bodies needed). These are wrapped by adapter classes
 * (e.g. ExpenseRepositoryAdapter) that implement the domain repository ports,
 * so `application`/`domain` never see Spring Data types directly.
 *
 * Uses Mono/Flux for reactive streams instead of coroutines.
 */
interface CompanyR2dbcRepository : ReactiveCrudRepository<Company, UUID>

interface UserR2dbcRepository : ReactiveCrudRepository<User, UUID> {
    fun findByEmail(email: String): Mono<User>
}

interface AccountR2dbcRepository : ReactiveCrudRepository<Account, UUID> {
    fun findByCompanyId(companyId: UUID): Flux<Account>
}

interface TransactionR2dbcRepository : ReactiveCrudRepository<Transaction, UUID> {
    fun findByAccountId(accountId: UUID): Flux<Transaction>
}

interface CategoryR2dbcRepository : ReactiveCrudRepository<Category, UUID> {
    fun findByCompanyId(companyId: UUID): Flux<Category>
}

interface ExpenseR2dbcRepository : ReactiveCrudRepository<Expense, UUID> {
    fun findByUserId(userId: UUID): Flux<Expense>
}

interface InvoiceR2dbcRepository : ReactiveCrudRepository<Invoice, UUID> {
    fun findByCompanyId(companyId: UUID): Flux<Invoice>
}

interface ApprovalR2dbcRepository : ReactiveCrudRepository<Approval, UUID> {
    fun findByExpenseId(expenseId: UUID): Flux<Approval>
}
