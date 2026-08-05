package com.alsatech.finflow.shared.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("companies")
data class Company(
    @Id val id: Long? = null,
    val name: String,
    val createdAt: Instant = Instant.now(),
)
