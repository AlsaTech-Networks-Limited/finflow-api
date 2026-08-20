package com.alsatech.finflow.shared.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("categories")
data class Category(
    @Id val id: Long? = null,
    val companyId: Long,
    val name: String,
    val parentId: Long? = null,
    val description: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
