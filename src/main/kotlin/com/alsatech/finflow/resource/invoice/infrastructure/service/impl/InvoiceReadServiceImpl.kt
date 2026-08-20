package com.alsatech.finflow.resource.invoice.infrastructure.service.impl

import com.alsatech.finflow.resource.invoice.dto.InvoiceDetailDto
import com.alsatech.finflow.resource.invoice.dto.InvoiceDto
import com.alsatech.finflow.resource.invoice.dto.InvoiceLineItemDto
import com.alsatech.finflow.resource.invoice.dto.ListInvoicesQuery
import com.alsatech.finflow.resource.invoice.infrastructure.service.InvoiceReadService
import com.alsatech.finflow.shared.domain.entity.Invoice
import com.alsatech.finflow.shared.domain.entity.InvoiceLineItem
import com.alsatech.finflow.shared.domain.repository.InvoiceLineItemRepository
import com.alsatech.finflow.shared.domain.repository.InvoiceRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class InvoiceReadServiceImpl(
    private val invoiceRepository: InvoiceRepository,
    private val lineItemRepository: InvoiceLineItemRepository
) : InvoiceReadService {

    override fun fetchInvoiceById(invoiceId: Long): Mono<InvoiceDetailDto> {
        return invoiceRepository.findById(invoiceId)
            .flatMap { invoice ->
                lineItemRepository.findByInvoiceId(invoice.id!!)
                    .collectList()
                    .map { lineItems ->
                        InvoiceDetailDto(
                            id = invoice.id!!,
                            companyId = invoice.companyId,
                            invoiceNumber = invoice.invoiceNumber,
                            clientName = invoice.clientName,
                            clientEmail = invoice.clientEmail,
                            amount = invoice.amount,
                            status = invoice.status,
                            issueDate = invoice.issueDate,
                            dueDate = invoice.dueDate,
                            notes = invoice.notes,
                            lineItems = lineItems.map { it.toDto() },
                            createdAt = invoice.createdAt
                        )
                    }
            }
    }

    override fun fetchInvoicesByCompany(query: ListInvoicesQuery): Flux<InvoiceDto> {
        return invoiceRepository.findByCompanyId(query.companyId)
            .filter { invoice ->
                query.status == null || invoice.status == query.status
            }
            .flatMap { invoice ->
                lineItemRepository.findByInvoiceId(invoice.id!!)
                    .count()
                    .map { count -> invoice to count }
            }
            .map { (invoice, lineItemCount) ->
                InvoiceDto(
                    id = invoice.id!!,
                    companyId = invoice.companyId,
                    invoiceNumber = invoice.invoiceNumber,
                    clientName = invoice.clientName,
                    clientEmail = invoice.clientEmail,
                    amount = invoice.amount,
                    status = invoice.status,
                    issueDate = invoice.issueDate,
                    dueDate = invoice.dueDate,
                    notes = invoice.notes,
                    lineItemsCount = lineItemCount.toInt(),
                    createdAt = invoice.createdAt
                )
            }
    }

    // ================= PRIVATE MAPPING FUNCTIONS =================

    private fun InvoiceLineItem.toDto(): InvoiceLineItemDto {
        return InvoiceLineItemDto(
            id = this.id,
            description = this.description,
            quantity = this.quantity,
            unitPrice = this.unitPrice,
            amount = this.amount,
            position = this.position
        )
    }
}
