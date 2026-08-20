package com.alsatech.finflow.resource.category.dto

import com.collicode.common.util.AuditInfo
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.*
import java.time.LocalDateTime

// ================= COMMAND DTOs (Requests) =================

data class CreateCategoryCommand(
    @field:NotNull(message = "Company ID is required")
    @field:Positive(message = "Company ID must be positive")
    val companyId: Long,

    @field:NotBlank(message = "Category name is required")
    @field:Size(min = 1, max = 250, message = "Category name must be between 1 and 250 characters")
    val name: String,

    @field:Positive(message = "Parent ID must be positive")
    val parentId: Long? = null,

    @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String? = null,

    val auditInfo: AuditInfo? = null
)

data class UpdateCategoryCommand(
    @field:NotNull(message = "Category ID is required")
    @field:Positive(message = "Category ID must be positive")
    val categoryId: Long,

    @field:Size(min = 1, max = 250, message = "Category name must be between 1 and 250 characters")
    val name: String?,

    @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String?,

    val auditInfo: AuditInfo? = null
)

data class DeleteCategoryCommand(
    @field:NotNull(message = "Category ID is required")
    @field:Positive(message = "Category ID must be positive")
    val categoryId: Long,

    val auditInfo: AuditInfo? = null
)

data class GetCategoryQuery(
    val categoryId: Long
)

data class ListCategoriesQuery(
    val companyId: Long,
    val parentId: Long? = null
)

// ================= RESPONSE DTOs =================

/**
 * Category response DTO.
 * Used for category list and detail endpoints.
 */
data class CategoryDto(
    val id: Long,
    val companyId: Long,
    val name: String,
    val parentId: Long?,
    val description: String?,
    val hasChildren: Boolean = false,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime
)

/**
 * Category tree node for hierarchical display.
 */
data class CategoryTreeDto(
    val id: Long,
    val companyId: Long,
    val name: String,
    val parentId: Long?,
    val description: String?,
    val children: List<CategoryTreeDto> = emptyList(),

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime
)
