package com.alsatech.finflow.resource.reimbursement.api

const val REIMBURSEMENT_BASE_ROUTE = "/api/v1/reimbursements"
const val REIMBURSEMENT_BY_ID = "$REIMBURSEMENT_BASE_ROUTE/{reimbursementId}"
const val REIMBURSEMENT_MARK_PAID = "$REIMBURSEMENT_BASE_ROUTE/{reimbursementId}/mark-paid"

const val RESOURCE_NAME = "REIMBURSEMENT"
