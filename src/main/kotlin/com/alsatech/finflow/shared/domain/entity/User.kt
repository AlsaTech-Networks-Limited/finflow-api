package com.alsatech.finflow.shared.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

enum class Role { USER, MANAGER, ADMIN }

@Table("users")
data class User(
    @Id val id: Long? = null,
    val companyId: Long,
    val email: String,
    val passwordHash: String,
    val role: Role,
    val createdAt: Instant = Instant.now(),
)
