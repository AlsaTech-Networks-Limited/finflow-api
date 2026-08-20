package com.alsatech.finflow.resource.invoice.infrastructure.service.impl

import com.alsatech.finflow.resource.invoice.dto.*
import com.alsatech.finflow.resource.invoice.infrastructure.service.InvoiceReadService
import com.alsatech.finflow.resource.invoice.infrastructure.service.InvoiceWriteService
import com.alsatech.finflow.shared.domain.entity.Invoice
import com.alsatech.finflow.shared.domain.entity.InvoiceLineItem
import com.alsatech.finflow.shared.domain.entity.InvoiceStatus
import com.alsatech.finflow.shared.domain.repository.InvoiceLineItemRepository
import com.alsatech.finflow.shared.domain.repository.InvoiceRepository
import com.collicode.common.util.asUnit
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class InvoiceWriteServiceImpl(
    private val invoiceRepository: InvoiceRepository,
    private val lineItemRepository: InvoiceLineItemRepository,
    private val readService: InvoiceReadService
) : InvoiceWriteService {

    override fun createInvoice(command: CreateInvoiceCommand): Mono<InvoiceDetailDto> {
        val totalAmount = command.lineItems.sumOf { it.quantity * it.unitPrice }

        val invoice = Invoice(
            companyId = command.companyId,
            invoiceNumber = command.invoiceNumber,
            clientName = command.clientName,
            clientEmail = command.clientEmail,
            amount = totalAmount,
            status = InvoiceStatus.DRAFT,
            issueDate = command.issueDate,
            dueDate = command.dueDate,
            notes = command.notes
        )

        return invoiceRepository.save(invoice)
            .flatMap { savedInvoice ->
                val lineItems = command.lineItems.mapIndexed { index, item ->
                    InvoiceLineItem(
                        invoiceId = savedInvoice.id!!,
                        description = item.description,
                        quantity = item.quantity,
                        unitPrice = item.unitPrice,
                        amount = item.quantity * item.unitPrice,
                        position = index + 1
                    )
                }
                lineItemRepository.saveAll(lineItems)
                    .collectList()
                    .then(readService.fetchInvoiceById(savedInvoice.id!!))
            }
    }

    override fun updateInvoice(command: UpdateInvoiceCommand): Mono<InvoiceDetailDto> {
        return invoiceRepository.findById(command.invoiceId)
            .flatMap { existingInvoice ->
                val totalAmount = command.lineItems.sumOf { it.quantity * it.unitPrice }

                val updatedInvoice = existingInvoice.copy(
                    clientName = command.clientName,
                    clientEmail = command.clientEmail,
                    amount = totalAmount,
                    issueDate = command.issueDate,
                    dueDate = command.dueDate,
                    notes = command.notes
                )

                invoiceRepository.save(updatedInvoice)
                    .flatMap { savedInvoice ->
                        // Delete existing line items
                        lineItemRepository.deleteByInvoiceId(savedInvoice.id!!)
                            .then(Mono.just(savedInvoice))
                    }
                    .flatMap { savedInvoice ->
                        // Create new line items
                        val lineItems = command.lineItems.mapIndexed { index, item ->
                            InvoiceLineItem(
                                invoiceId = savedInvoice.id!!,
                                description = item.description,
                                quantity = item.quantity,
                                unitPrice = item.unitPrice,
                                amount = item.quantity * item.unitPrice,
                                position = index + 1
                            )
                        }
                        lineItemRepository.saveAll(lineItems)
                            .collectList()
                            .then(readService.fetchInvoiceById(savedInvoice.id!!))
                    }
            }
    }

    override fun sendInvoice(command: SendInvoiceCommand): Mono<Unit> {
        return invoiceRepository.updateStatus(command.invoiceId, InvoiceStatus.SENT)
            .asUnit()
    }

    override fun markInvoicePaid(command: MarkInvoicePaidCommand): Mono<Unit> {
        return invoiceRepository.updateStatus(command.invoiceId, InvoiceStatus.PAID)
            .asUnit()
    }

    override fun cancelInvoice(command: CancelInvoiceCommand): Mono<Unit> {
        return invoiceRepository.updateStatus(command.invoiceId, InvoiceStatus.CANCELLED)
            .asUnit()
    }
}
