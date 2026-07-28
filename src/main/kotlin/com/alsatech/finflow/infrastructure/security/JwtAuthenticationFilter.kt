package com.alsatech.finflow.infrastructure.security

import org.springframework.stereotype.Component
import org.springframework.web.server.WebFilter

// TODO: read Authorization header, validate via JwtService, populate security context.
@Component
class JwtAuthenticationFilter : WebFilter
