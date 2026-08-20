package com.alsatech.finflow.resource.expense.api

import com.alsatech.finflow.resource.expense.infrastructure.actions.ExpenseApproveAction
import com.alsatech.finflow.resource.expense.infrastructure.actions.ExpenseRejectAction
import com.alsatech.finflow.resource.expense.infrastructure.actions.ExpenseSubmitAction
import com.alsatech.finflow.resource.expense.dto.ApproveExpenseCommand
import com.alsatech.finflow.resource.expense.dto.RejectExpenseCommand
import com.alsatech.finflow.resource.expense.dto.SubmitExpenseCommand
import com.alsatech.finflow.resource.expense.infrastructure.service.ExpenseReadService
import com.alsatech.finflow.shared.infrastructure.constants.CREATE
import com.alsatech.finflow.shared.infrastructure.constants.UPDATE
import com.alsatech.finflow.shared.infrastructure.constants.FETCH_BY_ID
import com.alsatech.finflow.shared.infrastructure.constants.FETCH_ALL
import com.collicode.common.api.wrapRequestWithBodyInApiResponse
import com.collicode.common.api.wrapGetRequestApiResponse
import com.collicode.common.api.wrapInFluxApiResponse
import com.collicode.common.dto.ApiRequest
import com.collicode.common.util.toObject
import com.google.gson.reflect.TypeToken
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Service
class ExpenseApiHandler(
    private val expenseSubmitAction: ExpenseSubmitAction,
    private val expenseApproveAction: ExpenseApproveAction,
    private val expenseRejectAction: ExpenseRejectAction,
    private val expenseReadService: ExpenseReadService
) {
    companion object {
        private const val RESOURCE_NAME = "expense"
    }

    // -------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------
    fun submitExpense(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = CREATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo ->
            val type = object : TypeToken<ApiRequest<SubmitExpenseCommand>>() {}.type
            val request: ApiRequest<SubmitExpenseCommand> = toObject(requestBody, type)
            expenseSubmitAction.processRequest(request.payload.copy(auditInfo = auditInfo))
        }


    // -------------------------------------------------------------------
    // APPROVE
    // -------------------------------------------------------------------
    fun approveExpense(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = UPDATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo, req ->
            val type = object : TypeToken<ApiRequest<ApproveExpenseCommand>>() {}.type
            val request: ApiRequest<ApproveExpenseCommand> = toObject(requestBody, type)
            val expenseId = req.pathVariable("expenseId")
            val commandWithId = request.payload.copy(
                expenseId = expenseId,
                auditInfo = auditInfo
            )
            expenseApproveAction.processRequest(commandWithId)
        }

    // -------------------------------------------------------------------
    // REJECT
    // -------------------------------------------------------------------
    fun rejectExpense(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = UPDATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo, req ->
            val type = object : TypeToken<ApiRequest<RejectExpenseCommand>>() {}.type
            val request: ApiRequest<RejectExpenseCommand> = toObject(requestBody, type)
            val expenseId = req.pathVariable("expenseId")
            val commandWithId = request.payload.copy(
                expenseId = expenseId,
                auditInfo = auditInfo
            )
            expenseRejectAction.processRequest(commandWithId)
        }

    // -------------------------------------------------------------------
    // FETCH BY ID
    // -------------------------------------------------------------------
    fun fetchExpenseById(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapGetRequestApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_BY_ID,
            serverRequest = serverRequest
        ) { req, _ ->
            val expenseId = req.pathVariable("expenseId")
            expenseReadService.fetchExpenseById(expenseId)
        }

    // -------------------------------------------------------------------
    // FETCH ALL
    // -------------------------------------------------------------------
    fun fetchAllExpenses(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapInFluxApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_ALL,
            serverRequest = serverRequest
        ) { req, _ ->
            val queryParams = req.queryParams().toSingleValueMap()
            expenseReadService.fetchAllExpenses(queryParams)
        }
}