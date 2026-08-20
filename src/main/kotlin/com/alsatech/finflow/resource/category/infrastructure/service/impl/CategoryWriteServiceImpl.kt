package com.alsatech.finflow.resource.category.infrastructure.service.impl

import com.alsatech.finflow.resource.category.dto.CategoryDto
import com.alsatech.finflow.resource.category.dto.CreateCategoryCommand
import com.alsatech.finflow.resource.category.dto.DeleteCategoryCommand
import com.alsatech.finflow.resource.category.dto.UpdateCategoryCommand
import com.alsatech.finflow.resource.category.infrastructure.service.CategoryWriteService
import com.alsatech.finflow.shared.domain.entity.Category
import com.alsatech.finflow.shared.domain.repository.CategoryRepository
import com.collicode.common.exception.BusinessException
import com.collicode.common.util.asUnit
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class CategoryWriteServiceImpl(
    private val categoryRepository: CategoryRepository
) : CategoryWriteService {

    override fun createCategory(command: CreateCategoryCommand): Mono<CategoryDto> {
        val now = LocalDateTime.now()

        val category = Category(
            id = null,
            companyId = command.companyId,
            name = command.name,
            parentId = command.parentId,
            description = command.description,
            createdAt = now
        )

        return categoryRepository.save(category)
            .map { savedCategory ->
                CategoryDto(
                    id = savedCategory.id!!,
                    companyId = savedCategory.companyId,
                    name = savedCategory.name,
                    parentId = savedCategory.parentId,
                    description = savedCategory.description,
                    hasChildren = false,
                    createdAt = savedCategory.createdAt
                )
            }
    }

    override fun updateCategory(command: UpdateCategoryCommand): Mono<CategoryDto> {
        return categoryRepository.findById(command.categoryId)
            .switchIfEmpty(Mono.error(BusinessException.exception(
                "CATEGORY_NOT_FOUND",
                "Category not found with ID: ${command.categoryId}"
            )))
            .flatMap { category ->
                val updatedCategory = category.copy(
                    name = command.name ?: category.name,
                    description = command.description ?: category.description
                )

                categoryRepository.save(updatedCategory)
                    .map { savedCategory ->
                        CategoryDto(
                            id = savedCategory.id!!,
                            companyId = savedCategory.companyId,
                            name = savedCategory.name,
                            parentId = savedCategory.parentId,
                            description = savedCategory.description,
                            hasChildren = false,
                            createdAt = savedCategory.createdAt
                        )
                    }
            }
    }

    override fun deleteCategory(command: DeleteCategoryCommand): Mono<Unit> {
        return categoryRepository.deleteById(command.categoryId)
            .asUnit()
    }
}
