package com.alsatech.finflow.resource.invoice.api

import com.alsatech.finflow.resource.invoice.dto.*
import com.alsatech.finflow.resource.invoice.infrastructure.actions.*
import com.alsatech.finflow.resource.invoice.infrastructure.service.InvoiceReadService
import com.alsatech.finflow.shared.domain.entity.InvoiceStatus
import com.alsatech.finflow.shared.infrastructure.constants.*
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
class InvoiceApiHandler(
    private val invoiceCreateAction: InvoiceCreateAction,
    private val invoiceUpdateAction: InvoiceUpdateAction,
    private val invoiceSendAction: InvoiceSendAction,
    private val invoiceMarkPaidAction: InvoiceMarkPaidAction,
    private val invoiceCancelAction: InvoiceCancelAction,
    private val invoiceReadService: InvoiceReadService
) {
    companion object {
        private const val RESOURCE_NAME = "invoice"
    }

    // -------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------
    fun createInvoice(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = CREATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo ->
            val type = object : TypeToken<ApiRequest<CreateInvoiceCommand>>() {}.type
            val request: ApiRequest<CreateInvoiceCommand> = toObject(requestBody, type)
            invoiceCreateAction.processRequest(request.payload.copy(auditInfo = auditInfo))
        }

    // -------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------
    fun updateInvoice(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = UPDATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo, req ->
            val type = object : TypeToken<ApiRequest<UpdateInvoiceCommand>>() {}.type
            val request: ApiRequest<UpdateInvoiceCommand> = toObject(requestBody, type)
            val invoiceId = req.pathVariable("invoiceId").toLong()
            val commandWithId = request.payload.copy(
                invoiceId = invoiceId,
                auditInfo = auditInfo
            )
            invoiceUpdateAction.processRequest(commandWithId)
        }

    // -------------------------------------------------------------------
    // SEND
    // -------------------------------------------------------------------
    fun sendInvoice(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = UPDATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo, req ->
            val invoiceId = req.pathVariable("invoiceId").toLong()
            val command = SendInvoiceCommand(
                invoiceId = invoiceId,
                auditInfo = auditInfo
            )
            invoiceSendAction.processRequest(command)
        }

    // -------------------------------------------------------------------
    // MARK PAID
    // -------------------------------------------------------------------
    fun markInvoicePaid(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = UPDATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo, req ->
            val invoiceId = req.pathVariable("invoiceId").toLong()
            val command = MarkInvoicePaidCommand(
                invoiceId = invoiceId,
                auditInfo = auditInfo
            )
            invoiceMarkPaidAction.processRequest(command)
        }

    // -------------------------------------------------------------------
    // CANCEL
    // -------------------------------------------------------------------
    fun cancelInvoice(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapRequestWithBodyInApiResponse(
            resource = RESOURCE_NAME,
            action = UPDATE,
            serverRequest = serverRequest
        ) { requestBody, auditInfo, req ->
            val invoiceId = req.pathVariable("invoiceId").toLong()
            val command = CancelInvoiceCommand(
                invoiceId = invoiceId,
                auditInfo = auditInfo
            )
            invoiceCancelAction.processRequest(command)
        }

    // -------------------------------------------------------------------
    // FETCH BY ID
    // -------------------------------------------------------------------
    fun fetchInvoiceById(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapGetRequestApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_BY_ID,
            serverRequest = serverRequest
        ) { req, _ ->
            val invoiceId = req.pathVariable("invoiceId").toLong()
            invoiceReadService.fetchInvoiceById(invoiceId)
        }

    // -------------------------------------------------------------------
    // FETCH ALL BY COMPANY
    // -------------------------------------------------------------------
    fun fetchInvoicesByCompany(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapInFluxApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_ALL,
            serverRequest = serverRequest
        ) { req, _ ->
            val companyId = req.queryParam("companyId")
                .orElseThrow { IllegalArgumentException("companyId is required") }
                .toLong()
            val status = req.queryParam("status")
                .map { InvoiceStatus.valueOf(it) }
                .orElse(null)

            val query = ListInvoicesQuery(
                companyId = companyId,
                status = status
            )
            invoiceReadService.fetchInvoicesByCompany(query)
        }
}
