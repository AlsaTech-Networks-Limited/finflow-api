package com.alsatech.finflow.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Table("transactions")
data class Transaction(
    @Id val id: UUID? = null,
    val accountId: UUID,
    val amount: BigDecimal,
    val description: String,
    val occurredAt: Instant,
    val reconciled: Boolean = false,
)
