package com.alsatech.finflow.shared.infrastructure.external

import com.alsatech.finflow.shared.infrastructure.port.FileStorage
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * S3 (or Supabase Storage) file storage implementation.
 * TODO: Implement presigned URL generation for S3 or Supabase Storage.
 */
@Component
class S3FileStorage : FileStorage {
    override fun presignUploadUrl(key: String, contentType: String): Mono<String> {
        // TODO: Generate presigned S3/Supabase upload URLs for receipt uploads
        return Mono.error(NotImplementedError("File storage service not yet implemented"))
    }
}
