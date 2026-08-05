package com.alsatech.finflow.shared.domain

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CategoryRepository {
    fun findByCompanyId(companyId: Long): Flux<Category>
    fun save(category: Category): Mono<Category>
}
