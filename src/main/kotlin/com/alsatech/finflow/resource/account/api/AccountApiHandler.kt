package com.alsatech.finflow.resource.account.api

import com.alsatech.finflow.resource.account.dto.CreateAccountCommand
import com.alsatech.finflow.resource.account.infrastructure.actions.AccountCreateAction
import com.alsatech.finflow.resource.account.infrastructure.service.AccountReadService
import com.alsatech.finflow.shared.infrastructure.constants.CREATE
import com.alsatech.finflow.shared.infrastructure.constants.FETCH_BY_ID
import com.alsatech.finflow.shared.infrastructure.constants.FETCH_ALL
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
class AccountApiHandler(
    private val accountCreateAction: AccountCreateAction,
    private val accountReadService: AccountReadService
) {
    companion object {
        private const val RESOURCE_NAME = "account"
    }

    // -------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------
    fun createAccount(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = CREATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo ->
            val type = object : TypeToken<ApiRequest<CreateAccountCommand>>() {}.type
            val request: ApiRequest<CreateAccountCommand> = toObject(requestBody, type)
            accountCreateAction.processRequest(request.payload.copy(auditInfo = auditInfo))
        }

    // -------------------------------------------------------------------
    // FETCH BY ID
    // -------------------------------------------------------------------
    fun fetchAccountById(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapGetRequestApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_BY_ID,
            serverRequest = serverRequest
        ) { req, _ ->
            val accountId = req.pathVariable("accountId").toLong()
            accountReadService.fetchAccountById(accountId)
        }

    // -------------------------------------------------------------------
    // FETCH ALL
    // -------------------------------------------------------------------
    fun fetchAllAccounts(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapInFluxApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_ALL,
            serverRequest = serverRequest
        ) { req, _ ->
            val companyId = req.queryParam("companyId")
                .orElseThrow { IllegalArgumentException("companyId query parameter is required") }
                .toLong()
            accountReadService.fetchAccountsByCompany(companyId)
        }
}
