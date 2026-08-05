package com.alsatech.finflow.domain.repository

import com.alsatech.finflow.domain.model.Expense
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

/**
 * Port owned by the domain layer. Implemented in `infrastructure.persistence`
 * using Spring Data R2DBC — nothing in `domain` may import R2DBC/Spring types.
 * See api/GUIDE.md "Clean Architecture Layers".
 *
 * Uses Mono/Flux for reactive streams.
 */
interface ExpenseRepository {
    fun findById(id: UUID): Mono<Expense>
    fun findByUserId(userId: UUID): Flux<Expense>
    fun findPendingByCompany(companyId: UUID): Flux<Expense>
    fun save(expense: Expense): Mono<Expense>
}
