package com.alsatech.finflow.resource.expense.domain

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Port owned by the expense domain.
 * Implemented in infrastructure.persistence using Spring Data R2DBC.
 * Uses Mono/Flux for reactive streams.
 */
interface ExpenseRepository {
    fun findById(id: Long): Mono<Expense>
    fun findByUserId(userId: Long): Flux<Expense>
    fun findPendingByCompany(companyId: Long): Flux<Expense>
    fun save(expense: Expense): Mono<Expense>
}
