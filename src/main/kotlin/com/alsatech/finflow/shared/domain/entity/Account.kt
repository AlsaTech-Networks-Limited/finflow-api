package com.alsatech.finflow.shared.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

enum class AccountType { BANK, CASH, CREDIT_CARD }

@Table("accounts")
data class Account(
    @Id val id: Long? = null,
    val companyId: Long,
    val name: String,
    val type: AccountType,
    val balance: BigDecimal = BigDecimal.ZERO,
    val currency: String = "USD",
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
