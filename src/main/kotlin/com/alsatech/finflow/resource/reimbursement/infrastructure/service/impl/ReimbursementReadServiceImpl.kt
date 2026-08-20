package com.alsatech.finflow.resource.reimbursement.infrastructure.service.impl

import com.alsatech.finflow.resource.reimbursement.dto.ListReimbursementsQuery
import com.alsatech.finflow.resource.reimbursement.dto.ReimbursementDto
import com.alsatech.finflow.resource.reimbursement.infrastructure.service.ReimbursementReadService
import com.alsatech.finflow.shared.domain.entity.Reimbursement
import com.alsatech.finflow.shared.domain.repository.ReimbursementRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ReimbursementReadServiceImpl(
    private val reimbursementRepository: ReimbursementRepository
) : ReimbursementReadService {

    override fun fetchReimbursementById(reimbursementId: Long): Mono<ReimbursementDto> {
        return reimbursementRepository.findById(reimbursementId)
            .map { it.toDto() }
    }

    override fun fetchReimbursements(query: ListReimbursementsQuery): Flux<ReimbursementDto> {
        return if (query.status != null) {
            reimbursementRepository.findByStatus(query.status)
        } else {
            reimbursementRepository.findAll()
        }.map { it.toDto() }
    }

    // ================= PRIVATE MAPPING FUNCTIONS =================

    private fun Reimbursement.toDto(): ReimbursementDto {
        return ReimbursementDto(
            id = this.id!!,
            expenseId = this.expenseId,
            amount = this.amount,
            status = this.status,
            method = this.method,
            paidAt = this.paidAt,
            createdAt = this.createdAt
        )
    }
}
