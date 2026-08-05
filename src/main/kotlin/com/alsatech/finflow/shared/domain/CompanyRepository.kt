package com.alsatech.finflow.shared.domain

import reactor.core.publisher.Mono

interface CompanyRepository {
    fun findById(id: Long): Mono<Company>
    fun save(company: Company): Mono<Company>
}
