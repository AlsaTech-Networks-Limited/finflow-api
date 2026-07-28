package com.alsatech.finflow.interfaces.rest

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// TODO: list/create/approve/reject endpoints. See BACKEND-README.md "API Endpoints".
@RestController
@RequestMapping("/api/expenses")
class ExpenseController
