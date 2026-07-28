package com.alsatech.finflow.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("categories")
data class Category(
    @Id val id: UUID? = null,
    val companyId: UUID,
    val name: String,
    val parentId: UUID? = null,
)
