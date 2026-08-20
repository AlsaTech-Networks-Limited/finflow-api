package com.alsatech.finflow.shared.application.workflow

import reactor.core.publisher.Mono

/**
 * Base interface for action workflow services.
 *
 * Actions combine validation + use case execution in a single component.
 * Validation is PLUGGED IN via the validate() method that must be implemented.
 *
 * @param TRequest The request/command type
 * @param TResponse The response type (defaults to TRequest)
 */
interface ActionWorkFlowService<TRequest, TResponse> {

    /**
     * Validate the request.
     *
     * This is where validation logic is PLUGGED IN.
     * Implement this method to add domain validation rules.
     *
     * @param request The request to validate
     * @return Mono<TRequest> The validated request, or Mono.error if validation fails
     */
    fun validate(request: TRequest): Mono<TRequest>

    /**
     * Execute the action workflow.
     *
     * Flow:
     * 1. Validate request
     * 2. Execute use case
     * 3. Return response
     *
     * @param request The request to process
     * @return Mono<TResponse> The response
     */
    fun execute(request: TRequest): Mono<TResponse> {
        return validate(request)  // ← Validation plugged in here
            .flatMap { validatedRequest ->
                performAction(validatedRequest)
            }
    }

    /**
     * Perform the actual action logic (call use case).
     *
     * This is called AFTER validation succeeds.
     *
     * @param request The validated request
     * @return Mono<TResponse> The response
     */
    fun performAction(request: TRequest): Mono<TResponse>

    /**
     * Process the request by executing the action workflow.
     *
     * This is an alias for execute() to match the API handler pattern.
     *
     * @param request The request to process
     * @return Mono<TResponse> The response
     */
    fun processRequest(request: TRequest): Mono<TResponse> {
        return execute(request)
    }
}
