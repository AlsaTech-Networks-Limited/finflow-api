package com.alsatech.finflow.domain.repository

import com.alsatech.finflow.domain.model.Category
import java.util.UUID

interface CategoryRepository {
    suspend fun findByCompanyId(companyId: UUID): List<Category>
    suspend fun save(category: Category): Category
}
