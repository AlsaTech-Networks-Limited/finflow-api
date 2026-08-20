package com.alsatech.finflow.shared.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("invoice_line_items")
data class InvoiceLineItem(
    @Id val id: Long? = null,
    val invoiceId: Long,
    val description: String,
    val quantity: BigDecimal,
    val unitPrice: BigDecimal,
    val amount: BigDecimal,
    val position: Int
)
