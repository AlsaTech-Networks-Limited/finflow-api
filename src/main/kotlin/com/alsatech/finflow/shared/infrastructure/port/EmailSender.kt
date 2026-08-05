package com.alsatech.finflow.shared.infrastructure.port

import reactor.core.publisher.Mono

/**
 * Port for email sending operations.
 * Uses Mono for reactive streams.
 */
interface EmailSender {
    fun send(to: String, subject: String, body: String): Mono<Unit>
}
