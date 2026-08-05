package com.alsatech.finflow.shared.domain

import reactor.core.publisher.Mono

interface UserRepository {
    fun findById(id: Long): Mono<User>
    fun findByEmail(email: String): Mono<User>
    fun save(user: User): Mono<User>
}
