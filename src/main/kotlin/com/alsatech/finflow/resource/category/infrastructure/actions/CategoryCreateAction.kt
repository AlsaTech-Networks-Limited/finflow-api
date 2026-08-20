package com.alsatech.finflow.resource.category.infrastructure.actions

import com.alsatech.finflow.resource.category.domain.service.CategoryValidationService
import com.alsatech.finflow.resource.category.dto.CreateCategoryCommand
import com.alsatech.finflow.resource.category.infrastructure.actions.usecase.CategoryCreateUseCase
import com.collicode.common.dto.ActionResponse
import com.collicode.common.service.actions.ActionWorkFlowService
import handleActionExecution
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CategoryCreateAction(
    private val useCase: CategoryCreateUseCase,
    private val validationService: CategoryValidationService
) : ActionWorkFlowService<CreateCategoryCommand> {

    override fun validate(request: CreateCategoryCommand): Mono<CreateCategoryCommand> {
        return validationService.validateCreateCategory(request)
    }

    override fun processRequest(request: CreateCategoryCommand): Mono<ActionResponse> {
        return handleActionExecution(
            input = request,
            validateInput = ::validate,
            executeAction = useCase::executeAction
        )
    }
}
