package com.alsatech.finflow.application.mapper

import com.alsatech.finflow.domain.model.Account
import com.alsatech.finflow.interfaces.dto.AccountResponse

object AccountMapper {
    fun toResponse(account: Account): AccountResponse {
        return AccountResponse(
            id = account.id!!,
            companyId = account.companyId,
            name = account.name,
            currency = account.currency,
            balance = account.balance,
            type = account.type
        )
    }
}