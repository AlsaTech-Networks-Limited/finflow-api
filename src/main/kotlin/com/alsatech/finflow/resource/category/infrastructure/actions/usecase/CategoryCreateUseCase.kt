package com.alsatech.finflow.resource.category.infrastructure.actions.usecase

import com.alsatech.finflow.resource.category.dto.CategoryDto
import com.alsatech.finflow.resource.category.dto.CreateCategoryCommand
import com.alsatech.finflow.resource.category.infrastructure.service.CategoryWriteService
import com.collicode.common.service.actions.ActionWriteService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CategoryCreateUseCase(
    private val categoryWriteService: CategoryWriteService
) : ActionWriteService<CreateCategoryCommand, CategoryDto> {

    override fun executeAction(request: CreateCategoryCommand): Mono<CategoryDto> {
        return categoryWriteService.createCategory(request)
    }

    override fun logAction(request: CreateCategoryCommand): Mono<CategoryDto> {
        return Mono.empty()
    }
}
