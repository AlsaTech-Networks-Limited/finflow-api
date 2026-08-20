package com.alsatech.finflow.resource.expense.api


const val BASE_ROUTE = "/api/v1/expenses"
const val EXPENSE_BY_ID = "$BASE_ROUTE/{expenseId}"
const val EXPENSE_APPROVE = "$BASE_ROUTE/{expenseId}/approve"
const val EXPENSE_REJECT = "$BASE_ROUTE/{expenseId}/reject"


const val RESOURCE_NAME = "EXPENSE"