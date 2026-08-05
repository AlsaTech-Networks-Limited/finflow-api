package com.alsatech.finflow.shared.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

enum class AccountType { BANK, CASH, CREDIT_CARD }

@Table("accounts")
data class Account(
    @Id val id: Long? = null,
    val companyId: Long,
    val name: String,
    val type: AccountType,
    val balance: BigDecimal = BigDecimal.ZERO,
    val currency: String = "USD",
    val createdAt: Instant = Instant.now(),
)
