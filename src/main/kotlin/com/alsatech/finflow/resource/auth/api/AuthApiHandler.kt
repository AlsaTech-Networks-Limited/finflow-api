package com.alsatech.finflow.resource.auth.api

import com.alsatech.finflow.resource.auth.dto.LoginCommand
import com.alsatech.finflow.resource.auth.dto.RegisterCommand
import com.alsatech.finflow.resource.auth.infrastructure.service.AuthService
import com.alsatech.finflow.shared.infrastructure.constants.CREATE
import com.collicode.common.api.wrapRequestWithBodyInApiResponse
import com.collicode.common.dto.ApiRequest
import com.collicode.common.util.toObject
import com.google.gson.reflect.TypeToken
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

// Uses the same {meta, payload} envelope + wrapRequestWithBodyInApiResponse
// helper as every other resource (Expense/Account/Invoice/...) — the
// original version of this handler hand-built a response with ApiResponse/
// MetaInfo classes that don't exist in the com.collicode:common jar actually
// on the classpath, and expected a raw (unwrapped) request body, inconsistent
// with the rest of the API. Standardized here so one envelope client on the
// frontend covers every endpoint, auth included.
@Service
class AuthApiHandler(
    private val authService: AuthService
) {
    companion object {
        private const val RESOURCE_NAME = "auth"
    }

    // -------------------------------------------------------------------
    // LOGIN
    // -------------------------------------------------------------------
    fun login(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = CREATE,
            serverRequest = serverRequest
        ) { requestBody, _ ->
            val type = object : TypeToken<ApiRequest<LoginCommand>>() {}.type
            val request: ApiRequest<LoginCommand> = toObject(requestBody, type)
            authService.login(request.payload)
        }

    // -------------------------------------------------------------------
    // REGISTER
    // -------------------------------------------------------------------
    fun register(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = CREATE,
            serverRequest = serverRequest
        ) { requestBody, _ ->
            val type = object : TypeToken<ApiRequest<RegisterCommand>>() {}.type
            val request: ApiRequest<RegisterCommand> = toObject(requestBody, type)
            authService.register(request.payload)
        }
}
