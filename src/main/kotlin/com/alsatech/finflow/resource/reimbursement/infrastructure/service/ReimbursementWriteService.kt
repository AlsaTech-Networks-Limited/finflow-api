package com.alsatech.finflow.resource.reimbursement.infrastructure.service

import com.alsatech.finflow.resource.reimbursement.dto.MarkReimbursementPaidCommand
import reactor.core.publisher.Mono

/**
 * Write service for reimbursement mutations.
 */
interface ReimbursementWriteService {
    fun markAsPaid(command: MarkReimbursementPaidCommand): Mono<Unit>
}
