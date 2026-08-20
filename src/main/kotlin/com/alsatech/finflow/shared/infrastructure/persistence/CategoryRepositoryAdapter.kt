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

    override fun findById(id: Long): Mono<Category> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.selectOne(query, Category::class.java)
    }

    override fun findByCompanyId(companyId: Long): Flux<Category> {
        val query = Query.query(Criteria.where("company_id").`is`(companyId))
        return template.select(query, Category::class.java)
    }

    override fun findByParentId(parentId: Long): Flux<Category> {
        val query = Query.query(Criteria.where("parent_id").`is`(parentId))
        return template.select(query, Category::class.java)
    }

    override fun save(category: Category): Mono<Category> {
        return if (category.id == null) {
            template.insert(Category::class.java).using(category)
        } else {
            template.update(category)
        }
    }

    override fun deleteById(id: Long): Mono<Void> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.delete(query, Category::class.java).then()
    }
}
