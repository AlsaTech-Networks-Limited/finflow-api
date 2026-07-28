package com.alsatech.finflow.domain.repository

import com.alsatech.finflow.domain.model.Company
import java.util.UUID

interface CompanyRepository {
    suspend fun findById(id: UUID): Company?
    suspend fun save(company: Company): Company
}
