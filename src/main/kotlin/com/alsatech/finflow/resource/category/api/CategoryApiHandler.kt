package com.alsatech.finflow.resource.category.api

import com.alsatech.finflow.resource.category.dto.CreateCategoryCommand
import com.alsatech.finflow.resource.category.infrastructure.actions.CategoryCreateAction
import com.alsatech.finflow.resource.category.infrastructure.service.CategoryReadService
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
class CategoryApiHandler(
    private val categoryCreateAction: CategoryCreateAction,
    private val categoryReadService: CategoryReadService
) {
    companion object {
        private const val RESOURCE_NAME = "category"
    }

    // -------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------
    fun createCategory(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = CREATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo ->
            val type = object : TypeToken<ApiRequest<CreateCategoryCommand>>() {}.type
            val request: ApiRequest<CreateCategoryCommand> = toObject(requestBody, type)
            categoryCreateAction.processRequest(request.payload.copy(auditInfo = auditInfo))
        }

    // -------------------------------------------------------------------
    // FETCH BY ID
    // -------------------------------------------------------------------
    fun fetchCategoryById(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapGetRequestApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_BY_ID,
            serverRequest = serverRequest
        ) { req, _ ->
            val categoryId = req.pathVariable("categoryId").toLong()
            categoryReadService.fetchCategoryById(categoryId)
        }

    // -------------------------------------------------------------------
    // FETCH ALL
    // -------------------------------------------------------------------
    fun fetchAllCategories(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapInFluxApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_ALL,
            serverRequest = serverRequest
        ) { req, _ ->
            val companyId = req.queryParam("companyId")
                .orElseThrow { IllegalArgumentException("companyId query parameter is required") }
                .toLong()
            categoryReadService.fetchCategoriesByCompany(companyId)
        }

    // -------------------------------------------------------------------
    // FETCH TREE
    // -------------------------------------------------------------------
    fun fetchCategoryTree(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapInFluxApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_ALL,
            serverRequest = serverRequest
        ) { req, _ ->
            val companyId = req.queryParam("companyId")
                .orElseThrow { IllegalArgumentException("companyId query parameter is required") }
                .toLong()
            categoryReadService.fetchCategoryTree(companyId)
        }
}
