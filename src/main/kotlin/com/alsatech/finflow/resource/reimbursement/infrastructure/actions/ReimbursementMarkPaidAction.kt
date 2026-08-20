package com.alsatech.finflow.resource.reimbursement.infrastructure.actions

import com.alsatech.finflow.resource.reimbursement.domain.service.ReimbursementValidationService
import com.alsatech.finflow.resource.reimbursement.dto.MarkReimbursementPaidCommand
import com.alsatech.finflow.resource.reimbursement.infrastructure.actions.usecase.ReimbursementMarkPaidUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ReimbursementMarkPaidAction(
    private val useCase: ReimbursementMarkPaidUseCase,
    private val validationService: ReimbursementValidationService
) : ActionWorkFlowService<MarkReimbursementPaidCommand> {

    override fun validate(request: MarkReimbursementPaidCommand): Mono<MarkReimbursementPaidCommand> {
        return validationService.validateMarkAsPaid(request)
    }

    override fun processRequest(request: MarkReimbursementPaidCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
