package com.alsatech.finflow.resource.transaction.dto

import com.alsatech.finflow.shared.domain.entity.TransactionType
import com.collicode.common.util.AuditInfo
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime

// ================= COMMAND DTOs (Requests) =================

data class CreateTransactionCommand(
    @field:NotNull(message = "Account ID is required")
    @field:Positive(message = "Account ID must be positive")
    val accountId: Long,

    @field:NotNull(message = "Transaction type is required")
    val type: TransactionType,

    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @field:Digits(integer = 12, fraction = 2, message = "Amount must have at most 12 digits and 2 decimal places")
    val amount: BigDecimal,

    @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String? = null,

    @field:Size(max = 250, message = "Reference must not exceed 250 characters")
    val reference: String? = null,

    @field:NotNull(message = "Occurred at timestamp is required")
    val occurredAt: LocalDateTime,

    val auditInfo: AuditInfo? = null
)

data class ImportTransactionsCommand(
    @field:NotNull(message = "Account ID is required")
    @field:Positive(message = "Account ID must be positive")
    val accountId: Long,

    @field:NotEmpty(message = "Transactions list cannot be empty")
    val transactions: List<TransactionImportItem>,

    val auditInfo: AuditInfo? = null
)

data class TransactionImportItem(
    @field:NotNull(message = "Transaction type is required")
    val type: TransactionType,

    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    val amount: BigDecimal,

    val description: String? = null,
    val reference: String? = null,

    @field:NotNull(message = "Occurred at timestamp is required")
    val occurredAt: LocalDateTime
)

data class ReconcileTransactionCommand(
    @field:NotNull(message = "Transaction ID is required")
    @field:Positive(message = "Transaction ID must be positive")
    val transactionId: Long,

    val auditInfo: AuditInfo? = null
)

data class LinkExpenseCommand(
    @field:NotNull(message = "Transaction ID is required")
    @field:Positive(message = "Transaction ID must be positive")
    val transactionId: Long,

    @field:NotBlank(message = "Expense ID is required")
    val expenseId: String,

    val auditInfo: AuditInfo? = null
)

data class GetTransactionQuery(
    val transactionId: Long
)

data class ListTransactionsQuery(
    val accountId: Long? = null,
    val companyId: Long? = null,
    val reconciled: Boolean? = null
)

// ================= RESPONSE DTOs =================

/**
 * Transaction response DTO.
 * Used for transaction list endpoints.
 */
data class TransactionDto(
    val id: Long,
    val accountId: Long,
    val type: TransactionType,
    val amount: BigDecimal,
    val description: String?,
    val reference: String?,
    val reconciled: Boolean,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val occurredAt: LocalDateTime,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime
)

/**
 * Transaction detail response DTO.
 * Used for single transaction endpoint.
 */
data class TransactionDetailDto(
    val id: Long,
    val accountId: Long,
    val accountName: String,
    val type: TransactionType,
    val amount: BigDecimal,
    val description: String?,
    val reference: String?,
    val reconciled: Boolean,
    val linkedExpenseId: String?,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val occurredAt: LocalDateTime,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime
)

/**
 * Response for bulk import operations.
 */
data class TransactionImportResponse(
    val imported: Int,
    val failed: Int,
    val errors: List<String> = emptyList()
)
