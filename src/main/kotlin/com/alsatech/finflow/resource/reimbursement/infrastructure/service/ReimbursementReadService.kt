package com.alsatech.finflow.resource.reimbursement.infrastructure.service

import com.alsatech.finflow.resource.reimbursement.dto.ListReimbursementsQuery
import com.alsatech.finflow.resource.reimbursement.dto.ReimbursementDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Read service for reimbursement queries.
 */
interface ReimbursementReadService {
    fun fetchReimbursementById(reimbursementId: Long): Mono<ReimbursementDto>
    fun fetchReimbursements(query: ListReimbursementsQuery): Flux<ReimbursementDto>
}
