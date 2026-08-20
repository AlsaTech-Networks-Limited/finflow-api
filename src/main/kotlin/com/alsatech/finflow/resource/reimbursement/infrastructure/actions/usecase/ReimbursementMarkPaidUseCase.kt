package com.alsatech.finflow.resource.reimbursement.infrastructure.actions.usecase

import com.alsatech.finflow.resource.reimbursement.dto.MarkReimbursementPaidCommand
import com.alsatech.finflow.resource.reimbursement.infrastructure.service.ReimbursementWriteService
import com.collicode.common.service.actions.ActionWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ReimbursementMarkPaidUseCase(
    private val writeService: ReimbursementWriteService
) : ActionWriteService<MarkReimbursementPaidCommand, Unit> {

    override fun executeAction(request: MarkReimbursementPaidCommand): Mono<Unit> {
        return writeService.markAsPaid(request)
    }

    override fun logAction(request: MarkReimbursementPaidCommand): Mono<Unit> {
        return Mono.empty()
    }
}
