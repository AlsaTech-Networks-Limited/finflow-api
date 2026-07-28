package com.alsatech.finflow.infrastructure.persistence

import com.alsatech.finflow.domain.repository.ExpenseRepository
import org.springframework.stereotype.Repository

// TODO: implement ExpenseRepository using ExpenseR2dbcRepository.
// Follow this shape for Company/User/Account/Transaction/Category/Invoice/Approval too.
@Repository
class ExpenseRepositoryAdapter : ExpenseRepository
