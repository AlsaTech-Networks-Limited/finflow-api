package com.alsatech.finflow.shared.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

enum class InvoiceStatus { DRAFT, SENT, PAID, OVERDUE, CANCELLED }

@Table("invoices")
data class Invoice(
    @Id val id: Long? = null,
    val companyId: Long,
    val invoiceNumber: String,
    val clientName: String,
    val clientEmail: String? = null,
    val amount: BigDecimal,
    val status: InvoiceStatus = InvoiceStatus.DRAFT,
    val issueDate: LocalDate,
    val dueDate: LocalDate,
    val notes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
