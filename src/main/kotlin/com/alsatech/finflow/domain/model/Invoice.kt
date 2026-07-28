package com.alsatech.finflow.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

enum class InvoiceStatus { DRAFT, SENT, PAID, OVERDUE }

@Table("invoices")
data class Invoice(
    @Id val id: UUID? = null,
    val companyId: UUID,
    val clientName: String,
    val amount: BigDecimal,
    val status: InvoiceStatus = InvoiceStatus.DRAFT,
    val dueDate: LocalDate,
)
