# Domain Organization Guide

## Proper Domain Structure

This guide explains how to organize the domain layer in a Clean Architecture resource module.

## Current Structure (Expense Resource)

```
resource/expense/
├── api/                                    # API Layer (Controllers/Handlers/Routes)
│   ├── ExpenseApiConstants.kt            # Route constants and resource names
│   ├── ExpenseApiHandler.kt              # Request handlers (ServerRequest → ServerResponse)
│   └── ExpenseApiResource.kt             # Router function definitions
│
├── dto/                                   # Data Transfer Objects (Commands, Queries, Responses)
│   └── ExpenseDto.kt                     # All DTOs for expense operations
│
├── domain/                                # Domain Layer (Core Business Logic)
│   ├── entity/                           # Domain entities (business models)
│   │   ├── Expense.kt                    # Domain entity
│   │   └── Approval.kt                   # Domain entity
│   ├── repository/                       # Repository interfaces (ports)
│   │   ├── ExpenseRepository.kt          # Repository interface (port)
│   │   └── ApprovalRepository.kt         # Repository interface (port)
│   └── service/                          # Domain services (validation, business rules)
│       ├── ExpenseValidationService.kt   # Validation service interface
│       └── impl/
│           └── ExpenseValidationServiceImpl.kt
│
└── infrastructure/                        # Infrastructure Layer (Implementations)
    ├── actions/                          # Write operations (create, update, delete)
    │   ├── ExpenseSubmitAction.kt
    │   ├── ExpenseApproveAction.kt
    │   ├── ExpenseRejectAction.kt
    │   └── usecase/
    │       ├── ExpenseSubmitUseCase.kt
    │       ├── ExpenseApproveUseCase.kt
    │       └── ExpenseRejectUseCase.kt
    │
    ├── service/                          # Infrastructure services
    │   ├── ExpenseWriteService.kt        # Write operations interface
    │   ├── ExpenseReadService.kt         # Read operations interface
    │   └── impl/
    │       ├── ExpenseWriteServiceImpl.kt
    │       └── ExpenseReadServiceImpl.kt
    │
    └── persistence/                      # Repository implementations
        ├── ExpenseRepositoryAdapter.kt
        └── ApprovalRepositoryAdapter.kt
```

## Key Principles

### 1. Domain Layer (`domain/`)

The domain layer contains:

#### Entity Package (`domain/entity/`)
- **Entities**: Core business objects (`Expense.kt`, `Approval.kt`)
- **Value Objects**: Immutable domain values
- **Enums**: Domain-specific enumerations (`ExpenseStatus`)

#### Repository Package (`domain/repository/`)
- **Repository Interfaces**: Define what the domain needs (ports for hexagonal architecture)
- These are **architectural boundaries** between domain and infrastructure

#### Domain Services (`domain/service/`)
- **Validation Services**: Business rule validation
- **Domain Logic**: Complex business operations that don't belong in entities

```kotlin
domain/
├── entity/                       # ✅ Domain entities
│   ├── Expense.kt
│   └── Approval.kt
├── repository/                   # ✅ Repository interfaces (ports)
│   ├── ExpenseRepository.kt
│   └── ApprovalRepository.kt
└── service/                      # ✅ Domain services
    ├── ExpenseValidationService.kt
    └── impl/
        └── ExpenseValidationServiceImpl.kt
```

**WRONG Structure (Mulab Reference - DON'T FOLLOW):**
```kotlin
domain/
├── valueobject/                  # ❌ DON'T put entities here
│   ├── AccountDomain.kt         # ❌ WRONG - entities are not value objects
│   └── ...
└── service/
    └── AccountValidationService.kt
# ❌ Missing repository interfaces in domain
```

**WRONG Structure (Flat - Mixes Concerns):**
```kotlin
domain/
├── Expense.kt                    # ❌ Entities and ports mixed
├── Approval.kt                   # ❌ Hard to distinguish what is what
├── ExpenseRepository.kt          # ❌ No clear separation
├── ApprovalRepository.kt         # ❌ Package responsibility unclear
└── service/
    └── ...
```

### 2. DTO Layer (`dto/`)

All Data Transfer Objects go here:
- **Commands**: `SubmitExpenseCommand`, `ApproveExpenseCommand`, `RejectExpenseCommand`
- **Queries**: `ListExpensesQuery`, `GetExpenseQuery`
- **Responses**: `ExpenseDto`, `ExpenseDetailDto`, `ApprovalDto`

### 3. Infrastructure Layer (`infrastructure/`)

#### Actions (`infrastructure/actions/`)
- **Actions**: Process write commands (Create, Update, Delete)
- **Use Cases**: Orchestrate the business flow for each action

```kotlin
infrastructure/actions/
├── ExpenseSubmitAction.kt        # Action class
├── ExpenseApproveAction.kt
├── ExpenseRejectAction.kt
└── usecase/                      # Use cases orchestrate the flow
    ├── ExpenseSubmitUseCase.kt
    ├── ExpenseApproveUseCase.kt
    └── ExpenseRejectUseCase.kt
```

#### Services (`infrastructure/service/`)
- **Write Service**: Handles database writes (create, update, delete)
- **Read Service**: Handles database reads (queries)

```kotlin
infrastructure/service/
├── ExpenseWriteService.kt        # Interface
├── ExpenseReadService.kt         # Interface
└── impl/
    ├── ExpenseWriteServiceImpl.kt
    └── ExpenseReadServiceImpl.kt
```

#### Persistence (`infrastructure/persistence/`)
- **Repository Adapters**: Implement domain repository interfaces

```kotlin
infrastructure/persistence/
├── ExpenseRepositoryAdapter.kt   # Implements ExpenseRepository
└── ApprovalRepositoryAdapter.kt  # Implements ApprovalRepository
```

## Dependency Flow

```
API Layer
    ↓ calls
Infrastructure Actions/Services
    ↓ uses
Domain Services (validation)
    ↓ uses
Domain Entities & Repository Interfaces
    ↑ implemented by
Infrastructure Repository Adapters
```

## Example: Validation Flow

### 1. Domain Service (Validation)
```kotlin
// domain/service/ExpenseValidationService.kt
interface ExpenseValidationService {
    fun validateSubmitExpense(command: SubmitExpenseCommand): Mono<SubmitExpenseCommand>
}

// domain/service/impl/ExpenseValidationServiceImpl.kt
@Service
class ExpenseValidationServiceImpl : ExpenseValidationService {
    override fun validateSubmitExpense(command: SubmitExpenseCommand): Mono<SubmitExpenseCommand> {
        return Mono.just(command)
            .flatMap { cmd ->
                if (cmd.amount <= BigDecimal.ZERO) {
                    Mono.error(BusinessException("INVALID_AMOUNT", "Amount must be positive"))
                } else {
                    Mono.just(cmd)
                }
            }
    }
}
```

### 2. Use Case Uses Validation
```kotlin
// infrastructure/actions/usecase/ExpenseSubmitUseCase.kt
@Service
class ExpenseSubmitUseCase(
    private val validationService: ExpenseValidationService,  // Domain service
    private val writeService: ExpenseWriteService             // Infrastructure service
) {
    fun executeAction(command: SubmitExpenseCommand): Mono<Long> {
        return validationService.validateSubmitExpense(command)  // Validate first
            .flatMap { validatedCommand ->
                writeService.submitExpense(validatedCommand)     // Then write
            }
    }
}
```

## Comparison with Reference Structure

### Reference (Mulab Backend)
```
domain/
├── service/
│   ├── AccountValidationService.kt
│   └── impl/
│       └── AccountValidationServiceImpl.kt
└── valueobject/
    └── AccountDomain.kt              # ❌ Entities should NOT be in valueobject
```

### Corrected Structure (FinFlow)
```
domain/
├── entity/                           # ✅ Domain entities
│   ├── Expense.kt
│   └── Approval.kt
├── repository/                       # ✅ Repository interfaces (ports)
│   ├── ExpenseRepository.kt
│   └── ApprovalRepository.kt
└── service/                          # ✅ Domain services
    ├── ExpenseValidationService.kt
    └── impl/
        └── ExpenseValidationServiceImpl.kt
```

## When to Use Each Layer

### Domain Service (`domain/service/`)
Use when:
- Business rule validation
- Complex domain logic that doesn't fit in an entity
- Cross-entity business rules
- Domain-specific calculations

Examples:
- `validateExpenseAmount()`
- `checkExpenseLimit()`
- `canUserApproveExpense()`

### Infrastructure Service (`infrastructure/service/`)
Use when:
- Database operations (CRUD)
- External system integration
- Data mapping and transformation
- Orchestrating repository calls

Examples:
- `submitExpense()` - writes to DB
- `fetchExpenseById()` - reads from DB
- `updateExpenseStatus()` - updates DB

## File Organization Rules

1. ✅ **Entities**: `domain/entity/Entity.kt`
2. ✅ **Repository Interfaces**: `domain/repository/EntityRepository.kt`
3. ✅ **Domain Services**: `domain/service/ServiceName.kt`
4. ✅ **Service Implementations**: `domain/service/impl/ServiceNameImpl.kt`
5. ❌ **Never**: `domain/valueobject/Entity.kt` (entities are not value objects!)
6. ❌ **Never**: Mix entities and repositories in the same package (separation of concerns)

## Migration Checklist

If you have entities at domain root or in `valueobject/` folder:

- [x] Create `domain/entity/` package
- [x] Create `domain/repository/` package
- [x] Move entities to `domain/entity/`
- [x] Move repository interfaces to `domain/repository/`
- [x] Keep domain services in `domain/service/`
- [x] Update all imports across the codebase
- [x] Delete old files from domain root
- [x] Delete empty `valueobject/` folder if exists

## Summary

**Correct Structure:**
```
domain/
├── entity/
│   └── [Domain entities]
├── repository/
│   └── [Repository interfaces - ports]
└── service/
    ├── [Validation services]
    └── impl/
        └── [Service implementations]
```

**What Goes Where:**
- **Entities** → `domain/entity/`
- **Repository Interfaces (Ports)** → `domain/repository/`
- **Domain Services** → `domain/service/`
- **DTOs** → `dto/`
- **Actions** → `infrastructure/actions/`
- **Use Cases** → `infrastructure/actions/usecase/`
- **Infrastructure Services** → `infrastructure/service/`
- **Repository Implementations (Adapters)** → `infrastructure/persistence/`
