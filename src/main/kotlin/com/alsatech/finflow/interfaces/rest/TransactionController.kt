package com.alsatech.finflow.interfaces.rest

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// TODO: list + CSV import endpoints. See BACKEND-README.md "API Endpoints".
@RestController
@RequestMapping("/api/transactions")
class TransactionController
