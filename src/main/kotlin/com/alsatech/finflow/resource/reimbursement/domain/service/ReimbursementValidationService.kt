package com.alsatech.finflow.resource.reimbursement.domain.service

import com.alsatech.finflow.resource.reimbursement.dto.MarkReimbursementPaidCommand
import reactor.core.publisher.Mono

/**
 * Domain service for reimbursement validation business rules.
 */
interface ReimbursementValidationService {
    fun validateMarkAsPaid(command: MarkReimbursementPaidCommand): Mono<MarkReimbursementPaidCommand>
}
