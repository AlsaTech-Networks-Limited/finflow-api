# Action Workflow Pattern - Complete Request Flow

## Overview

The Action Workflow Pattern combines **validation** + **use case execution** in a single Action class. Validation is **plugged in** directly in the Action's `validate()` method.

## Complete Request Flow

### Example: Approve Expense Request

```
HTTP POST /api/v1/expenses/{expenseId}/approve
{
  "expenseId": "exp_1704067200001",
  "approverId": 2,
  "comment": "Approved for client meeting"
}

          ↓

┌─────────────────────────────────────────────────────────────────┐
│ 1. CONTROLLER (Presentation Layer)                             │
│    ExpenseController.approveExpense()                           │
│                                                                  │
│    - Receives HTTP request                                      │
│    - Bean validation executes (@Valid annotation)              │
│    - Converts JSON to ApproveExpenseCommand                    │
│    - Calls action.execute()                                    │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ ApproveExpenseCommand
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│ 2. ACTION (Application Layer)                                   │
│    ExpenseApproveAction.execute()                              │
│                                                                  │
│    @Service                                                     │
│    class ExpenseApproveAction(                                  │
│        private val useCase: ExpenseApproveUseCase              │
│    ) : ActionWorkFlowService<ApproveExpenseCommand, Unit> {    │
│                                                                  │
│        ✅ VALIDATION PLUGGED IN HERE ↓                          │
│                                                                  │
│        override fun validate(request: ApproveExpenseCommand)    │
│            : Mono<ApproveExpenseCommand> {                     │
│            return Mono.just(request)                           │
│                .flatMap { validateApprover(it) }               │
│                .flatMap { validateExpenseId(it) }              │
│        }                                                        │
│                                                                  │
│        override fun performAction(request: ApproveExpenseCommand)│
│            : Mono<Unit> {                                       │
│            return useCase.execute(request)                     │
│        }                                                        │
│    }                                                            │
│                                                                  │
│    Flow inside execute():                                       │
│    1. validate(request) ← Validation executes here             │
│    2. performAction(validatedRequest) ← Then use case          │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ Validated ApproveExpenseCommand
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│ 3. USE CASE (Application Layer)                                │
│    ExpenseApproveUseCase.execute()                             │
│    (Implementation: ExpenseApproveUseCaseImpl)                 │
│                                                                  │
│    @Service                                                     │
│    class ExpenseApproveUseCaseImpl(                            │
│        private val writeService: ExpenseWriteService           │
│    ) : ExpenseApproveUseCase {                                 │
│                                                                  │
│        override fun execute(command: ApproveExpenseCommand)     │
│            : Mono<Unit> {                                       │
│            return writeService.approveExpense(command)         │
│        }                                                        │
│    }                                                            │
│                                                                  │
│    - Receives validated command                                │
│    - Delegates to write service                                │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ ApproveExpenseCommand
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│ 4. WRITE SERVICE (Infrastructure Layer)                        │
│    ExpenseWriteService.approveExpense()                        │
│    (Implementation: ExpenseWriteServiceImpl)                   │
│                                                                  │
│    @Service                                                     │
│    class ExpenseWriteServiceImpl(                              │
│        private val expenseRepository: ExpenseRepository,       │
│        private val approvalRepository: ApprovalRepository,     │
│        private val idGenerator: IdGenerator                    │
│    ) : ExpenseWriteService {                                   │
│                                                                  │
│        override fun approveExpense(command: ApproveExpenseCommand)│
│            : Mono<Unit> {                                       │
│                                                                  │
│            return expenseRepository.findById(command.expenseId) │
│                .switchIfEmpty(Mono.error(...))  ← DB validation│
│                .flatMap { expense ->                           │
│                    // State validation (requires DB):          │
│                    when {                                       │
│                        expense.status != PENDING ->            │
│                            Mono.error(...)                     │
│                        expense.userId == command.approverId -> │
│                            Mono.error(...)  ← Self-approval    │
│                        else -> {                               │
│                            // Generate approval ID             │
│                            val approvalId = idGenerator        │
│                                .generateId(EntityType.APPROVAL)│
│                                                                  │
│                            // Update expense status            │
│                            val updatedExpense = expense.copy(  │
│                                status = APPROVED,              │
│                                updatedAt = Instant.now()       │
│                            )                                    │
│                                                                  │
│                            // Save expense + create approval   │
│                            expenseRepository.save(updatedExpense)│
│                                .flatMap { savedExpense ->      │
│                                    val approval = Approval(... )│
│                                    approvalRepository.save(approval)│
│                                }                               │
│                                .then(Mono.just(Unit))          │
│                        }                                       │
│                    }                                           │
│                }                                               │
│        }                                                        │
│    }                                                            │
│                                                                  │
│    - Fetches expense from repository                           │
│    - Validates state (status is PENDING, not self-approval)   │
│    - Creates domain entities (Expense, Approval)               │
│    - Generates IDs using IdGenerator                           │
│    - Persists changes to repositories                          │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│ 5. REPOSITORY (Infrastructure Layer)                           │
│    ExpenseRepository.save()                                    │
│    ApprovalRepository.save()                                   │
│    (Implementation: Adapter classes)                           │
│                                                                  │
│    - Saves expense with updated status                         │
│    - Creates approval record                                   │
│    - Executes SQL via R2dbcEntityTemplate                      │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│ 6. DATABASE                                                     │
│                                                                  │
│    UPDATE expenses                                             │
│    SET status = 'APPROVED', updated_at = NOW()                 │
│    WHERE record_id = 'exp_1704067200001'                       │
│                                                                  │
│    INSERT INTO approvals (record_id, expense_id, ...)          │
│    VALUES ('app_1704067300001', 'exp_1704067200001', ...)      │
└─────────────────────────────────────────────────────────────────┘

          ↓

HTTP 200 OK
{
  "meta": {
    "requestId": "...",
    "timestamp": "2025-01-15T12:00:00Z",
    "status": "SUCCESS"
  },
  "payload": null
}
```

## Package Structure

```
expense/
├── application/
│   ├── command/                           # Commands (Write operations)
│   │   ├── SubmitExpenseCommand.kt
│   │   ├── ApproveExpenseCommand.kt
│   │   └── RejectExpenseCommand.kt
│   │
│   ├── query/                             # Queries (Read operations)
│   │   ├── GetExpenseQuery.kt
│   │   └── ListExpensesQuery.kt
│   │
│   ├── action/                            # ✅ Actions (Validation Plugged In Here)
│   │   ├── ExpenseSubmitAction.kt
│   │   ├── ExpenseApproveAction.kt
│   │   └── ExpenseRejectAction.kt
│   │
│   └── usecase/                           # Use Cases (Business Logic Interface)
│       ├── ExpenseSubmitUseCase.kt
│       ├── ExpenseApproveUseCase.kt
│       ├── ExpenseRejectUseCase.kt
│       └── impl/
│           ├── ExpenseSubmitUseCaseImpl.kt
│           ├── ExpenseApproveUseCaseImpl.kt
│           └── ExpenseRejectUseCaseImpl.kt
│
├── domain/                                # Domain Layer
│   ├── entity/
│   │   ├── Expense.kt
│   │   └── Approval.kt
│   └── repository/
│       ├── ExpenseRepository.kt
│       └── ApprovalRepository.kt
│
├── infrastructure/                        # Infrastructure Layer
│   ├── persistence/
│   │   ├── ExpenseRepositoryAdapter.kt
│   │   └── ApprovalRepositoryAdapter.kt
│   └── service/
│       ├── ExpenseWriteService.kt        # Handles write operations
│       ├── ExpenseReadService.kt         # Handles read operations
│       └── impl/
│           ├── ExpenseWriteServiceImpl.kt
│           └── ExpenseReadServiceImpl.kt
│
├── dto/                                   # Response DTOs
│   ├── ExpenseDto.kt
│   ├── ExpenseDetailDto.kt
│   └── ApprovalDto.kt
│
└── presentation/                          # Controllers
    └── ExpenseController.kt
```

## Validation Layers

### Layer 1: Bean Validation (Controller Entry)

```kotlin
@PostMapping("/api/v1/expenses/{expenseId}/approve")
fun approveExpense(
    @Valid @RequestBody request: RequestWrapper<ApproveExpenseCommand>
    //  ↑ Bean validation executes here
) {
    // @NotBlank, @NotNull, @Positive, etc.
}
```

### Layer 2: Domain Validation (Action - PLUGGED IN HERE)

```kotlin
@Service
class ExpenseApproveAction(
    private val useCase: ExpenseApproveUseCase
) : ActionWorkFlowService<ApproveExpenseCommand, Unit> {

    ✅ VALIDATION PLUGGED IN HERE ↓

    override fun validate(request: ApproveExpenseCommand): Mono<ApproveExpenseCommand> {
        return Mono.just(request)
            .flatMap { validateApprover(it) }
            .flatMap { validateExpenseId(it) }
    }

    override fun performAction(request: ApproveExpenseCommand): Mono<Unit> {
        return useCase.execute(request)
    }
}
```

### Layer 3: State Validation (Write Service)

```kotlin
@Service
class ExpenseWriteServiceImpl : ExpenseWriteService {

    override fun approveExpense(command: ApproveExpenseCommand): Mono<Unit> {
        return expenseRepository.findById(command.expenseId)
            .flatMap { expense ->
                when {
                    expense.status != PENDING -> error("Not pending")
                    expense.userId == command.approverId -> error("Self-approval")
                    else -> approve(expense)
                }
            }
    }
}
```

## Code Examples

### Complete Action with Validation

```kotlin
@Service
class ExpenseApproveAction(
    private val useCase: ExpenseApproveUseCase
) : ActionWorkFlowService<ApproveExpenseCommand, Unit> {

    /**
     * ✅ VALIDATION PLUGGED IN HERE
     *
     * Business rules validated:
     * - Approver has permission (future)
     * - Expense ID is well-formed (future)
     */
    override fun validate(request: ApproveExpenseCommand): Mono<ApproveExpenseCommand> {
        return Mono.just(request)
            .flatMap { validateApprover(it) }
            .flatMap { validateExpenseId(it) }
    }

    /**
     * Execute use case after validation succeeds.
     */
    override fun performAction(request: ApproveExpenseCommand): Mono<Unit> {
        return useCase.execute(request)
    }

    // ========== Private Validation Methods ==========

    private fun validateApprover(command: ApproveExpenseCommand): Mono<ApproveExpenseCommand> {
        // Future: Check if approver exists and has permission
        // Future: Check approval limits based on expense amount
        return Mono.just(command)
    }

    private fun validateExpenseId(command: ApproveExpenseCommand): Mono<ApproveExpenseCommand> {
        // Future: Validate ID format matches exp_xxxxx pattern
        return Mono.just(command)
    }
}
```

### Complete Use Case

```kotlin
@Service
class ExpenseApproveUseCaseImpl(
    private val writeService: ExpenseWriteService
) : ExpenseApproveUseCase {

    override fun execute(command: ApproveExpenseCommand): Mono<Unit> {
        // Command is already validated by the action
        // Just delegate to write service for persistence
        return writeService.approveExpense(command)
    }
}
```

### Controller Integration

```kotlin
@RestController
@RequestMapping("/api/v1/expenses")
class ExpenseController(
    private val submitAction: ExpenseSubmitAction,
    private val approveAction: ExpenseApproveAction,
    private val rejectAction: ExpenseRejectAction
) {

    @PostMapping
    fun submitExpense(
        @Valid @RequestBody request: RequestWrapper<SubmitExpenseCommand>
    ): Mono<ResponseWrapper<String>> {
        return submitAction.execute(request.payload)  // ← Action handles validation + execution
            .map { expenseId -> ResponseWrapper.success(expenseId) }
            .onErrorResume { error -> handleError(error) }
    }

    @PutMapping("/{expenseId}/approve")
    fun approveExpense(
        @PathVariable expenseId: String,
        @Valid @RequestBody request: RequestWrapper<ApproveExpenseCommand>
    ): Mono<ResponseWrapper<Unit>> {
        return approveAction.execute(request.payload)  // ← Action handles validation + execution
            .map { ResponseWrapper.success(Unit) }
            .onErrorResume { error -> handleError(error) }
    }
}
```

## Execution Flow Diagram

```
┌──────────────┐
│ Controller   │ 1. Receives HTTP request
└──────┬───────┘ 2. Bean validation
       │
       ↓ Command
┌──────────────┐
│ Action       │ 3. action.execute()
│              │    ├─ validate(command)   ✅ VALIDATION HERE
│              │    └─ performAction(validatedCommand)
└──────┬───────┘
       │
       ↓ Validated Command
┌──────────────┐
│ Use Case     │ 4. useCase.execute(command)
│              │    └─ Delegates to write service
└──────┬───────┘
       │
       ↓ Command
┌──────────────┐
│ Write Service│ 5. writeService.approve(command)
│              │    ├─ Fetch from repository
│              │    ├─ State validation (DB access)
│              │    ├─ Generate IDs
│              │    ├─ Create entities
│              │    └─ Save to repository
└──────┬───────┘
       │
       ↓ Entities
┌──────────────┐
│ Repository   │ 6. repository.save(entity)
│              │    └─ SQL execution
└──────┬───────┘
       │
       ↓ SQL
┌──────────────┐
│ Database     │ 7. UPDATE/INSERT
└──────────────┘
```

## Key Points

### 1. Validation is Plugged Into Actions

✅ **Actions implement `validate()` method**:
```kotlin
@Service
class ExpenseApproveAction : ActionWorkFlowService<ApproveExpenseCommand, Unit> {
    override fun validate(request: ApproveExpenseCommand): Mono<ApproveExpenseCommand> {
        // Validation logic here ← PLUGGED IN
    }
}
```

### 2. Automatic Execution Flow

The `ActionWorkFlowService` interface defines the flow:
```kotlin
interface ActionWorkFlowService<TRequest, TResponse> {
    fun validate(request: TRequest): Mono<TRequest>

    fun execute(request: TRequest): Mono<TResponse> {
        return validate(request)  // ← Validation called first
            .flatMap { performAction(it) }  // ← Then action
    }

    fun performAction(request: TRequest): Mono<TResponse>
}
```

### 3. Separation of Concerns

- **Controller**: HTTP concerns, request/response mapping
- **Action**: Validation + orchestration
- **Use Case**: Business logic interface
- **Write Service**: Persistence, state transitions, ID generation
- **Repository**: Data access

### 4. Clean Architecture Layers

```
Presentation    →    Application    →     Domain     →   Infrastructure
(Controller)    →  (Action/UseCase)  →   (Entity)    →  (Repository/DB)
     │                    │                  │                  │
     │                    │                  │                  │
HTTP Request    →   Validation       →   Business    →    Data Access
                   + Orchestration       Logic
```

## Benefits

✅ **Validation in the right place**: Actions validate before execution
✅ **Single responsibility**: Each action handles one command type
✅ **Testable**: Actions can be tested in isolation
✅ **Clean separation**: Validation, use case, persistence are separate
✅ **Type-safe**: Strong typing throughout the flow
✅ **Maintainable**: Clear, predictable request flow
✅ **Flexible**: Easy to add new actions for new operations

---

**Summary**:

Validation is **plugged into Actions** via the `validate()` method.

**Flow**: Controller → Action.validate() → Action.performAction() → UseCase.execute() → WriteService → Repository → Database

**For Read Operations**: Controller → ReadService → Repository → Database (no action/validation needed)
