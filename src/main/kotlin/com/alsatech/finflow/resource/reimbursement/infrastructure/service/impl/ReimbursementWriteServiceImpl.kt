package com.alsatech.finflow.resource.reimbursement.infrastructure.service.impl

import com.alsatech.finflow.resource.reimbursement.dto.MarkReimbursementPaidCommand
import com.alsatech.finflow.resource.reimbursement.infrastructure.service.ReimbursementWriteService
import com.alsatech.finflow.shared.domain.repository.ReimbursementRepository
import com.collicode.common.util.asUnit
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ReimbursementWriteServiceImpl(
    private val reimbursementRepository: ReimbursementRepository
) : ReimbursementWriteService {

    override fun markAsPaid(command: MarkReimbursementPaidCommand): Mono<Unit> {
        return reimbursementRepository.markAsPaid(command.reimbursementId)
            .asUnit()
    }
}
