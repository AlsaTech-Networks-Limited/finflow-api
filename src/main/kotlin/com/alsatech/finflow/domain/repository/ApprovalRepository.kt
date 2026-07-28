package com.alsatech.finflow.domain.repository

import com.alsatech.finflow.domain.model.Approval
import java.util.UUID

interface ApprovalRepository {
    suspend fun findByExpenseId(expenseId: UUID): List<Approval>
    suspend fun save(approval: Approval): Approval
}
