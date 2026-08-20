package com.alsatech.finflow.resource.auth.infrastructure.service

import com.alsatech.finflow.resource.auth.dto.AuthResponseDto
import com.alsatech.finflow.resource.auth.dto.LoginCommand
import com.alsatech.finflow.resource.auth.dto.RegisterCommand
import reactor.core.publisher.Mono

/**
 * Service for authentication operations.
 */
interface AuthService {
    fun login(command: LoginCommand): Mono<AuthResponseDto>
    fun register(command: RegisterCommand): Mono<AuthResponseDto>
}
