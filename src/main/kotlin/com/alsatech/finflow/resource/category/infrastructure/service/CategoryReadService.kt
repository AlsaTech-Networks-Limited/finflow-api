package com.alsatech.finflow.resource.category.infrastructure.service

import com.alsatech.finflow.resource.category.dto.CategoryDto
import com.alsatech.finflow.resource.category.dto.CategoryTreeDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Infrastructure service for category read operations.
 */
interface CategoryReadService {
    fun fetchCategoryById(categoryId: Long): Mono<CategoryDto>
    fun fetchCategoriesByCompany(companyId: Long): Flux<CategoryDto>
    fun fetchCategoryTree(companyId: Long): Flux<CategoryTreeDto>
}
