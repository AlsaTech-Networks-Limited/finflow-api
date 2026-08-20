package com.alsatech.finflow.resource.auth.api

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Service
class AuthApiResource {

    @Bean(name = ["authApiRoute"])
    fun routes(authApiHandler: AuthApiHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route(POST(AUTH_LOGIN).and(accept(MediaType.APPLICATION_JSON)), authApiHandler::login)
            .andRoute(POST(AUTH_REGISTER).and(accept(MediaType.APPLICATION_JSON)), authApiHandler::register)
    }
}
