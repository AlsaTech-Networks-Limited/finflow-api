package com.alsatech.finflow.resource.transaction.api

import com.alsatech.finflow.resource.transaction.dto.CreateTransactionCommand
import com.alsatech.finflow.resource.transaction.dto.ImportTransactionsCommand
import com.alsatech.finflow.resource.transaction.dto.ReconcileTransactionCommand
import com.alsatech.finflow.resource.transaction.infrastructure.actions.TransactionCreateAction
import com.alsatech.finflow.resource.transaction.infrastructure.actions.TransactionImportAction
import com.alsatech.finflow.resource.transaction.infrastructure.actions.TransactionReconcileAction
import com.alsatech.finflow.resource.transaction.infrastructure.service.TransactionReadService
import com.alsatech.finflow.shared.infrastructure.constants.CREATE
import com.alsatech.finflow.shared.infrastructure.constants.FETCH_BY_ID
import com.alsatech.finflow.shared.infrastructure.constants.FETCH_ALL
import com.alsatech.finflow.shared.infrastructure.constants.UPDATE
import com.collicode.common.api.wrapGetRequestApiResponse
import com.collicode.common.api.wrapInFluxApiResponse
import com.collicode.common.api.wrapRequestWithBodyInApiResponse
import com.collicode.common.dto.ApiRequest
import com.collicode.common.util.toObject
import com.google.gson.reflect.TypeToken
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Service
class TransactionApiHandler(
    private val transactionCreateAction: TransactionCreateAction,
    private val transactionImportAction: TransactionImportAction,
    private val transactionReconcileAction: TransactionReconcileAction,
    private val transactionReadService: TransactionReadService
) {
    companion object {
        private const val RESOURCE_NAME = "transaction"
    }

    // -------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------
    fun createTransaction(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = CREATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo ->
            val type = object : TypeToken<ApiRequest<CreateTransactionCommand>>() {}.type
            val request: ApiRequest<CreateTransactionCommand> = toObject(requestBody, type)
            transactionCreateAction.processRequest(request.payload.copy(auditInfo = auditInfo))
        }

    // -------------------------------------------------------------------
    // IMPORT
    // -------------------------------------------------------------------
    fun importTransactions(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = CREATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo ->
            val type = object : TypeToken<ApiRequest<ImportTransactionsCommand>>() {}.type
            val request: ApiRequest<ImportTransactionsCommand> = toObject(requestBody, type)
            transactionImportAction.processRequest(request.payload.copy(auditInfo = auditInfo))
        }

    // -------------------------------------------------------------------
    // RECONCILE
    // -------------------------------------------------------------------
    fun reconcileTransaction(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = UPDATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo, req ->
            val type = object : TypeToken<ApiRequest<ReconcileTransactionCommand>>() {}.type
            val request: ApiRequest<ReconcileTransactionCommand> = toObject(requestBody, type)
            val transactionId = req.pathVariable("transactionId").toLong()
            val commandWithId = request.payload.copy(
                transactionId = transactionId,
                auditInfo = auditInfo
            )
            transactionReconcileAction.processRequest(commandWithId)
        }

    // -------------------------------------------------------------------
    // FETCH BY ID
    // -------------------------------------------------------------------
    fun fetchTransactionById(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapGetRequestApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_BY_ID,
            serverRequest = serverRequest
        ) { req, _ ->
            val transactionId = req.pathVariable("transactionId").toLong()
            transactionReadService.fetchTransactionById(transactionId)
        }

    // -------------------------------------------------------------------
    // FETCH ALL
    // -------------------------------------------------------------------
    fun fetchAllTransactions(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapInFluxApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_ALL,
            serverRequest = serverRequest
        ) { req, _ ->
            val queryParams = req.queryParams().toSingleValueMap()
            val accountId = queryParams["accountId"]?.toLongOrNull()
            val companyId = queryParams["companyId"]?.toLongOrNull()

            when {
                accountId != null -> transactionReadService.fetchTransactionsByAccount(accountId)
                companyId != null -> transactionReadService.fetchTransactionsByCompany(companyId)
                else -> throw IllegalArgumentException("Either accountId or companyId query parameter is required")
            }
        }
}
