package com.alsatech.finflow.resource.dashboard.api

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Service
class DashboardApiResource {

    @Bean(name = ["dashboardApiRoute"])
    fun routes(dashboardApiHandler: DashboardApiHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route(GET(DASHBOARD_ROUTE).and(accept(MediaType.APPLICATION_JSON)), dashboardApiHandler::fetchDashboard)
    }
}
