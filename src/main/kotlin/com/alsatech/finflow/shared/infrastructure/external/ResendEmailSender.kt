package com.alsatech.finflow.shared.infrastructure.external

import com.alsatech.finflow.shared.infrastructure.port.EmailSender
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Resend (or SES) email service implementation.
 * TODO: Implement actual Resend/SES API calls for transactional email.
 */
@Component
class ResendEmailSender : EmailSender {
    override fun send(to: String, subject: String, body: String): Mono<Unit> {
        // TODO: Integrate with Resend or AWS SES
        return Mono.error(NotImplementedError("Email service not yet implemented"))
    }
}
