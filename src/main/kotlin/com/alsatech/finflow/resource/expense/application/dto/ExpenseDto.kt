package com.alsatech.finflow.resource.expense.application.dto

import com.alsatech.finflow.resource.expense.domain.ExpenseStatus
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.Instant

// ================= COMMAND DTOs (Requests) =================

data class SubmitExpenseCommand(
    @field:NotNull(message = "User ID is required")
    @field:Positive(message = "User ID must be positive")
    val userId: Long,

    @field:NotNull(message = "Category ID is required")
    @field:Positive(message = "Category ID must be positive")
    val categoryId: Long,

    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @field:Digits(integer = 12, fraction = 2, message = "Amount must have at most 12 digits and 2 decimal places")
    val amount: BigDecimal,

    @field:Size(max = 500, message = "Notes must not exceed 500 characters")
    val notes: String? = null,

    val receiptUrl: String? = null
)

data class ApproveExpenseCommand(
    @field:NotNull(message = "Expense ID is required")
    @field:Positive(message = "Expense ID must be positive")
    val expenseId: Long,

    @field:NotNull(message = "Approver ID is required")
    @field:Positive(message = "Approver ID must be positive")
    val approverId: Long,

    @field:Size(max = 500, message = "Comment must not exceed 500 characters")
    val comment: String? = null
)

data class RejectExpenseCommand(
    @field:NotNull(message = "Expense ID is required")
    @field:Positive(message = "Expense ID must be positive")
    val expenseId: Long,

    @field:NotNull(message = "Approver ID is required")
    @field:Positive(message = "Approver ID must be positive")
    val approverId: Long,

    @field:NotBlank(message = "Comment is required when rejecting an expense")
    @field:Size(max = 500, message = "Comment must not exceed 500 characters")
    val comment: String
)

data class GetExpenseQuery(
    val expenseId: Long
)

data class ListExpensesQuery(
    val userId: Long? = null,
    val status: ExpenseStatus? = null,
    val companyId: Long? = null
)

// ================= RESPONSE DTOs =================

data class ExpenseDto(
    val id: Long,
    val userId: Long,
    val categoryId: Long,
    val amount: BigDecimal,
    val status: ExpenseStatus,
    val receiptUrl: String?,
    val notes: String?,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    val createdAt: Instant
)

data class ExpenseDetailDto(
    val id: Long,
    val userId: Long,
    val categoryId: Long,
    val amount: BigDecimal,
    val status: ExpenseStatus,
    val receiptUrl: String?,
    val notes: String?,
    val approvals: List<ApprovalDto>,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    val createdAt: Instant
)

data class ApprovalDto(
    val approverId: Long,
    val decision: String,
    val comment: String?,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    val decidedAt: Instant
)
