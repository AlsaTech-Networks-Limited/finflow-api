package com.alsatech.finflow.interfaces.rest

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// Phase 2 (Voice AI). TODO: voice-expense + chat endpoints. Draft only, never
// auto-submit. See BACKEND-README.md "AI / Voice AI Endpoints".
@RestController
@RequestMapping("/api/ai")
class AiController
