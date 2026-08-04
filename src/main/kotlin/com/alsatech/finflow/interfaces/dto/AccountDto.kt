package com.alsatech.finflow.interfaces.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID

data class CreateAccountRequest(
    @field:NotBlank(message = "Account name is required")
    @field:Size(min = 1, max = 100, message = "Account name must be between 1 and 100 characters")
    val name: String,

    @field:NotBlank(message = "Currency is required")
    @field:Size(min = 3, max = 3, message = "Currency must be a 3-letter code (e.g., USD, EUR)")
    val currency: String,

    @field:NotNull(message = "Initial balance is required")
    val balance: BigDecimal,

    @field:NotBlank(message = "Account type is required")
    @field:Size(min = 1, max = 50, message = "Account type must be between 1 and 50 characters")
    val type: String
)

data class AccountResponse(
    val id: UUID,
    val companyId: UUID,
    val name: String,
    val currency: String,
    val balance: BigDecimal,
    val type: String
)