package com.alsatech.finflow.resource.invoice.api

const val INVOICE_BASE_ROUTE = "/api/v1/invoices"
const val INVOICE_BY_ID = "$INVOICE_BASE_ROUTE/{invoiceId}"
const val INVOICE_SEND = "$INVOICE_BASE_ROUTE/{invoiceId}/send"
const val INVOICE_MARK_PAID = "$INVOICE_BASE_ROUTE/{invoiceId}/mark-paid"
const val INVOICE_CANCEL = "$INVOICE_BASE_ROUTE/{invoiceId}/cancel"

const val RESOURCE_NAME = "INVOICE"
