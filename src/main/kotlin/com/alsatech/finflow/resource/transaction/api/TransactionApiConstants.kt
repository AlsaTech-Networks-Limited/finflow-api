package com.alsatech.finflow.resource.transaction.api

const val BASE_ROUTE = "/api/v1/transactions"
const val TRANSACTION_BY_ID = "$BASE_ROUTE/{transactionId}"
const val TRANSACTION_IMPORT = "$BASE_ROUTE/import"
const val TRANSACTION_RECONCILE = "$BASE_ROUTE/{transactionId}/reconcile"

const val RESOURCE_NAME = "TRANSACTION"
