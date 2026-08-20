package com.alsatech.finflow.shared.domain.repository

import com.alsatech.finflow.shared.domain.entity.Reimbursement
import com.alsatech.finflow.shared.domain.entity.ReimbursementStatus
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository interface for Reimbursement entity.
 * Defines data access contract owned by the domain.
 */
interface ReimbursementRepository {
    fun findById(id: Long): Mono<Reimbursement>
    fun findByExpenseId(expenseId: String): Mono<Reimbursement>
    fun findByStatus(status: ReimbursementStatus): Flux<Reimbursement>
    fun findAll(): Flux<Reimbursement>
    fun save(reimbursement: Reimbursement): Mono<Reimbursement>
    fun markAsPaid(reimbursementId: Long): Mono<Reimbursement>
}
