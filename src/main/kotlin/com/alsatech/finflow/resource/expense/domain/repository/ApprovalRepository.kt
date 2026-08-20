package com.alsatech.finflow.resource.expense.domain.repository

import com.alsatech.finflow.resource.expense.domain.entity.Approval
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Port owned by the expense domain.
 * Implemented in infrastructure.persistence using Spring Data R2DBC.
 * Uses Mono/Flux for reactive streams.
 */
interface ApprovalRepository {
    fun findByExpenseId(expenseId: String): Flux<Approval>
    fun save(approval: Approval): Mono<Approval>
}
