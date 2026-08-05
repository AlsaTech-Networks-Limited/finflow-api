package com.alsatech.finflow.shared.domain.repository

import com.alsatech.finflow.shared.domain.entity.User
import reactor.core.publisher.Mono

/**
 * Repository interface for User entity.
 * Defines data access contract owned by the domain.
 */
interface UserRepository {
    fun findById(id: Long): Mono<User>
    fun findByEmail(email: String): Mono<User>
    fun save(user: User): Mono<User>
}
