package com.alsatech.finflow.resource.account.api

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Service
class AccountApiResource {

    @Bean(name = ["accountApiRoute"])
    fun routes(accountApiHandler: AccountApiHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route(POST(BASE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), accountApiHandler::createAccount)
            .andRoute(GET(ACCOUNT_BY_ID).and(accept(MediaType.APPLICATION_JSON)), accountApiHandler::fetchAccountById)
            .andRoute(GET(BASE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), accountApiHandler::fetchAllAccounts)
    }
}
