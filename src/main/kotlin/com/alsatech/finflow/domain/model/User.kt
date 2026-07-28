package com.alsatech.finflow.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

enum class Role { USER, MANAGER, ADMIN }

@Table("users")
data class User(
    @Id val id: UUID? = null,
    val companyId: UUID,
    val email: String,
    val passwordHash: String,
    val role: Role,
    val createdAt: Instant = Instant.now(),
)
