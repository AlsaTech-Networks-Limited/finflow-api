package com.alsatech.finflow.resource.category.infrastructure.service.impl

import com.alsatech.finflow.resource.category.dto.CategoryDto
import com.alsatech.finflow.resource.category.dto.CategoryTreeDto
import com.alsatech.finflow.resource.category.infrastructure.service.CategoryReadService
import com.alsatech.finflow.shared.domain.repository.CategoryRepository
import com.collicode.common.exception.BusinessException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CategoryReadServiceImpl(
    private val categoryRepository: CategoryRepository
) : CategoryReadService {

    override fun fetchCategoryById(categoryId: Long): Mono<CategoryDto> {
        return categoryRepository.findById(categoryId)
            .switchIfEmpty(Mono.error(BusinessException.exception(
                "CATEGORY_NOT_FOUND",
                "Category not found with ID: $categoryId"
            )))
            .flatMap { category ->
                categoryRepository.findByParentId(category.id!!)
                    .hasElements()
                    .map { hasChildren ->
                        CategoryDto(
                            id = category.id!!,
                            companyId = category.companyId,
                            name = category.name,
                            parentId = category.parentId,
                            description = category.description,
                            hasChildren = hasChildren,
                            createdAt = category.createdAt
                        )
                    }
            }
    }

    override fun fetchCategoriesByCompany(companyId: Long): Flux<CategoryDto> {
        return categoryRepository.findByCompanyId(companyId)
            .flatMap { category ->
                categoryRepository.findByParentId(category.id!!)
                    .hasElements()
                    .map { hasChildren ->
                        CategoryDto(
                            id = category.id!!,
                            companyId = category.companyId,
                            name = category.name,
                            parentId = category.parentId,
                            description = category.description,
                            hasChildren = hasChildren,
                            createdAt = category.createdAt
                        )
                    }
            }
    }

    override fun fetchCategoryTree(companyId: Long): Flux<CategoryTreeDto> {
        return categoryRepository.findByCompanyId(companyId)
            .filter { it.parentId == null } // Get root categories
            .flatMap { rootCategory ->
                buildCategoryTree(rootCategory.id!!)
            }
    }

    private fun buildCategoryTree(categoryId: Long): Mono<CategoryTreeDto> {
        return categoryRepository.findById(categoryId)
            .flatMap { category ->
                categoryRepository.findByParentId(category.id!!)
                    .flatMap { child -> buildCategoryTree(child.id!!) }
                    .collectList()
                    .map { children ->
                        CategoryTreeDto(
                            id = category.id!!,
                            companyId = category.companyId,
                            name = category.name,
                            parentId = category.parentId,
                            description = category.description,
                            children = children,
                            createdAt = category.createdAt
                        )
                    }
            }
    }
}
