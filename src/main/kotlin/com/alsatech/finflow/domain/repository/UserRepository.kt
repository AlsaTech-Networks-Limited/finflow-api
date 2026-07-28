package com.alsatech.finflow.domain.repository

import com.alsatech.finflow.domain.model.User
import java.util.UUID

interface UserRepository {
    suspend fun findById(id: UUID): User?
    suspend fun findByEmail(email: String): User?
    suspend fun save(user: User): User
}
