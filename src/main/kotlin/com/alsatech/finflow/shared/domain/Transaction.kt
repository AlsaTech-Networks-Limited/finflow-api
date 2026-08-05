package com.alsatech.finflow.shared.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

enum class TransactionType { DEBIT, CREDIT }

@Table("transactions")
data class Transaction(
    @Id val id: Long? = null,
    val accountId: Long,
    val type: TransactionType,
    val amount: BigDecimal,
    val description: String? = null,
    val reference: String? = null,
    val createdAt: Instant = Instant.now(),
)
