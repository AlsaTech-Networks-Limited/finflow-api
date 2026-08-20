package com.alsatech.finflow.resource.invoice.api

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Service
class InvoiceApiResource {

    @Bean(name = ["invoiceApiRoute"])
    fun routes(invoiceApiHandler: InvoiceApiHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route(POST(INVOICE_BASE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), invoiceApiHandler::createInvoice)
            .andRoute(PUT(INVOICE_BY_ID).and(accept(MediaType.APPLICATION_JSON)), invoiceApiHandler::updateInvoice)
            .andRoute(POST(INVOICE_SEND).and(accept(MediaType.APPLICATION_JSON)), invoiceApiHandler::sendInvoice)
            .andRoute(POST(INVOICE_MARK_PAID).and(accept(MediaType.APPLICATION_JSON)), invoiceApiHandler::markInvoicePaid)
            .andRoute(POST(INVOICE_CANCEL).and(accept(MediaType.APPLICATION_JSON)), invoiceApiHandler::cancelInvoice)
            .andRoute(GET(INVOICE_BY_ID).and(accept(MediaType.APPLICATION_JSON)), invoiceApiHandler::fetchInvoiceById)
            .andRoute(GET(INVOICE_BASE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), invoiceApiHandler::fetchInvoicesByCompany)
    }
}
