package com.alsatech.finflow.resource.dashboard.api

import com.alsatech.finflow.resource.dashboard.infrastructure.service.DashboardService
import com.alsatech.finflow.shared.infrastructure.constants.FETCH_ALL
import com.collicode.common.api.wrapGetRequestApiResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Service
class DashboardApiHandler(
    private val dashboardService: DashboardService
) {
    companion object {
        private const val RESOURCE_NAME = "dashboard"
    }

    // -------------------------------------------------------------------
    // FETCH DASHBOARD
    // -------------------------------------------------------------------
    fun fetchDashboard(serverRequest: ServerRequest): Mono<ServerResponse> =
        wrapGetRequestApiResponse(
            resource = RESOURCE_NAME,
            action = FETCH_ALL,
            serverRequest = serverRequest
        ) { req, _ ->
            val companyId = req.queryParam("companyId")
                .orElseThrow { IllegalArgumentException("companyId query parameter is required") }
                .toLong()
            dashboardService.fetchDashboard(companyId)
        }
}
