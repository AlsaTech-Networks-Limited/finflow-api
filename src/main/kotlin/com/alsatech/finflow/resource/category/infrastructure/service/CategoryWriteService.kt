package com.alsatech.finflow.resource.category.infrastructure.service

import com.alsatech.finflow.resource.category.dto.CategoryDto
import com.alsatech.finflow.resource.category.dto.CreateCategoryCommand
import com.alsatech.finflow.resource.category.dto.DeleteCategoryCommand
import com.alsatech.finflow.resource.category.dto.UpdateCategoryCommand
import reactor.core.publisher.Mono

/**
 * Infrastructure service for category write operations.
 */
interface CategoryWriteService {
    fun createCategory(command: CreateCategoryCommand): Mono<CategoryDto>
    fun updateCategory(command: UpdateCategoryCommand): Mono<CategoryDto>
    fun deleteCategory(command: DeleteCategoryCommand): Mono<Unit>
}
