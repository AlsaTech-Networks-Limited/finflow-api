package com.alsatech.finflow.shared.infrastructure.port

import reactor.core.publisher.Mono
import java.math.BigDecimal

/**
 * Provider-agnostic port so Groq/OpenAI/Ollama can be swapped via config
 * without touching use cases.
 * Uses Mono for reactive streams.
 */
interface LlmClient {
    fun parseExpenseFromText(transcript: String): Mono<ParsedExpenseDraft>
    fun chat(query: String, context: String): Mono<String>
}

data class ParsedExpenseDraft(
    val amount: BigDecimal?,
    val categoryHint: String?,
    val confidence: Double,
)
