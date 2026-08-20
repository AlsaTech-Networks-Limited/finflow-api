package com.alsatech.finflow.resource.transaction.api

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Service
class TransactionApiResource {

    @Bean(name = ["transactionApiRoute"])
    fun routes(transactionApiHandler: TransactionApiHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route(POST(BASE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), transactionApiHandler::createTransaction)
            .andRoute(POST(TRANSACTION_IMPORT).and(accept(MediaType.APPLICATION_JSON)), transactionApiHandler::importTransactions)
            .andRoute(POST(TRANSACTION_RECONCILE).and(accept(MediaType.APPLICATION_JSON)), transactionApiHandler::reconcileTransaction)
            .andRoute(GET(TRANSACTION_BY_ID).and(accept(MediaType.APPLICATION_JSON)), transactionApiHandler::fetchTransactionById)
            .andRoute(GET(BASE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), transactionApiHandler::fetchAllTransactions)
    }
}
