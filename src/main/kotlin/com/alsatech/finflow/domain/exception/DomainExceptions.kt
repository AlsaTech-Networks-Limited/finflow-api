package com.alsatech.finflow.domain.exception

sealed class DomainException(message: String) : RuntimeException(message)

class EntityNotFoundException(entity: String, id: Any) :
    DomainException("$entity not found: $id")

class InvalidStateTransitionException(message: String) : DomainException(message)

class UnauthorizedActionException(message: String) : DomainException(message)
