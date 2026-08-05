package com.alsatech.finflow.shared.infrastructure.persistence

import com.alsatech.finflow.shared.domain.User
import com.alsatech.finflow.shared.domain.UserRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class UserRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : UserRepository {

    override fun findById(id: Long): Mono<User> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.selectOne(query, User::class.java)
    }

    override fun findByEmail(email: String): Mono<User> {
        val query = Query.query(Criteria.where("email").`is`(email))
        return template.selectOne(query, User::class.java)
    }

    override fun save(user: User): Mono<User> {
        return if (user.id == null) {
            template.insert(User::class.java).using(user)
        } else {
            template.update(user)
        }
    }
}
