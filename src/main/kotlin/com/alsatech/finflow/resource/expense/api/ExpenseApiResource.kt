package com.alsatech.finflow.resource.expense.api

import com.alsatech.finflow.resource.expense.application.dto.*
import com.alsatech.finflow.resource.expense.application.usecase.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * REST Controller for Expense operations.
 *
 * Follows Clean Architecture principles:
 * - Controller receives requests
 * - Maps to Commands/Queries
 * - Delegates to Use Cases
 * - Returns DTOs
 *
 * HTTP Methods mapping:
 * - POST   /api/expenses             → Submit new expense
 * - POST   /api/expenses/{id}/approve → Approve expense
 * - POST   /api/expenses/{id}/reject  → Reject expense
 * - GET    /api/expenses/{id}        → Get expense details
 * - GET    /api/expenses             → List expenses (filtered)
 */
@RestController
@RequestMapping("/api/expenses")
class ExpenseController(
    private val submitExpenseUseCase: SubmitExpenseUseCase,
    private val approveExpenseUseCase: ApproveExpenseUseCase,
    private val rejectExpenseUseCase: RejectExpenseUseCase,
    private val getExpenseUseCase: GetExpenseUseCase,
    private val listExpensesUseCase: ListExpensesUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun submitExpense(
        @Valid @RequestBody command: SubmitExpenseCommand
    ): Mono<Map<String, Long>> {
        return submitExpenseUseCase.execute(command)
            .map { expenseId -> mapOf("expenseId" to expenseId) }
    }

    @PostMapping("/{expenseId}/approve")
    @ResponseStatus(HttpStatus.OK)
    fun approveExpense(
        @PathVariable expenseId: Long,
        @Valid @RequestBody command: ApproveExpenseCommand
    ): Mono<Void> {
        val commandWithId = command.copy(expenseId = expenseId)
        return approveExpenseUseCase.execute(commandWithId)
    }

    @PostMapping("/{expenseId}/reject")
    @ResponseStatus(HttpStatus.OK)
    fun rejectExpense(
        @PathVariable expenseId: Long,
        @Valid @RequestBody command: RejectExpenseCommand
    ): Mono<Void> {
        val commandWithId = command.copy(expenseId = expenseId)
        return rejectExpenseUseCase.execute(commandWithId)
    }

    @GetMapping("/{expenseId}")
    fun getExpenseById(
        @PathVariable expenseId: Long
    ): Mono<ExpenseDetailDto> {
        return getExpenseUseCase.execute(expenseId)
    }

    @GetMapping
    fun listExpenses(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) companyId: Long?
    ): Flux<ExpenseDto> {
        val query = ListExpensesQuery(
            userId = userId,
            companyId = companyId
        )
        return listExpensesUseCase.execute(query)
    }
}
