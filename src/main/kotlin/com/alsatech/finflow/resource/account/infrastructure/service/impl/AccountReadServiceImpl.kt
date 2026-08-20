package com.alsatech.finflow.resource.account.infrastructure.service.impl

import com.alsatech.finflow.resource.account.dto.AccountDetailDto
import com.alsatech.finflow.resource.account.dto.AccountDto
import com.alsatech.finflow.resource.account.infrastructure.service.AccountReadService
import com.alsatech.finflow.shared.domain.repository.AccountRepository
import com.alsatech.finflow.shared.domain.repository.TransactionRepository
import com.collicode.common.exception.BusinessException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AccountReadServiceImpl(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : AccountReadService {

    override fun fetchAccountById(accountId: Long): Mono<AccountDetailDto> {
        return accountRepository.findById(accountId)
            .switchIfEmpty(Mono.error(BusinessException.exception(
                "ACCOUNT_NOT_FOUND",
                "Account not found with ID: $accountId"
            )))
            .flatMap { account ->
                transactionRepository.findByAccountId(account.id!!)
                    .count()
                    .map { transactionCount ->
                        AccountDetailDto(
                            id = account.id!!,
                            companyId = account.companyId,
                            name = account.name,
                            type = account.type,
                            balance = account.balance,
                            currency = account.currency,
                            transactionCount = transactionCount,
                            createdAt = account.createdAt
                        )
                    }
            }
    }

    override fun fetchAccountsByCompany(companyId: Long): Flux<AccountDto> {
        return accountRepository.findByCompanyId(companyId)
            .map { account ->
                AccountDto(
                    id = account.id!!,
                    companyId = account.companyId,
                    name = account.name,
                    type = account.type,
                    balance = account.balance,
                    currency = account.currency,
                    createdAt = account.createdAt
                )
            }
    }
}
