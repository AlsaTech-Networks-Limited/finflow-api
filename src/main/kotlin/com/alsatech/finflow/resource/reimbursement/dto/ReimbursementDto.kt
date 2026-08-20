package com.alsatech.finflow.resource.reimbursement.dto

import com.alsatech.finflow.shared.domain.entity.ReimbursementMethod
import com.alsatech.finflow.shared.domain.entity.ReimbursementStatus
import com.collicode.common.util.AuditInfo
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

// ================= COMMAND DTOs =================

data class MarkReimbursementPaidCommand(
    @field:NotNull(message = "Reimbursement ID is required")
    @field:Positive(message = "Reimbursement ID must be positive")
    val reimbursementId: Long,

    val auditInfo: AuditInfo? = null
)

data class ListReimbursementsQuery(
    val status: ReimbursementStatus? = null
)

// ================= RESPONSE DTOs =================

data class ReimbursementDto(
    val id: Long,
    val expenseId: String,
    val amount: BigDecimal,
    val status: ReimbursementStatus,
    val method: ReimbursementMethod,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val paidAt: LocalDateTime?,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime
)
