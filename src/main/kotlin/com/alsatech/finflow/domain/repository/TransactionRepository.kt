package com.alsatech.finflow.domain.repository

import com.alsatech.finflow.domain.model.Transaction
import java.util.UUID

interface TransactionRepository {
    suspend fun findByAccountId(accountId: UUID): List<Transaction>
    suspend fun save(transaction: Transaction): Transaction
    suspend fun saveAll(transactions: List<Transaction>): List<Transaction>
}
