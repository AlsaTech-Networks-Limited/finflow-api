package com.alsatech.finflow.resource.expense.api

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Service
class ExpenseApiResource {

    @Bean(name = ["expenseApiRoute"])
    fun routes(expenseApiHandler: ExpenseApiHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route(POST(BASE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), expenseApiHandler::submitExpense)
            .andRoute(PUT(EXPENSE_APPROVE).and(accept(MediaType.APPLICATION_JSON)), expenseApiHandler::approveExpense)
            .andRoute(PUT(EXPENSE_REJECT).and(accept(MediaType.APPLICATION_JSON)), expenseApiHandler::rejectExpense)
            .andRoute(GET(EXPENSE_BY_ID).and(accept(MediaType.APPLICATION_JSON)), expenseApiHandler::fetchExpenseById)
            .andRoute(GET(BASE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), expenseApiHandler::fetchAllExpenses)
    }
}
