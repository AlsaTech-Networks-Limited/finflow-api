package com.alsatech.finflow.shared.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

enum class ReimbursementStatus { PENDING, PAID }
enum class ReimbursementMethod { BANK_TRANSFER, PAYROLL }

@Table("reimbursements")
data class Reimbursement(
    @Id val id: Long? = null,
    val expenseId: String,
    val amount: BigDecimal,
    val status: ReimbursementStatus = ReimbursementStatus.PENDING,
    val method: ReimbursementMethod = ReimbursementMethod.BANK_TRANSFER,
    val paidAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
