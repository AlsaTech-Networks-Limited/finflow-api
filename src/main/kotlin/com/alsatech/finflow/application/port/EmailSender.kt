package com.alsatech.finflow.application.port

interface EmailSender {
    suspend fun send(to: String, subject: String, body: String)
}
