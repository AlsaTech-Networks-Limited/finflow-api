package com.alsatech.finflow.infrastructure.persistence

import com.alsatech.finflow.domain.model.Expense
import com.alsatech.finflow.domain.model.ExpenseStatus
import com.alsatech.finflow.domain.repository.ExpenseRepository
import com.alsatech.finflow.infrastructure.persistence.r2dbc.ExpenseR2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
class ExpenseRepositoryAdapter(
    private val expenseR2dbcRepository: ExpenseR2dbcRepository
) : ExpenseRepository {

    override fun findById(id: UUID): Mono<Expense> {
        return expenseR2dbcRepository.findById(id)
    }

    override fun findByUserId(userId: UUID): Flux<Expense> {
        return expenseR2dbcRepository.findByUserId(userId)
    }

    override fun findPendingByCompany(companyId: UUID): Flux<Expense> {
        // TODO: This needs a custom query to join expenses with users to filter by companyId and status
        // For now, returning empty flux as placeholder
        return Flux.empty()
    }

    override fun save(expense: Expense): Mono<Expense> {
        return expenseR2dbcRepository.save(expense)
    }
}
