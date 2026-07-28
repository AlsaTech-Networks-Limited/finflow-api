package com.alsatech.finflow.infrastructure.external

import com.alsatech.finflow.application.port.FileStorage
import org.springframework.stereotype.Component

// TODO: generate presigned S3 (or Supabase Storage) upload URLs for receipts.
@Component
class S3FileStorage : FileStorage
