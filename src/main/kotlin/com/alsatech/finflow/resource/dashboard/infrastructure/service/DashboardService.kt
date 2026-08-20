package com.alsatech.finflow.resource.dashboard.infrastructure.service

import com.alsatech.finflow.resource.dashboard.dto.DashboardDto
import reactor.core.publisher.Mono

/**
 * Service for fetching aggregated dashboard data.
 */
interface DashboardService {
    /**
     * Fetch aggregated dashboard summary for a company.
     */
    fun fetchDashboard(companyId: Long): Mono<DashboardDto>
}
