package com.alsatech.finflow.shared.domain.repository

import com.alsatech.finflow.shared.domain.entity.Category
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Repository interface for Category entity.
 * Defines data access contract owned by the domain.
 */
interface CategoryRepository {
    fun findByCompanyId(companyId: Long): Flux<Category>
    fun save(category: Category): Mono<Category>
}
