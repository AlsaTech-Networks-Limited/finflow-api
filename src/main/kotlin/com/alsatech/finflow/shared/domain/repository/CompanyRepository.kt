package com.alsatech.finflow.shared.domain.repository

import com.alsatech.finflow.shared.domain.entity.Company
import reactor.core.publisher.Mono

/**
 * Repository interface for Company entity.
 * Defines data access contract owned by the domain.
 */
interface CompanyRepository {
    fun findById(id: Long): Mono<Company>
    fun save(company: Company): Mono<Company>
}
