package com.alsatech.finflow.resource.category.infrastructure.service

import com.alsatech.finflow.resource.category.domain.service.CategoryValidationService
import com.alsatech.finflow.resource.category.dto.CreateCategoryCommand
import com.alsatech.finflow.resource.category.dto.DeleteCategoryCommand
import com.alsatech.finflow.resource.category.dto.UpdateCategoryCommand
import com.alsatech.finflow.shared.domain.repository.CategoryRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CategoryValidationServiceImpl(
    private val categoryRepository: CategoryRepository
) : CategoryValidationService {

    override fun validateCreateCategory(command: CreateCategoryCommand): Mono<CreateCategoryCommand> {
        return Mono.just(command)
            .flatMap { validateCategoryName(it.name).thenReturn(it) }
            .flatMap {
                if (it.parentId != null) validateParentExists(it.parentId).thenReturn(it)
                else Mono.just(it)
            }
    }

    override fun validateUpdateCategory(command: UpdateCategoryCommand): Mono<UpdateCategoryCommand> {
        return Mono.just(command)
            .flatMap { validateCategoryExists(it.categoryId).thenReturn(it) }
            .flatMap {
                if (it.name != null) validateCategoryName(it.name).thenReturn(it)
                else Mono.just(it)
            }
    }

    override fun validateDeleteCategory(command: DeleteCategoryCommand): Mono<DeleteCategoryCommand> {
        return Mono.just(command)
            .flatMap { validateCategoryExists(it.categoryId).thenReturn(it) }
            .flatMap { validateNoChildren(it.categoryId).thenReturn(it) }
    }

    // =================== Private Validation Helpers ===================

    private fun validateCategoryName(name: String): Mono<Unit> {
        return if (name.isBlank()) {
            Mono.error(IllegalArgumentException("Category name cannot be blank"))
        } else if (name.length > 250) {
            Mono.error(IllegalArgumentException("Category name must not exceed 250 characters"))
        } else {
            Mono.just(Unit)
        }
    }

    private fun validateParentExists(parentId: Long): Mono<Unit> {
        return categoryRepository.findById(parentId)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Parent category with ID $parentId not found")))
            .then(Mono.just(Unit))
    }

    private fun validateCategoryExists(categoryId: Long): Mono<Unit> {
        return categoryRepository.findById(categoryId)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Category with ID $categoryId not found")))
            .then(Mono.just(Unit))
    }

    private fun validateNoChildren(categoryId: Long): Mono<Unit> {
        return categoryRepository.findByParentId(categoryId)
            .hasElements()
            .flatMap { hasChildren ->
                if (hasChildren) {
                    Mono.error(IllegalStateException("Cannot delete category with child categories"))
                } else {
                    Mono.just(Unit)
                }
            }
    }
}
