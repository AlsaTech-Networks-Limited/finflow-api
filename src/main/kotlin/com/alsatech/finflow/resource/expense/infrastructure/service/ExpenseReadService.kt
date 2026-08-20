package com.alsatech.finflow.resource.expense.infrastructure.service

import com.alsatech.finflow.resource.expense.dto.ExpenseDetailDto
import com.alsatech.finflow.resource.expense.dto.ExpenseDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Infrastructure service for expense read operations.
 *
 * This service handles data retrieval for queries.
 * It is called by query handlers.
 *
 * Responsibilities:
 * - Fetch data from repositories
 * - Map domain entities to DTOs
 * - Aggregate data from multiple sources (e.g., expense + approvals)
 *
 * Follows CQRS pattern - read operations only (no state changes).
 */
interface ExpenseReadService {
    fun fetchExpenseById(expenseId: String): Mono<ExpenseDetailDto>
    fun fetchAllExpenses(queryParams: Map<String, String>): Flux<ExpenseDto>
}
