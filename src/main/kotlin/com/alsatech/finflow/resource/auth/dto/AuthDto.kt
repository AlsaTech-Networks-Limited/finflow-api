package com.alsatech.finflow.resource.auth.dto

import com.alsatech.finflow.shared.domain.entity.Role
import jakarta.validation.constraints.*

// ================= COMMAND DTOs (Requests) =================

data class LoginCommand(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String
)

data class RegisterCommand(
    @field:NotBlank(message = "Company name is required")
    @field:Size(min = 2, max = 250, message = "Company name must be between 2 and 250 characters")
    val companyName: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, and one number"
    )
    val password: String,

    @field:NotBlank(message = "Full name is required")
    @field:Size(min = 2, max = 250, message = "Full name must be between 2 and 250 characters")
    val fullName: String
)

// ================= RESPONSE DTOs =================

data class AuthResponseDto(
    val token: String,
    val user: UserInfoDto,
    val expiresIn: Long = 86400 // 24 hours in seconds
)

data class UserInfoDto(
    val id: Long,
    val email: String,
    val fullName: String?,
    val role: Role,
    val companyId: Long,
    val companyName: String?
)
