package com.alsatech.finflow.shared.infrastructure.port

import reactor.core.publisher.Mono

/**
 * Port for file storage operations.
 * Uses Mono for reactive streams.
 */
interface FileStorage {
    fun presignUploadUrl(key: String, contentType: String): Mono<String>
}
