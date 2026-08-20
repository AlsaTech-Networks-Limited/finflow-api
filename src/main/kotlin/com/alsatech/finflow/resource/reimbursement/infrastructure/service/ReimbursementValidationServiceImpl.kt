package com.alsatech.finflow.resource.reimbursement.infrastructure.service

import com.alsatech.finflow.resource.reimbursement.domain.service.ReimbursementValidationService
import com.alsatech.finflow.resource.reimbursement.dto.MarkReimbursementPaidCommand
import com.alsatech.finflow.shared.domain.entity.ReimbursementStatus
import com.alsatech.finflow.shared.domain.repository.ReimbursementRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ReimbursementValidationServiceImpl(
    private val reimbursementRepository: ReimbursementRepository
) : ReimbursementValidationService {

    override fun validateMarkAsPaid(command: MarkReimbursementPaidCommand): Mono<MarkReimbursementPaidCommand> {
        return Mono.just(command)
            .flatMap { validateReimbursementExists(it.reimbursementId).thenReturn(it) }
            .flatMap { validateReimbursementStatus(it.reimbursementId).thenReturn(it) }
    }

    // ================= PRIVATE VALIDATION HELPERS =================

    private fun validateReimbursementExists(reimbursementId: Long): Mono<Unit> {
        return reimbursementRepository.findById(reimbursementId)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Reimbursement with ID $reimbursementId not found")))
            .then(Mono.just(Unit))
    }

    private fun validateReimbursementStatus(reimbursementId: Long): Mono<Unit> {
        return reimbursementRepository.findById(reimbursementId)
            .flatMap { reimbursement ->
                if (reimbursement.status == ReimbursementStatus.PAID) {
                    Mono.error(IllegalArgumentException("Reimbursement has already been marked as paid"))
                } else {
                    Mono.just(Unit)
                }
            }
    }
}
