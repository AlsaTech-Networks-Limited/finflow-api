package com.alsatech.finflow.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.util.UUID

@Table("accounts")
data class Account(
    @Id val id: UUID? = null,
    val companyId: UUID,
    val name: String,
    val currency: String,
    val balance: BigDecimal,
    val type: String,
)
