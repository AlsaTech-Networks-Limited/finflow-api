package com.alsatech.finflow.shared.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

enum class InvoiceStatus { DRAFT, SENT, PAID, OVERDUE, CANCELLED }

@Table("invoices")
data class Invoice(
    @Id val id: Long? = null,
    val companyId: Long,
    val invoiceNumber: String,
    val clientName: String,
    val amount: BigDecimal,
    val status: InvoiceStatus = InvoiceStatus.DRAFT,
    val issueDate: LocalDate,
    val dueDate: LocalDate,
    val createdAt: Instant = Instant.now(),
)
