package com.alsatech.finflow.resource.reimbursement.api

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Service
class ReimbursementApiResource {

    @Bean(name = ["reimbursementApiRoute"])
    fun routes(reimbursementApiHandler: ReimbursementApiHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route(POST(REIMBURSEMENT_MARK_PAID).and(accept(MediaType.APPLICATION_JSON)), reimbursementApiHandler::markReimbursementPaid)
            .andRoute(GET(REIMBURSEMENT_BY_ID).and(accept(MediaType.APPLICATION_JSON)), reimbursementApiHandler::fetchReimbursementById)
            .andRoute(GET(REIMBURSEMENT_BASE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), reimbursementApiHandler::fetchReimbursements)
    }
}
