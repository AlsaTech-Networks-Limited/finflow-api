# Domain Entity Organization Patterns

## Question: How to organize Expense and Approval entities?

Current structure (UPDATED - RECOMMENDED):
```
domain/
├── entity/
│   ├── Expense.kt
│   └── Approval.kt
├── repository/
│   ├── ExpenseRepository.kt
│   └── ApprovalRepository.kt
└── service/
```

Let's explore different organization patterns and why the current one is optimal.

## Pattern 1: Organized Structure (CURRENT - ✅ RECOMMENDED for all cases)

```
domain/
├── entity/                       # Domain entities
│   ├── Expense.kt               # Main entity
│   └── Approval.kt              # Related entity
├── repository/                   # Repository interfaces (ports)
│   ├── ExpenseRepository.kt     # Repository interface
│   └── ApprovalRepository.kt    # Repository interface
└── service/                      # Domain services
    ├── ExpenseValidationService.kt
    └── impl/
        └── ExpenseValidationServiceImpl.kt
```

**Pros:**
- ✅ Clear separation of concerns (entities vs ports vs services)
- ✅ Professional standard for Clean Architecture / DDD
- ✅ Easy to navigate - each package has clear responsibility
- ✅ Scales well as domain grows
- ✅ Follows Hexagonal Architecture (repository interfaces are ports)
- ✅ No confusion between entities and architectural boundaries

**Cons:**
- None for professional applications

**When to use:**
- **ALWAYS** - This is the professional standard
- Any resource with domain entities
- Projects following Clean Architecture / DDD

**Example:**
```kotlin
// domain/entity/Expense.kt
package com.alsatech.finflow.resource.expense.domain.entity
data class Expense(val id: Long, ...)

// domain/entity/Approval.kt
package com.alsatech.finflow.resource.expense.domain.entity
data class Approval(val id: Long, val expenseId: Long, ...)

// domain/repository/ExpenseRepository.kt
package com.alsatech.finflow.resource.expense.domain.repository
interface ExpenseRepository { ... }
```

---

## Pattern 2: Flat Structure (OLD - ❌ NOT RECOMMENDED - Mixes Concerns)

```
domain/
├── Expense.kt                    # Entity
├── Approval.kt                   # Entity
├── ExpenseRepository.kt          # Repository interface (Port)
├── ApprovalRepository.kt         # Repository interface (Port)
└── service/
    ├── ExpenseValidationService.kt
    └── impl/
        └── ExpenseValidationServiceImpl.kt
```

**Pros:**
- ✅ Simple and straightforward
- ✅ Fewer folders

**Cons:**
- ❌ **Mixes domain models with architectural boundaries (ports)**
- ❌ Package responsibility is unclear
- ❌ Hard to distinguish entities from repository interfaces
- ❌ Doesn't follow Clean Architecture best practices
- ❌ Can get cluttered with many entities (5+)
- ❌ Doesn't show clear separation of concerns

**When to use:**
- ❌ **DON'T USE** - This pattern mixes concerns

---

## Pattern 3: Entity-Grouped Structure

```
domain/
├── expense/
│   ├── Expense.kt
│   └── ExpenseRepository.kt
├── approval/
│   ├── Approval.kt
│   └── ApprovalRepository.kt
└── service/
    ├── ExpenseValidationService.kt
    └── impl/
        └── ExpenseValidationServiceImpl.kt
```

**Pros:**
- ✅ Clear separation by entity
- ✅ Scales well with many entities
- ✅ Easy to find entity and its repository together

**Cons:**
- ❌ More folders/nesting
- ❌ Doesn't show entity relationships
- ❌ Overkill for 2-3 entities

**When to use:**
- Resources with 5+ entities
- Complex domains with many entities
- When each entity is independent

---

## Pattern 4: Aggregate-Based Structure (✅ GOOD for complex aggregates)

```
domain/
├── aggregate/
│   └── expense/                  # Expense is the aggregate root
│       ├── Expense.kt           # Aggregate root
│       └── Approval.kt          # Part of expense aggregate
├── repository/
│   ├── ExpenseRepository.kt
│   └── ApprovalRepository.kt
└── service/
    ├── ExpenseValidationService.kt
    └── impl/
        └── ExpenseValidationServiceImpl.kt
```

**Pros:**
- ✅ Shows entity relationships clearly
- ✅ Follows DDD aggregate pattern
- ✅ Groups related entities together
- ✅ Indicates that Approval belongs to Expense

**Cons:**
- ❌ More complex structure
- ❌ Might be overkill for simple domains

**When to use:**
- DDD-style domains with clear aggregates
- When entity relationships are important
- Complex business rules around aggregates

**Example:**
```kotlin
// domain/aggregate/expense/Expense.kt
// Aggregate root - can exist independently
data class Expense(val id: Long, ...)

// domain/aggregate/expense/Approval.kt
// Part of Expense aggregate - cannot exist without Expense
data class Approval(val expenseId: Long, ...)
```

---

## Pattern 5: Repository Model Pattern (Reference pattern - ❌ NOT DDD)

```
domain/
└── service/
    ├── ExpenseValidationService.kt
    └── impl/
        └── ExpenseValidationServiceImpl.kt

infrastructure/
└── repository/
    ├── model/                    # Entities treated as data models
    │   ├── Expense.kt
    │   └── Approval.kt
    ├── ExpenseRepository.kt
    ├── ApprovalRepository.kt
    └── impl/
        ├── ExpenseRepositoryImpl.kt
        └── ApprovalRepositoryImpl.kt
```

**Pros:**
- ✅ Entities close to their repositories
- ✅ Clear separation: domain = services, infrastructure = data

**Cons:**
- ❌ Entities in infrastructure layer (not ideal for DDD)
- ❌ Doesn't follow Clean Architecture strictly
- ❌ Harder to enforce domain rules in entities

**When to use:**
- Data-centric applications (CRUD)
- Anemic domain models
- Simple applications without complex business logic

**Note:** This is the pattern used in the Mulab reference, but it's **NOT recommended for DDD/Clean Architecture**.

---

## Comparison Table

| Pattern | Separation of Concerns | DDD-Friendly | Scalability | Professional Standard |
|---------|----------------------|--------------|-------------|---------------------|
| **Organized (entity/ + repository/)** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Flat (mixed)** | ⭐⭐ | ⭐⭐ | ⭐⭐ | ⭐ |
| **Entity-Grouped** | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| **Aggregate-Based** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Repository Model** | ⭐ | ⭐ | ⭐⭐⭐ | ⭐ |

---

## Recommendation for Expense Resource

### Current Situation:
- **2 entities**: Expense, Approval
- **Relationship**: Approval belongs to Expense (cannot exist without it)
- **Complexity**: Moderate domain with business rules

### Recommended Approach: **ORGANIZED STRUCTURE** ✅

**Why:**
1. **Clear Separation of Concerns** - Entities separate from repository interfaces (ports)
2. **Professional Standard** - Follows Clean Architecture / DDD best practices
3. **Easy to Navigate** - Each package has clear, single responsibility
4. **Scales Well** - Works for 2 entities or 20 entities
5. **Hexagonal Architecture** - Repository interfaces are clearly marked as ports

**Current structure (CORRECT):**
```
domain/
├── entity/                       # ✅ Domain entities
│   ├── Expense.kt               # ✅ Aggregate root
│   └── Approval.kt              # ✅ Child entity
├── repository/                   # ✅ Repository interfaces (ports)
│   ├── ExpenseRepository.kt     # ✅ Repository interface
│   └── ApprovalRepository.kt    # ✅ Repository interface
└── service/                      # ✅ Domain services
    ├── ExpenseValidationService.kt
    └── impl/
        └── ExpenseValidationServiceImpl.kt
```

### When to Migrate:

**Migrate to Entity-Grouped** when:
- You have 5+ entities in the domain
- Entities are mostly independent

**Migrate to Aggregate-Based** when:
- You add more child entities (ExpenseItem, ExpenseAttachment, etc.)
- Complex aggregate rules emerge
- Need to enforce aggregate boundaries

---

## Alternative: Hybrid Approach (if you add more entities)

If Expense grows to have multiple related entities:

```
domain/
├── expense/                      # Expense aggregate
│   ├── Expense.kt               # Root
│   ├── Approval.kt              # Child
│   ├── ExpenseItem.kt           # Child (if added)
│   └── ExpenseAttachment.kt     # Child (if added)
├── category/                     # Category aggregate (if complex)
│   └── Category.kt
├── repository/
│   ├── ExpenseRepository.kt
│   ├── ApprovalRepository.kt
│   └── CategoryRepository.kt
└── service/
    └── ...
```

---

## Code Examples

### Current (Organized) - ✅ RECOMMENDED
```kotlin
// domain/entity/Expense.kt
package com.alsatech.finflow.resource.expense.domain.entity

data class Expense(
    val id: Long?,
    val userId: Long,
    val amount: BigDecimal,
    ...
)

// domain/entity/Approval.kt
package com.alsatech.finflow.resource.expense.domain.entity

data class Approval(
    val id: Long?,
    val expenseId: Long,  // Shows relationship
    val approverId: Long,
    ...
)

// domain/repository/ExpenseRepository.kt
package com.alsatech.finflow.resource.expense.domain.repository

interface ExpenseRepository {
    fun findById(id: Long): Mono<Expense>
    fun save(expense: Expense): Mono<Expense>
}
```

### If using Aggregate-Based
```kotlin
// domain/aggregate/expense/Expense.kt
package com.alsatech.finflow.resource.expense.domain.aggregate.expense

data class Expense(...)

// domain/aggregate/expense/Approval.kt
package com.alsatech.finflow.resource.expense.domain.aggregate.expense

data class Approval(...)
```

---

## Summary

**For Expense Resource (2 entities):**
- ✅ **ORGANIZED STRUCTURE** - Current structure is optimal
- Clear separation: entities in `entity/`, ports in `repository/`, services in `service/`
- Professional standard for Clean Architecture / DDD
- Perfect for any number of entities (2-20+)

**Key Benefits:**
- **Separation of Concerns** - Each package has single responsibility
- **Hexagonal Architecture** - Repository interfaces are clearly ports
- **Professional** - Industry standard structure
- **Scalable** - Works well as domain grows

**Bottom line:** The organized structure (entity/ + repository/ + service/) is the **professional standard**. It provides clear separation of concerns and follows Clean Architecture principles perfectly.
