package com.alsatech.finflow.resource.invoice.infrastructure.service

import com.alsatech.finflow.resource.invoice.domain.service.InvoiceValidationService
import com.alsatech.finflow.resource.invoice.dto.*
import com.alsatech.finflow.shared.domain.entity.InvoiceStatus
import com.alsatech.finflow.shared.domain.repository.InvoiceRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class InvoiceValidationServiceImpl(
    private val invoiceRepository: InvoiceRepository
) : InvoiceValidationService {

    override fun validateCreateInvoice(command: CreateInvoiceCommand): Mono<CreateInvoiceCommand> {
        return Mono.just(command)
            .flatMap { validateInvoiceNumberUnique(it.invoiceNumber).thenReturn(it) }
            .flatMap { validateDates(it.issueDate, it.dueDate).thenReturn(it) }
            .flatMap { validateLineItems(it.lineItems).thenReturn(it) }
    }

    override fun validateUpdateInvoice(command: UpdateInvoiceCommand): Mono<UpdateInvoiceCommand> {
        return Mono.just(command)
            .flatMap { validateInvoiceExists(it.invoiceId).thenReturn(it) }
            .flatMap { validateInvoiceStatus(it.invoiceId, InvoiceStatus.DRAFT).thenReturn(it) }
            .flatMap { validateDates(it.issueDate, it.dueDate).thenReturn(it) }
            .flatMap { validateLineItems(it.lineItems).thenReturn(it) }
    }

    override fun validateSendInvoice(command: SendInvoiceCommand): Mono<SendInvoiceCommand> {
        return Mono.just(command)
            .flatMap { validateInvoiceExists(it.invoiceId).thenReturn(it) }
            .flatMap { validateInvoiceStatus(it.invoiceId, InvoiceStatus.DRAFT).thenReturn(it) }
    }

    override fun validateMarkInvoicePaid(command: MarkInvoicePaidCommand): Mono<MarkInvoicePaidCommand> {
        return Mono.just(command)
            .flatMap { validateInvoiceExists(it.invoiceId).thenReturn(it) }
            .flatMap { validateInvoiceStatus(it.invoiceId, InvoiceStatus.SENT).thenReturn(it) }
    }

    override fun validateCancelInvoice(command: CancelInvoiceCommand): Mono<CancelInvoiceCommand> {
        return Mono.just(command)
            .flatMap { validateInvoiceExists(it.invoiceId).thenReturn(it) }
            .flatMap { validateInvoiceNotPaid(it.invoiceId).thenReturn(it) }
    }

    // ================= PRIVATE VALIDATION HELPERS =================

    private fun validateInvoiceExists(invoiceId: Long): Mono<Unit> {
        return invoiceRepository.findById(invoiceId)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Invoice with ID $invoiceId not found")))
            .then(Mono.just(Unit))
    }

    private fun validateInvoiceNumberUnique(invoiceNumber: String): Mono<Unit> {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
            .flatMap<Unit> { Mono.error(IllegalArgumentException("Invoice number $invoiceNumber already exists")) }
            .switchIfEmpty(Mono.just(Unit))
    }

    private fun validateInvoiceStatus(invoiceId: Long, expectedStatus: InvoiceStatus): Mono<Unit> {
        return invoiceRepository.findById(invoiceId)
            .flatMap { invoice ->
                if (invoice.status != expectedStatus) {
                    Mono.error(IllegalArgumentException("Invoice must be in $expectedStatus status"))
                } else {
                    Mono.just(Unit)
                }
            }
    }

    private fun validateInvoiceNotPaid(invoiceId: Long): Mono<Unit> {
        return invoiceRepository.findById(invoiceId)
            .flatMap { invoice ->
                if (invoice.status == InvoiceStatus.PAID) {
                    Mono.error(IllegalArgumentException("Cannot cancel a paid invoice"))
                } else {
                    Mono.just(Unit)
                }
            }
    }

    private fun validateDates(issueDate: java.time.LocalDate, dueDate: java.time.LocalDate): Mono<Unit> {
        return if (dueDate.isBefore(issueDate)) {
            Mono.error(IllegalArgumentException("Due date must be on or after issue date"))
        } else {
            Mono.just(Unit)
        }
    }

    private fun validateLineItems(lineItems: List<CreateInvoiceLineItemCommand>): Mono<Unit> {
        if (lineItems.isEmpty()) {
            return Mono.error(IllegalArgumentException("Invoice must have at least one line item"))
        }

        val hasInvalidAmounts = lineItems.any { item ->
            val calculatedAmount = item.quantity * item.unitPrice
            calculatedAmount < BigDecimal.ZERO
        }

        return if (hasInvalidAmounts) {
            Mono.error(IllegalArgumentException("Line item amounts must be non-negative"))
        } else {
            Mono.just(Unit)
        }
    }
}
