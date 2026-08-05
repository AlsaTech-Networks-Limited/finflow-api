package com.alsatech.finflow.shared.infrastructure.persistence

import com.alsatech.finflow.shared.domain.entity.Category
import com.alsatech.finflow.shared.domain.repository.CategoryRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class CategoryRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : CategoryRepository {

    override fun findByCompanyId(companyId: Long): Flux<Category> {
        val query = Query.query(Criteria.where("company_id").`is`(companyId))
        return template.select(query, Category::class.java)
    }

    override fun save(category: Category): Mono<Category> {
        return if (category.id == null) {
            template.insert(Category::class.java).using(category)
        } else {
            template.update(category)
        }
    }
}
