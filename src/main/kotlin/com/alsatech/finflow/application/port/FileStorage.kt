package com.alsatech.finflow.application.port

interface FileStorage {
    suspend fun presignUploadUrl(key: String, contentType: String): String
}
