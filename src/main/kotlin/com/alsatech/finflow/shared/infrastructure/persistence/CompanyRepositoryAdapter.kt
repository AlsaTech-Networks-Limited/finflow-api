package com.alsatech.finflow.shared.infrastructure.persistence

import com.alsatech.finflow.shared.domain.Company
import com.alsatech.finflow.shared.domain.CompanyRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class CompanyRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : CompanyRepository {

    override fun findById(id: Long): Mono<Company> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.selectOne(query, Company::class.java)
    }

    override fun save(company: Company): Mono<Company> {
        return if (company.id == null) {
            template.insert(Company::class.java).using(company)
        } else {
            template.update(company)
        }
    }
}
