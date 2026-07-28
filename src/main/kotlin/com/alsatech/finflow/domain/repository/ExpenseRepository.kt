package com.alsatech.finflow.domain.repository

import com.alsatech.finflow.domain.model.Expense
import java.util.UUID

/**
 * Port owned by the domain layer. Implemented in `infrastructure.persistence`
 * using Spring Data R2DBC — nothing in `domain` may import R2DBC/Spring types.
 * See api/GUIDE.md "Clean Architecture Layers".
 */
interface ExpenseRepository {
    suspend fun findById(id: UUID): Expense?
    suspend fun findByUserId(userId: UUID): List<Expense>
    suspend fun findPendingByCompany(companyId: UUID): List<Expense>
    suspend fun save(expense: Expense): Expense
}
