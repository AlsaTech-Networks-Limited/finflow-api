# 🎯 Why Repository Interfaces Are in Domain Layer

## The Fundamental Question

**"Why is the repository in the domain? Shouldn't it be in infrastructure since it deals with the database?"**

**Short Answer:** The repository **INTERFACE** is in domain. The repository **IMPLEMENTATION** is in infrastructure.

---

## 🔑 The Core Concept: Dependency Inversion Principle

### ❌ Traditional Approach (WRONG)

```
┌─────────────────────────────────────┐
│         Application Layer           │
│                                     │
│  SubmitExpenseUseCase               │
│    ↓ depends on                     │
└─────────────────────────────────────┘
            ↓
┌─────────────────────────────────────┐
│      Infrastructure Layer           │
│                                     │
│  ExpenseRepositoryImpl              │ ← Implementation
│    - save(expense)                  │
│    - Uses database                  │
└─────────────────────────────────────┘
```

**Problem:**
- ❌ Application layer **depends on** infrastructure
- ❌ Can't test without database
- ❌ Hard to swap database implementations
- ❌ **Violates Clean Architecture principles**

---

### ✅ Clean Architecture Approach (CORRECT)

```
┌─────────────────────────────────────┐
│          Domain Layer               │
│                                     │
│  ExpenseRepository (INTERFACE)      │ ← Port/Contract
│    - save(expense): Mono<Expense>   │
│                                     │
│  Expense (ENTITY)                   │
│    - id, amount, status             │
└─────────────────────────────────────┘
            ↑ implements
            │
┌─────────────────────────────────────┐
│         Application Layer           │
│                                     │
│  SubmitExpenseUseCase               │
│    - expenseRepository: ExpenseRepository
│    - Uses INTERFACE                 │
└─────────────────────────────────────┘
            ↑ implements
            │
┌─────────────────────────────────────┐
│      Infrastructure Layer           │
│                                     │
│  ExpenseRepositoryAdapter           │ ← Implementation
│    implements ExpenseRepository     │
│    - Uses R2dbcEntityTemplate       │
│    - Knows about database           │
└─────────────────────────────────────┘
```

**Benefits:**
- ✅ Domain defines what it needs (interface)
- ✅ Infrastructure provides what domain needs
- ✅ Can test with mock repositories
- ✅ Can swap database easily
- ✅ **Follows Dependency Inversion Principle**

---

## 📚 Hexagonal Architecture (Ports & Adapters)

The repository interface is called a **PORT** in Hexagonal Architecture.

### Ports & Adapters Explained

```
┌──────────────────────────────────────────────────┐
│                                                  │
│              DOMAIN LAYER (Core)                 │
│                                                  │
│  ┌────────────────────────────────────┐         │
│  │  Expense (Entity)                  │         │
│  │  - Business rules                  │         │
│  │  - Pure logic                      │         │
│  └────────────────────────────────────┘         │
│                                                  │
│  ┌────────────────────────────────────┐         │
│  │  ExpenseRepository (PORT)          │ ← Interface
│  │  - What domain NEEDS                │
│  │  - Doesn't know HOW                │
│  └────────────────────────────────────┘         │
│                                                  │
└──────────────────────────────────────────────────┘
                    ↑
                    │ implements
                    │
┌──────────────────────────────────────────────────┐
│                                                  │
│         INFRASTRUCTURE LAYER (Outside)           │
│                                                  │
│  ┌────────────────────────────────────┐         │
│  │  ExpenseRepositoryAdapter (ADAPTER)│         │
│  │  - HOW to implement the port       │         │
│  │  - R2DBC, SQL, Database            │         │
│  │  - Framework specific              │         │
│  └────────────────────────────────────┘         │
│                                                  │
└──────────────────────────────────────────────────┘
```

**Key Points:**
- **PORT** = Interface in domain (what domain needs)
- **ADAPTER** = Implementation in infrastructure (how to provide it)
- Domain doesn't know HOW data is stored
- Infrastructure adapts to domain's needs

---

## 🎓 Real-World Example

### Domain Layer (Defines What It Needs)

```kotlin
// resource/expense/domain/ExpenseRepository.kt
package com.alsatech.finflow.resource.expense.domain

import reactor.core.publisher.Mono

/**
 * PORT - Domain defines what it needs
 * Domain doesn't care HOW this is implemented
 * Could be database, file, memory, API - doesn't matter!
 */
interface ExpenseRepository {
    fun save(expense: Expense): Mono<Expense>
    fun findById(id: Long): Mono<Expense>
}
```

**Key Points:**
- ✅ Pure interface - no implementation
- ✅ Domain language (save, findById)
- ✅ No database details (no SQL, no R2DBC)
- ✅ Domain OWNS this contract

---

### Application Layer (Uses What Domain Provides)

```kotlin
// resource/expense/application/usecase/SubmitExpenseUseCase.kt
package com.alsatech.finflow.resource.expense.application.usecase

import com.alsatech.finflow.resource.expense.domain.Expense
import com.alsatech.finflow.resource.expense.domain.ExpenseRepository
import reactor.core.publisher.Mono

@Service
class SubmitExpenseUseCase(
    private val expenseRepository: ExpenseRepository  // ← Uses INTERFACE
) {
    fun execute(command: SubmitExpenseCommand): Mono<Long> {
        val expense = Expense(...)

        // Uses the interface - doesn't know if it's:
        // - PostgreSQL database
        // - MongoDB
        // - In-memory storage
        // - File system
        // - External API
        return expenseRepository.save(expense)
            .map { it.id!! }
    }
}
```

**Key Points:**
- ✅ Depends on **interface**, not implementation
- ✅ Testable with mock repository
- ✅ No knowledge of database

---

### Infrastructure Layer (Implements What Domain Needs)

```kotlin
// resource/expense/infrastructure/persistence/ExpenseRepositoryAdapter.kt
package com.alsatech.finflow.resource.expense.infrastructure.persistence

import com.alsatech.finflow.resource.expense.domain.Expense
import com.alsatech.finflow.resource.expense.domain.ExpenseRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

/**
 * ADAPTER - Infrastructure implements domain's contract
 * This is where database details live
 */
@Repository
class ExpenseRepositoryAdapter(
    private val template: R2dbcEntityTemplate  // ← Framework specific
) : ExpenseRepository {  // ← Implements domain interface

    override fun save(expense: Expense): Mono<Expense> {
        // Implementation details - domain doesn't care about this
        return if (expense.id == null) {
            template.insert(Expense::class.java).using(expense)
        } else {
            template.update(expense)
        }
    }

    override fun findById(id: Long): Mono<Expense> {
        val query = Query.query(Criteria.where("id").`is`(id))
        return template.selectOne(query, Expense::class.java)
    }
}
```

**Key Points:**
- ✅ Implements domain interface
- ✅ Contains ALL database logic
- ✅ Uses framework-specific code (R2DBC, Spring)
- ✅ Can be swapped without changing domain

---

## 🧪 Testing Benefits

### Without Domain Repository Interface (WRONG)

```kotlin
// ❌ Can't test without database
@Test
fun `submit expense test`() {
    val database = startPostgresContainer()  // ← Need real DB!
    val repository = ExpenseRepositoryImpl(database)
    val useCase = SubmitExpenseUseCase(repository)

    // Test is slow and complex
}
```

### With Domain Repository Interface (CORRECT)

```kotlin
// ✅ Easy to test with mock
@Test
fun `submit expense test`() {
    // Mock the interface
    val mockRepository = mock<ExpenseRepository> {
        on { save(any()) } doReturn Mono.just(Expense(id = 1L, ...))
    }

    val useCase = SubmitExpenseUseCase(mockRepository)

    // Fast, simple test - no database needed!
    val result = useCase.execute(command).block()

    assertThat(result).isEqualTo(1L)
}
```

---

## 🔄 Swapping Implementations

Because the interface is in domain, you can **easily swap implementations**:

### Option 1: PostgreSQL (Current)

```kotlin
@Repository
class ExpenseRepositoryAdapter(
    private val template: R2dbcEntityTemplate
) : ExpenseRepository {
    // PostgreSQL implementation
}
```

### Option 2: MongoDB (Easy to Add)

```kotlin
@Repository
@Profile("mongodb")
class ExpenseMongoRepositoryAdapter(
    private val mongoTemplate: ReactiveMongoTemplate
) : ExpenseRepository {
    // MongoDB implementation
}
```

### Option 3: In-Memory (For Testing)

```kotlin
@Repository
@Profile("test")
class InMemoryExpenseRepository : ExpenseRepository {
    private val expenses = ConcurrentHashMap<Long, Expense>()

    override fun save(expense: Expense): Mono<Expense> {
        val id = expenses.size + 1L
        val saved = expense.copy(id = id)
        expenses[id] = saved
        return Mono.just(saved)
    }
}
```

**The use case doesn't change at all!** ✅

---

## 📊 Dependency Direction

### The Key Rule: Dependencies Point Inward

```
┌────────────────────────────────────────┐
│         API Layer                      │
│         (Controllers)                  │
└────────────────────────────────────────┘
                ↓ depends on
┌────────────────────────────────────────┐
│      Application Layer                 │
│      (Use Cases)                       │
└────────────────────────────────────────┘
                ↓ depends on
┌────────────────────────────────────────┐
│         Domain Layer                   │  ← CENTER
│   - Entities                           │  ← No dependencies
│   - Repository Interfaces (PORTS)      │  ← Defines contracts
└────────────────────────────────────────┘
                ↑ implements
┌────────────────────────────────────────┐
│    Infrastructure Layer                │
│    - Repository Adapters (ADAPTERS)    │
│    - Database, APIs, External          │
└────────────────────────────────────────┘
```

**Critical Point:**
- ❌ Domain NEVER depends on Infrastructure
- ✅ Domain defines what it needs (interface)
- ✅ Infrastructure implements what domain needs
- ✅ This is called **Dependency Inversion**

---

## 🎯 Common Confusion

### Question: "But the repository deals with database, so shouldn't it be in infrastructure?"

**Answer:** The repository **IMPLEMENTATION** is in infrastructure. The repository **INTERFACE** is in domain.

### Analogy: Power Socket

Think of it like a power socket in your house:

**Domain (Interface):**
```kotlin
interface PowerSocket {
    fun providePower(): Electricity
}
```
- Your lamp (domain) defines what it needs: a power socket
- Your lamp doesn't care WHERE the electricity comes from
- Could be solar, wind, nuclear, coal - doesn't matter!

**Infrastructure (Implementation):**
```kotlin
class CoalPowerPlant : PowerSocket {
    override fun providePower(): Electricity {
        // Burn coal, generate electricity
    }
}

class SolarPowerPlant : PowerSocket {
    override fun providePower(): Electricity {
        // Capture sun, generate electricity
    }
}
```
- Infrastructure provides the electricity
- Infrastructure knows HOW to generate power
- Lamp (domain) just uses the socket (interface)

**Same with Repository:**
- Domain defines: "I need to save expenses"
- Infrastructure provides: "I'll use PostgreSQL"
- Domain doesn't care HOW expenses are saved

---

## 📋 Checklist: Where Does It Go?

| Item | Location | Reason |
|------|----------|--------|
| **Repository Interface** | Domain | ✅ Domain defines what it needs |
| **Repository Implementation** | Infrastructure | ✅ Infrastructure provides how |
| **Entity (Expense)** | Domain | ✅ Core business object |
| **Use Case** | Application | ✅ Business workflow |
| **Controller** | API | ✅ HTTP interface |
| **R2dbcEntityTemplate** | Infrastructure | ✅ Framework specific |
| **Database queries (SQL)** | Infrastructure | ✅ Implementation detail |

---

## 🎓 The SOLID Principle Involved

This is the **"D" in SOLID**: Dependency Inversion Principle

### Dependency Inversion Principle States:

1. **High-level modules should not depend on low-level modules. Both should depend on abstractions.**
   - ✅ Use Case (high-level) doesn't depend on Repository Adapter (low-level)
   - ✅ Both depend on Repository Interface (abstraction)

2. **Abstractions should not depend on details. Details should depend on abstractions.**
   - ✅ Repository Interface (abstraction) doesn't know about database (detail)
   - ✅ Repository Adapter (detail) implements Repository Interface (abstraction)

### Without Dependency Inversion (WRONG)

```kotlin
// ❌ Use Case depends on concrete implementation
class SubmitExpenseUseCase(
    private val repository: ExpenseRepositoryAdapter  // Concrete class
) {
    // Tightly coupled to PostgreSQL implementation
}
```

### With Dependency Inversion (CORRECT)

```kotlin
// ✅ Use Case depends on abstraction
class SubmitExpenseUseCase(
    private val repository: ExpenseRepository  // Interface
) {
    // Loosely coupled - can use any implementation
}
```

---

## 🌟 Real-World Benefits

### 1. Easy Testing
```kotlin
// Mock the interface, not the database
val mockRepo = mock<ExpenseRepository>()
val useCase = SubmitExpenseUseCase(mockRepo)
```

### 2. Easy Swapping
```kotlin
// Switch from PostgreSQL to MongoDB
@Repository
class ExpenseMongoAdapter : ExpenseRepository {
    // Different implementation, same interface
}
```

### 3. Clear Boundaries
```kotlin
// Domain layer has ZERO dependencies
// Can be extracted to separate module
module domain {
    exports expense.domain
    // No Spring, no R2DBC, no database imports!
}
```

### 4. Business Logic Isolation
```kotlin
// Domain contains pure business logic
// No framework pollution
data class Expense(
    val amount: BigDecimal
) {
    fun isLarge(): Boolean = amount > BigDecimal(1000)
    // Pure business logic - no database, no framework
}
```

---

## 📖 Summary

### Why Repository Interface is in Domain:

1. ✅ **Domain defines what it needs** (the contract/port)
2. ✅ **Infrastructure provides what domain needs** (the implementation/adapter)
3. ✅ **Testable** (mock the interface)
4. ✅ **Flexible** (swap implementations)
5. ✅ **Clean** (domain has zero dependencies)
6. ✅ **Follows SOLID** (Dependency Inversion Principle)

### The Golden Rule:

> **"Domain defines the interface. Infrastructure implements the interface."**

### Visual Summary:

```
Domain Layer (Core):
├── Expense.kt (Entity)
└── ExpenseRepository.kt (INTERFACE/PORT)  ← "I need to save expenses"

Infrastructure Layer (Outside):
└── ExpenseRepositoryAdapter.kt (IMPLEMENTATION/ADAPTER)  ← "I'll use PostgreSQL"
```

---

## 🎯 Key Takeaways

1. **Repository = Two Parts:**
   - Interface (PORT) in domain
   - Implementation (ADAPTER) in infrastructure

2. **Dependency Direction:**
   - Domain defines contracts
   - Infrastructure implements contracts
   - Dependencies point INWARD to domain

3. **Benefits:**
   - Testable
   - Flexible
   - Clean
   - Maintainable

4. **Remember:**
   - Domain knows WHAT it needs
   - Infrastructure knows HOW to provide it
   - Domain doesn't care about HOW

---

**This is the essence of Clean Architecture and Hexagonal Architecture!**

---

**Last Updated:** 2026-08-05
**Topic:** Clean Architecture - Dependency Inversion
**Principle:** SOLID - "D" (Dependency Inversion Principle)
