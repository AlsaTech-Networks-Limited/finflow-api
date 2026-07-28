package com.alsatech.finflow.domain.repository

import com.alsatech.finflow.domain.model.Invoice
import java.util.UUID

interface InvoiceRepository {
    suspend fun findByCompanyId(companyId: UUID): List<Invoice>
    suspend fun save(invoice: Invoice): Invoice
}
