package com.alsatech.finflow.resource.account.dto

import com.alsatech.finflow.shared.domain.entity.AccountType
import com.collicode.common.util.AuditInfo
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime

// ================= COMMAND DTOs (Requests) =================

data class CreateAccountCommand(
    @field:NotNull(message = "Company ID is required")
    @field:Positive(message = "Company ID must be positive")
    val companyId: Long,

    @field:NotBlank(message = "Account name is required")
    @field:Size(min = 1, max = 250, message = "Account name must be between 1 and 250 characters")
    val name: String,

    @field:NotNull(message = "Account type is required")
    val type: AccountType,

    @field:NotBlank(message = "Currency is required")
    @field:Size(min = 3, max = 3, message = "Currency must be a 3-letter code (e.g., USD, EUR)")
    val currency: String = "USD",

    @field:NotNull(message = "Initial balance is required")
    @field:Digits(integer = 12, fraction = 2, message = "Balance must have at most 12 digits and 2 decimal places")
    val initialBalance: BigDecimal = BigDecimal.ZERO,

    val auditInfo: AuditInfo? = null
)

data class UpdateAccountCommand(
    @field:NotNull(message = "Account ID is required")
    @field:Positive(message = "Account ID must be positive")
    val accountId: Long,

    @field:Size(min = 1, max = 250, message = "Account name must be between 1 and 250 characters")
    val name: String?,

    @field:Digits(integer = 12, fraction = 2, message = "Balance must have at most 12 digits and 2 decimal places")
    val balance: BigDecimal?,

    val auditInfo: AuditInfo? = null
)

data class GetAccountQuery(
    val accountId: Long
)

data class ListAccountsQuery(
    val companyId: Long
)

// ================= RESPONSE DTOs =================

/**
 * Account response DTO.
 * Used for account list and detail endpoints.
 */
data class AccountDto(
    val id: Long,
    val companyId: Long,
    val name: String,
    val type: AccountType,
    val balance: BigDecimal,
    val currency: String,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime
)

/**
 * Account detail response DTO with transactions.
 * Used for single account endpoint.
 */
data class AccountDetailDto(
    val id: Long,
    val companyId: Long,
    val name: String,
    val type: AccountType,
    val balance: BigDecimal,
    val currency: String,
    val transactionCount: Long,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime
)
