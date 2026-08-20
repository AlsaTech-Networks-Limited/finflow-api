package com.alsatech.finflow.resource.category.domain.service

import com.alsatech.finflow.resource.category.dto.CreateCategoryCommand
import com.alsatech.finflow.resource.category.dto.DeleteCategoryCommand
import com.alsatech.finflow.resource.category.dto.UpdateCategoryCommand
import reactor.core.publisher.Mono

/**
 * Domain validation service for category-related commands.
 * Handles business rule validation beyond basic field constraints.
 */
interface CategoryValidationService {

    /**
     * Validates a create category command.
     * Checks business rules like parent category existence, naming conflicts, etc.
     */
    fun validateCreateCategory(command: CreateCategoryCommand): Mono<CreateCategoryCommand>

    /**
     * Validates an update category command.
     * Checks business rules like category existence, circular references, etc.
     */
    fun validateUpdateCategory(command: UpdateCategoryCommand): Mono<UpdateCategoryCommand>

    /**
     * Validates a delete category command.
     * Checks business rules like no child categories, no expenses using it, etc.
     */
    fun validateDeleteCategory(command: DeleteCategoryCommand): Mono<DeleteCategoryCommand>
}
