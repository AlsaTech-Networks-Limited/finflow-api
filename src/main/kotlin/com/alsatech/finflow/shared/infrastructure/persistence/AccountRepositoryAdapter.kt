package com.alsatech.finflow.shared.infrastructure.persistence

import com.alsatech.finflow.shared.domain.entity.Account
import com.alsatech.finflow.shared.domain.repository.AccountRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class AccountRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : AccountRepository {

    override fun findById(id: Long): Mono<Account> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.selectOne(query, Account::class.java)
    }

    override fun findByCompanyId(companyId: Long): Flux<Account> {
        val query = Query.query(Criteria.where("company_id").`is`(companyId))
        return template.select(query, Account::class.java)
    }

    override fun save(account: Account): Mono<Account> {
        return if (account.id == null) {
            template.insert(Account::class.java).using(account)
        } else {
            template.update(account)
        }
    }
}
