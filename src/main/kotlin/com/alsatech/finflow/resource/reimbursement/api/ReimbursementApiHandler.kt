package com.alsatech.finflow.resource.reimbursement.api

import com.alsatech.finflow.resource.reimbursement.dto.ListReimbursementsQuery
import com.alsatech.finflow.resource.reimbursement.dto.MarkReimbursementPaidCommand
import com.alsatech.finflow.resource.reimbursement.infrastructure.actions.ReimbursementMarkPaidAction
import com.alsatech.finflow.resource.reimbursement.infrastructure.service.ReimbursementReadService
import com.alsatech.finflow.shared.domain.entity.ReimbursementStatus
import com.alsatech.finflow.shared.infrastructure.constants.*
import com.collicode.common.api.wrapGetRequestApiResponse
import com.collicode.common.api.wrapInFluxApiResponse
import com.collicode.common.api.wrapRequestWithBodyInApiResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Service
class ReimbursementApiHandler(
    private val reimbursementMarkPaidAction: ReimbursementMarkPaidAction,
    private val reimbursementReadService: ReimbursementReadService
) {
    companion object {
        private const val RESOURCE_NAME = "reimbursement"
    }

    // -------------------------------------------------------------------
    // MARK PAID
    // -------------------------------------------------------------------
    fun markReimbursementPaid(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = UPDATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo, req ->
            val reimbursementId = req.pathVariable("reimbursementId").toLong()
            val command = MarkReimbursementPaidCommand(
                reimbursementId = reimbursementId,
                auditInfo = auditInfo
            )
            reimbursementMarkPaidAction.processRequest(command)
        }

    // -------------------------------------------------------------------
    // FETCH BY ID
    // -------------------------------------------------------------------
    fun fetchReimbursementById(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapGetRequestApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_BY_ID,
            serverRequest = serverRequest
        ) { req, _ ->
            val reimbursementId = req.pathVariable("reimbursementId").toLong()
            reimbursementReadService.fetchReimbursementById(reimbursementId)
        }

    // -------------------------------------------------------------------
    // FETCH ALL / BY STATUS
    // -------------------------------------------------------------------
    fun fetchReimbursements(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapInFluxApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_ALL,
            serverRequest = serverRequest
        ) { req, _ ->
            val status = req.queryParam("status")
                .map { ReimbursementStatus.valueOf(it) }
                .orElse(null)

            val query = ListReimbursementsQuery(status = status)
            reimbursementReadService.fetchReimbursements(query)
        }
}
