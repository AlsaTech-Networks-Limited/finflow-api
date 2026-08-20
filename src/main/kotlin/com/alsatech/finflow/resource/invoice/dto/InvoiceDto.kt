package com.alsatech.finflow.resource.invoice.dto

import com.alsatech.finflow.shared.domain.entity.InvoiceStatus
import com.collicode.common.util.AuditInfo
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

// ================= LINE ITEM DTOs =================

data class InvoiceLineItemDto(
    val id: Long? = null,
    val description: String,
    val quantity: BigDecimal,
    val unitPrice: BigDecimal,
    val amount: BigDecimal,
    val position: Int
)

data class CreateInvoiceLineItemCommand(
    @field:NotBlank(message = "Line item description is required")
    @field:Size(max = 500, message = "Description must not exceed 500 characters")
    val description: String,

    @field:NotNull(message = "Quantity is required")
    @field:DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    val quantity: BigDecimal,

    @field:NotNull(message = "Unit price is required")
    @field:DecimalMin(value = "0.00", message = "Unit price must be non-negative")
    val unitPrice: BigDecimal
)

// ================= COMMAND DTOs =================

data class CreateInvoiceCommand(
    @field:NotNull(message = "Company ID is required")
    @field:Positive(message = "Company ID must be positive")
    val companyId: Long,

    @field:NotBlank(message = "Invoice number is required")
    @field:Size(max = 100, message = "Invoice number must not exceed 100 characters")
    val invoiceNumber: String,

    @field:NotBlank(message = "Client name is required")
    @field:Size(max = 250, message = "Client name must not exceed 250 characters")
    val clientName: String,

    @field:Email(message = "Invalid email format")
    @field:Size(max = 250, message = "Client email must not exceed 250 characters")
    val clientEmail: String? = null,

    @field:NotNull(message = "Issue date is required")
    val issueDate: LocalDate,

    @field:NotNull(message = "Due date is required")
    val dueDate: LocalDate,

    @field:Size(max = 1000, message = "Notes must not exceed 1000 characters")
    val notes: String? = null,

    @field:Valid
    @field:NotEmpty(message = "At least one line item is required")
    val lineItems: List<CreateInvoiceLineItemCommand>,

    val auditInfo: AuditInfo? = null
)

data class UpdateInvoiceCommand(
    @field:NotNull(message = "Invoice ID is required")
    @field:Positive(message = "Invoice ID must be positive")
    val invoiceId: Long,

    @field:NotBlank(message = "Client name is required")
    @field:Size(max = 250, message = "Client name must not exceed 250 characters")
    val clientName: String,

    @field:Email(message = "Invalid email format")
    @field:Size(max = 250, message = "Client email must not exceed 250 characters")
    val clientEmail: String? = null,

    @field:NotNull(message = "Issue date is required")
    val issueDate: LocalDate,

    @field:NotNull(message = "Due date is required")
    val dueDate: LocalDate,

    @field:Size(max = 1000, message = "Notes must not exceed 1000 characters")
    val notes: String? = null,

    @field:Valid
    @field:NotEmpty(message = "At least one line item is required")
    val lineItems: List<CreateInvoiceLineItemCommand>,

    val auditInfo: AuditInfo? = null
)

data class SendInvoiceCommand(
    @field:NotNull(message = "Invoice ID is required")
    @field:Positive(message = "Invoice ID must be positive")
    val invoiceId: Long,

    val auditInfo: AuditInfo? = null
)

data class MarkInvoicePaidCommand(
    @field:NotNull(message = "Invoice ID is required")
    @field:Positive(message = "Invoice ID must be positive")
    val invoiceId: Long,

    val auditInfo: AuditInfo? = null
)

data class CancelInvoiceCommand(
    @field:NotNull(message = "Invoice ID is required")
    @field:Positive(message = "Invoice ID must be positive")
    val invoiceId: Long,

    val auditInfo: AuditInfo? = null
)

data class ListInvoicesQuery(
    @field:NotNull(message = "Company ID is required")
    @field:Positive(message = "Company ID must be positive")
    val companyId: Long,

    val status: InvoiceStatus? = null
)

// ================= RESPONSE DTOs =================

data class InvoiceDto(
    val id: Long,
    val companyId: Long,
    val invoiceNumber: String,
    val clientName: String,
    val clientEmail: String?,
    val amount: BigDecimal,
    val status: InvoiceStatus,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val issueDate: LocalDate,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val dueDate: LocalDate,

    val notes: String?,
    val lineItemsCount: Int,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime
)

data class InvoiceDetailDto(
    val id: Long,
    val companyId: Long,
    val invoiceNumber: String,
    val clientName: String,
    val clientEmail: String?,
    val amount: BigDecimal,
    val status: InvoiceStatus,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val issueDate: LocalDate,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val dueDate: LocalDate,

    val notes: String?,
    val lineItems: List<InvoiceLineItemDto>,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime
)
