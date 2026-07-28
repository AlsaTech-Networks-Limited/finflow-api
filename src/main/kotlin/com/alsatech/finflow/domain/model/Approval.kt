package com.alsatech.finflow.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

enum class Decision { APPROVED, REJECTED }

@Table("approvals")
data class Approval(
    @Id val id: UUID? = null,
    val expenseId: UUID,
    val approverId: UUID,
    val decision: Decision,
    val comment: String? = null,
    val decidedAt: Instant = Instant.now(),
)
