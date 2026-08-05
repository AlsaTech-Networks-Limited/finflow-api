package com.alsatech.finflow.resource.expense.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("approvals")
data class Approval(
    @Id val id: Long? = null,
    val expenseId: Long,
    val approverId: Long,
    val decision: String,
    val comment: String? = null,
    val decidedAt: Instant = Instant.now(),
)
