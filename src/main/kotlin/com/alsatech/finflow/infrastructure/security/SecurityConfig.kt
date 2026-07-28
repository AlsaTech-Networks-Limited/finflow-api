package com.alsatech.finflow.infrastructure.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity

// TODO: permit /api/auth/**, require JWT auth elsewhere, register JwtAuthenticationFilter.
@Configuration
@EnableWebFluxSecurity
class SecurityConfig
