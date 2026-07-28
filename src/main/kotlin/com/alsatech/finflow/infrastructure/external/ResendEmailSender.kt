package com.alsatech.finflow.infrastructure.external

import com.alsatech.finflow.application.port.EmailSender
import org.springframework.stereotype.Component

// TODO: call Resend (or SES) API for transactional email.
@Component
class ResendEmailSender : EmailSender
