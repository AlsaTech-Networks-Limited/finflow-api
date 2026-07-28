package com.alsatech.finflow.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("companies")
data class Company(
    @Id val id: UUID? = null,
    val name: String,
    val plan: String,
    val createdAt: Instant = Instant.now(),
)
