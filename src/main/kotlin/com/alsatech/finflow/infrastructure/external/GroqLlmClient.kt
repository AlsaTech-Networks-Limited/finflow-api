package com.alsatech.finflow.infrastructure.external

import com.alsatech.finflow.application.port.LlmClient
import org.springframework.stereotype.Component

// TODO: call Groq API. One class per provider (OpenAI/Ollama later), swappable via config.
@Component
class GroqLlmClient : LlmClient
