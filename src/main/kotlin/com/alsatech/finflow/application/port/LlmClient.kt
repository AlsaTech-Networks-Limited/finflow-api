package com.alsatech.finflow.application.port

/**
 * Provider-agnostic port so Groq/OpenAI/Ollama can be swapped via config
 * without touching use cases. See BACKEND-README.md "How to Add Voice AI Agents".
 */
interface LlmClient {
    suspend fun parseExpenseFromText(transcript: String): ParsedExpenseDraft
    suspend fun chat(query: String, context: String): String
}

data class ParsedExpenseDraft(
    val amount: java.math.BigDecimal?,
    val categoryHint: String?,
    val confidence: Double,
)
