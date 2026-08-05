# FinFlow - Clean Architecture Documentation

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Folder Structure](#folder-structure)
3. [Layer Responsibilities](#layer-responsibilities)
4. [Request Flow](#request-flow)
5. [Naming Conventions](#naming-conventions)
6. [Adding New Features](#adding-new-features)
7. [Best Practices](#best-practices)

---

## Architecture Overview

FinFlow follows **Clean Architecture** principles with a **Resource-Based** organization. This means:

- **Each business capability (resource) is self-contained** with its own domain, application, and infrastructure layers
- **Shared concerns** (User, Company, etc.) live in a `shared/` folder
- **Dependencies point inward**: Infrastructure → Application → Domain
- **Domain layer has zero dependencies** on frameworks or external libraries

### Why Clean Architecture?

1. **Testability**: Business logic can be tested without databases or HTTP
2. **Maintainability**: Clear separation of concerns makes code easier to understand
3. **Flexibility**: Easy to swap infrastructure (databases, APIs) without touching business logic
4. **Scalability**: Resource-based organization allows teams to work independently

---

## Folder Structure

```
src/main/kotlin/com/alsatech/finflow/
│
├── resource/                          # Business Capabilities (Self-Contained)
│   └── expense/                       # Expense Management Resource
│       ├── domain/                    # ← Core Business Logic (No Dependencies)
│       │   ├── Expense.kt            # Entity
│       │   ├── Approval.kt           # Entity
│       │   ├── ExpenseStatus.kt      # Enum
│       │   ├── ExpenseRepository.kt  # Port (Interface)
│       │   └── ApprovalRepository.kt # Port (Interface)
│       │
│       ├── application/               # ← Use Cases & DTOs
│       │   ├── usecase/
│       │   │   ├── SubmitExpenseUseCase.kt
│       │   │   ├── ApproveExpenseUseCase.kt
│       │   │   ├── RejectExpenseUseCase.kt
│       │   │   ├── GetExpenseUseCase.kt
│       │   │   └── ListExpensesUseCase.kt
│       │   └── dto/
│       │       └── ExpenseDto.kt      # Commands, Queries, Responses
│       │
│       ├── infrastructure/            # ← External Adapters (Depends on Domain)
│       │   └── persistence/
│       │       ├── ExpenseRepositoryAdapter.kt
│       │       └── ApprovalRepositoryAdapter.kt
│       │
│       └── api/                       # ← HTTP Interface (Depends on Application)
│           └── ExpenseController.kt   # REST Endpoints
│
├── shared/                            # Cross-Cutting Concerns
│   ├── domain/                        # Shared Entities & Repositories
│   │   ├── User.kt
│   │   ├── Company.kt
│   │   ├── Account.kt
│   │   ├── Transaction.kt
│   │   ├── Category.kt
│   │   ├── Invoice.kt
│   │   └── *Repository.kt             # Repository interfaces
│   │
│   └── infrastructure/                # Shared Infrastructure
│       ├── persistence/               # Repository Adapters
│       │   └── *RepositoryAdapter.kt
│       ├── config/                    # Spring Configuration
│       ├── security/                  # Security & JWT
│       └── external/                  # External APIs (LLM, Email, Storage)

```

---

## Layer Responsibilities

### 1. **Domain Layer** (Core Business Logic)
**Location**: `resource/*/domain/` and `shared/domain/`

**What belongs here:**
- **Entities**: Business objects (Expense, User, Company)
- **Value Objects**: Immutable types (ExpenseStatus, Role)
- **Repository Interfaces (Ports)**: Contracts for data access
- **Business Rules**: Domain-specific validation logic

**Rules:**
- ✅ **NO external dependencies** (no Spring, no database, no HTTP)
- ✅ Pure Kotlin/Java code
- ✅ Contains business rules and invariants

**Example:**
```kotlin
@Table("expenses")
data class Expense(
    @Id val id: Long? = null,
    val userId: Long,
    val amount: BigDecimal,
    val status: ExpenseStatus = ExpenseStatus.PENDING
)

interface ExpenseRepository {
    fun findById(id: Long): Mono<Expense>
    fun save(expense: Expense): Mono<Expense>
}
```

---

### 2. **Application Layer** (Use Cases & Orchestration)
**Location**: `resource/*/application/`

**What belongs here:**
- **Use Cases**: Application-specific business logic (SubmitExpenseUseCase)
- **DTOs (Data Transfer Objects)**: Commands, Queries, Responses
- **Application Services**: Coordinating multiple use cases

**Rules:**
- ✅ Depends ONLY on Domain Layer
- ✅ Orchestrates domain objects
- ✅ No database or HTTP details
- ✅ Transaction boundaries (@Transactional)

**Example:**
```kotlin
@Service
@Transactional
class SubmitExpenseUseCase(
    private val expenseRepository: ExpenseRepository
) {
    fun execute(command: SubmitExpenseCommand): Mono<Long> {
        // Validate business rules
        // Create domain entity
        // Save via repository
        // Return result
    }
}
```

---

### 3. **Infrastructure Layer** (External Adapters)
**Location**: `resource/*/infrastructure/` and `shared/infrastructure/`

**What belongs here:**
- **Repository Adapters**: Implementations of repository interfaces
- **Database Access**: R2DBC, SQL queries
- **External APIs**: HTTP clients, third-party services
- **Configuration**: Spring beans, database config

**Rules:**
- ✅ Implements interfaces from Domain
- ✅ Contains framework-specific code
- ✅ Uses R2dbcEntityTemplate, DatabaseClient

**Example:**
```kotlin
@Repository
class ExpenseRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : ExpenseRepository {
    override fun findById(id: Long): Mono<Expense> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.selectOne(query, Expense::class.java)
    }
}
```

---

### 4. **API Layer** (HTTP Interface)
**Location**: `resource/*/api/`

**What belongs here:**
- **Controllers**: REST endpoints
- **Request/Response mapping**: HTTP → Commands/Queries
- **HTTP-specific concerns**: Status codes, headers

**Rules:**
- ✅ Depends ONLY on Application Layer (Use Cases)
- ✅ No business logic
- ✅ Thin layer - just delegates to use cases

**Example:**
```kotlin
@RestController
@RequestMapping("/api/expenses")
class ExpenseController(
    private val submitExpenseUseCase: SubmitExpenseUseCase
) {
    @PostMapping
    fun submitExpense(@Valid @RequestBody command: SubmitExpenseCommand): Mono<Map<String, Long>> {
        return submitExpenseUseCase.execute(command)
            .map { expenseId -> mapOf("expenseId" to expenseId) }
    }
}
```

---

## Request Flow

### Example: Submitting an Expense

```
1. HTTP Request
   ↓
2. ExpenseController (API Layer)
   ├─ Receives SubmitExpenseCommand
   ├─ Validates input (@Valid)
   └─ Calls SubmitExpenseUseCase
   ↓
3. SubmitExpenseUseCase (Application Layer)
   ├─ Validates business rules
   ├─ Creates Expense entity
   └─ Calls ExpenseRepository.save()
   ↓
4. ExpenseRepositoryAdapter (Infrastructure Layer)
   ├─ Uses R2dbcEntityTemplate
   ├─ Inserts into database
   └─ Returns saved Expense
   ↓
5. Response flows back up
   └─ SubmitExpenseUseCase → ExpenseController → HTTP Response
```

### Diagram

```
┌─────────────────────────────────────────────────────────┐
│                     API Layer                           │
│  ExpenseController (HTTP Endpoints)                     │
└────────────────┬────────────────────────────────────────┘
                 │ Depends on
                 ↓
┌─────────────────────────────────────────────────────────┐
│                 Application Layer                       │
│  Use Cases (Business Workflows)                         │
│  DTOs (Commands, Queries, Responses)                    │
└────────────────┬────────────────────────────────────────┘
                 │ Depends on
                 ↓
┌─────────────────────────────────────────────────────────┐
│                   Domain Layer                          │
│  Entities (Expense, Approval)                           │
│  Repository Interfaces (Ports)                          │
│  Business Rules                                         │
└────────────────┬────────────────────────────────────────┘
                 ↑ Implements
                 │
┌─────────────────────────────────────────────────────────┐
│              Infrastructure Layer                       │
│  Repository Adapters (R2DBC)                            │
│  Database Access                                        │
│  External Services                                      │
└─────────────────────────────────────────────────────────┘
```

---

## Naming Conventions

### Files & Classes

| Type | Naming Pattern | Example |
|------|---------------|---------|
| **Entities** | Noun | `Expense`, `User`, `Company` |
| **Repositories (Interface)** | `{Entity}Repository` | `ExpenseRepository`, `UserRepository` |
| **Repository Adapters** | `{Entity}RepositoryAdapter` | `ExpenseRepositoryAdapter` |
| **Use Cases** | `{Verb}{Entity}UseCase` | `SubmitExpenseUseCase`, `ApproveExpenseUseCase` |
| **Commands** | `{Verb}{Entity}Command` | `SubmitExpenseCommand`, `ApproveExpenseCommand` |
| **Queries** | `{Verb/Get}{Entity}Query` | `GetExpenseQuery`, `ListExpensesQuery` |
| **Response DTOs** | `{Entity}Dto` | `ExpenseDto`, `ExpenseDetailDto` |
| **Controllers** | `{Entity}Controller` | `ExpenseController`, `UserController` |
| **Enums** | Noun (Singular) | `ExpenseStatus`, `Role`, `AccountType` |

### Packages

| Layer | Package Pattern | Example |
|-------|----------------|---------|
| **Domain** | `resource.{resource}.domain` | `resource.expense.domain` |
| **Application** | `resource.{resource}.application.usecase` | `resource.expense.application.usecase` |
| **DTOs** | `resource.{resource}.application.dto` | `resource.expense.application.dto` |
| **Infrastructure** | `resource.{resource}.infrastructure.persistence` | `resource.expense.infrastructure.persistence` |
| **API** | `resource.{resource}.api` | `resource.expense.api` |
| **Shared Domain** | `shared.domain` | `shared.domain` |
| **Shared Infrastructure** | `shared.infrastructure.{type}` | `shared.infrastructure.persistence` |

---

## Adding New Features

### Scenario: Add "Invoice" Resource

**Step 1: Create Domain Layer**
```
resource/invoice/domain/
  ├── Invoice.kt                  # Entity
  ├── InvoiceStatus.kt            # Enum
  └── InvoiceRepository.kt        # Port
```

**Step 2: Create Application Layer**
```
resource/invoice/application/
  ├── usecase/
  │   ├── CreateInvoiceUseCase.kt
  │   ├── SendInvoiceUseCase.kt
  │   └── MarkInvoicePaidUseCase.kt
  └── dto/
      └── InvoiceDto.kt
```

**Step 3: Create Infrastructure Layer**
```
resource/invoice/infrastructure/
  └── persistence/
      └── InvoiceRepositoryAdapter.kt
```

**Step 4: Create API Layer**
```
resource/invoice/api/
  └── InvoiceController.kt
```

---

## Best Practices

### 1. **Keep Use Cases Focused**
- ✅ One use case = one business operation
- ✅ Use case names should be verb-based: `SubmitExpense`, `ApproveExpense`
- ❌ Don't create generic "Service" classes

### 2. **Use Commands and Queries (CQRS Lite)**
- **Commands**: Change state (`SubmitExpenseCommand`)
- **Queries**: Read data (`GetExpenseQuery`)
- Return different DTOs for reads vs writes

### 3. **Repository Pattern**
- Repository interfaces in **domain** layer
- Implementations in **infrastructure** layer
- Use R2dbcEntityTemplate for CRUD
- Use DatabaseClient for complex joins

### 4. **Validation Layers**
| Layer | Validation Type | Example |
|-------|----------------|---------|
| **API (Controller)** | Input format | `@Valid`, `@NotNull`, `@Positive` |
| **Use Case** | Business rules | "Cannot approve own expense" |
| **Domain** | Invariants | "Amount must be positive" |

### 5. **Error Handling**
```kotlin
// Use domain exceptions
throw BusinessException("EXPENSE_NOT_FOUND", "Expense not found with ID: $id")

// Let global exception handler manage HTTP responses
```

### 6. **Transaction Management**
- Add `@Transactional` to use cases that modify data
- Keep transactions as short as possible
- Don't call external APIs inside transactions

### 7. **Reactive Patterns**
- Use `Mono<T>` for single results
- Use `Flux<T>` for multiple results
- Never block reactive streams (no `.block()`)

---

## Example: Complete Feature Flow

### Feature: Submit Expense

**1. Domain Entity**
```kotlin
// resource/expense/domain/Expense.kt
data class Expense(
    val id: Long? = null,
    val userId: Long,
    val amount: BigDecimal,
    val status: ExpenseStatus = ExpenseStatus.PENDING
)
```

**2. Command DTO**
```kotlin
// resource/expense/application/dto/ExpenseDto.kt
data class SubmitExpenseCommand(
    @field:NotNull val userId: Long,
    @field:NotNull val amount: BigDecimal
)
```

**3. Use Case**
```kotlin
// resource/expense/application/usecase/SubmitExpenseUseCase.kt
@Service
@Transactional
class SubmitExpenseUseCase(
    private val expenseRepository: ExpenseRepository
) {
    fun execute(command: SubmitExpenseCommand): Mono<Long> {
        val expense = Expense(
            userId = command.userId,
            amount = command.amount
        )
        return expenseRepository.save(expense)
            .map { it.id!! }
    }
}
```

**4. Repository Adapter**
```kotlin
// resource/expense/infrastructure/persistence/ExpenseRepositoryAdapter.kt
@Repository
class ExpenseRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : ExpenseRepository {
    override fun save(expense: Expense): Mono<Expense> {
        return template.insert(Expense::class.java).using(expense)
    }
}
```

**5. Controller**
```kotlin
// resource/expense/api/ExpenseController.kt
@RestController
@RequestMapping("/api/expenses")
class ExpenseController(
    private val submitExpenseUseCase: SubmitExpenseUseCase
) {
    @PostMapping
    fun submitExpense(@Valid @RequestBody command: SubmitExpenseCommand): Mono<Map<String, Long>> {
        return submitExpenseUseCase.execute(command)
            .map { id -> mapOf("expenseId" to id) }
    }
}
```

---

## Technology Stack

- **Language**: Kotlin 2.0.20
- **Framework**: Spring Boot 3.4.1 (WebFlux - Reactive)
- **Database**: PostgreSQL with R2DBC (Reactive)
- **Architecture**: Clean Architecture + Resource-Based
- **ID Type**: Long (auto-increment)

---

## Migration from Old Structure

The old structure (`domain/`, `application/`, `infrastructure/`, `interfaces/`) is being deprecated.

**Migration Steps:**
1. Identify the resource (e.g., Expense, Invoice)
2. Move entities to `resource/{resource}/domain/`
3. Move repositories to `resource/{resource}/domain/`
4. Simplify use cases (remove Action/UseCase/Service layers)
5. Move DTOs to `resource/{resource}/application/dto/`
6. Move adapters to `resource/{resource}/infrastructure/persistence/`
7. Rename controllers to `resource/{resource}/api/`

---

## Questions?

**Q: Where do I put cross-cutting concerns?**
A: In `shared/` folder (User, Company, Category, etc.)

**Q: Should I create separate read/write services?**
A: No, use cases should handle both. Avoid Read/Write service separation.

**Q: Where do validation rules go?**
A: Business rules in use cases, input validation in DTOs, invariants in entities.

**Q: How do I handle complex queries with joins?**
A: Use DatabaseClient in repository adapter for custom SQL.

**Q: Can use cases call other use cases?**
A: Generally no - compose them in a higher-level use case or controller.

---

**Last Updated**: 2026-08-05
**Author**: FinFlow Development Team
