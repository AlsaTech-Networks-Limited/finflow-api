package com.alsatech.finflow.interfaces.advice

import org.springframework.web.bind.annotation.RestControllerAdvice

// TODO: map DomainException subtypes (domain/exception) to HTTP status codes + error bodies.
@RestControllerAdvice
class GlobalExceptionHandler
