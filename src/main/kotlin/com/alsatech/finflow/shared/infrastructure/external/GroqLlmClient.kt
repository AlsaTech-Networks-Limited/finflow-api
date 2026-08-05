package com.alsatech.finflow.shared.infrastructure.external

import com.alsatech.finflow.shared.infrastructure.port.LlmClient
import com.alsatech.finflow.shared.infrastructure.port.ParsedExpenseDraft
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Groq API implementation of LlmClient.
 * TODO: Implement actual Groq API calls.
 * One class per provider (OpenAI/Ollama later), swappable via config.
 */
@Component
class GroqLlmClient : LlmClient {
    override fun parseExpenseFromText(transcript: String): Mono<ParsedExpenseDraft> {
        return Mono.error(NotImplementedError("Groq API integration not yet implemented"))
    }

    override fun chat(query: String, context: String): Mono<String> {
        return Mono.error(NotImplementedError("Groq API integration not yet implemented"))
    }
}
