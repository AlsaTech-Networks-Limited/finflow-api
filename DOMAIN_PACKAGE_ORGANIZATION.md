# 🎯 Domain Layer Package Organization Guide

## The Question

**Should we organize domain layer like this:**

### Option A: Flat Structure (Current)
```
shared/domain/
├── Account.kt
├── AccountRepository.kt
├── User.kt
├── UserRepository.kt
├── Company.kt
├── CompanyRepository.kt
└── ...
```

### Option B: Grouped by Type (Proposed)
```
shared/domain/
├── Account.kt
├── User.kt
├── Company.kt
├── repository/
│   ├── AccountRepository.kt
│   ├── UserRepository.kt
│   └── CompanyRepository.kt
└── ...
```

### Option C: Grouped by Feature (Alternative)
```
shared/domain/
├── account/
│   ├── Account.kt
│   └── AccountRepository.kt
├── user/
│   ├── User.kt
│   └── UserRepository.kt
└── company/
    ├── Company.kt
    └── CompanyRepository.kt
```

---

## 📊 Comparison

| Aspect | Option A (Flat) | Option B (Type) | Option C (Feature) |
|--------|----------------|-----------------|-------------------|
| **Files in One Place** | ✅ Yes | ❌ No | ✅ Yes |
| **Clear Separation** | ❌ No | ✅ Yes | ✅ Yes |
| **Easy Navigation** | ⚠️ When small | ⚠️ Need to jump | ✅ Best |
| **Scalability** | ❌ Gets messy | ✅ Scales well | ✅ Scales best |
| **Package-by-Layer** | ✅ Yes | ✅ Yes | ❌ No |
| **Package-by-Feature** | ❌ No | ❌ No | ✅ Yes |

---

## 🎯 Recommendation

### For `shared/domain/` → **Option B (Grouped by Type)** ✅

```
shared/domain/
├── entity/              # OR just entities/
│   ├── User.kt
│   ├── Company.kt
│   └── Account.kt
│
└── repository/
    ├── UserRepository.kt
    ├── CompanyRepository.kt
    └── AccountRepository.kt
```

**Why?**
- ✅ Clear separation of concerns
- ✅ Scales well (10+ entities won't clutter)
- ✅ Easy to see all entities at once
- ✅ Easy to see all repository contracts at once
- ✅ Standard Java/Kotlin convention

### For `resource/expense/domain/` → **Option A (Flat)** ✅

```
resource/expense/domain/
├── Expense.kt
├── ExpenseRepository.kt
├── Approval.kt
└── ApprovalRepository.kt
```

**Why?**
- ✅ Self-contained resource (only 2-4 entities)
- ✅ Entity and repository stay together
- ✅ Easy to navigate
- ✅ Won't get cluttered (small scope)

---

## 📚 Detailed Analysis

### Option A: Flat Structure

**Structure:**
```
shared/domain/
├── User.kt
├── UserRepository.kt
├── Company.kt
├── CompanyRepository.kt
├── Account.kt
├── AccountRepository.kt
├── Category.kt
├── CategoryRepository.kt
├── Invoice.kt
├── InvoiceRepository.kt
├── Transaction.kt
└── TransactionRepository.kt
```

**Pros:**
- ✅ Simple - everything in one place
- ✅ Entity and repository are next to each other
- ✅ Works well for small domains (< 5 entities)

**Cons:**
- ❌ Gets messy with many entities (10+ files)
- ❌ Hard to distinguish entities from repositories at a glance
- ❌ Alphabetical sorting mixes them up

**When to Use:**
- Small self-contained resources (`resource/expense/domain/`)
- Simple domains with few entities

---

### Option B: Grouped by Type (RECOMMENDED for shared)

**Structure:**
```
shared/domain/
├── entity/              # All entities together
│   ├── User.kt
│   ├── Company.kt
│   ├── Account.kt
│   ├── Category.kt
│   ├── Invoice.kt
│   └── Transaction.kt
│
├── repository/          # All repository interfaces together
│   ├── UserRepository.kt
│   ├── CompanyRepository.kt
│   ├── AccountRepository.kt
│   ├── CategoryRepository.kt
│   ├── InvoiceRepository.kt
│   └── TransactionRepository.kt
│
└── valueobject/         # If you have value objects
    ├── Money.kt
    ├── Email.kt
    └── Address.kt
```

**Pros:**
- ✅ Clear separation of concerns
- ✅ Scales well (can have 50+ entities)
- ✅ Easy to see all entities at once
- ✅ Standard enterprise convention
- ✅ Can add more categories (valueobject, enum, etc.)

**Cons:**
- ❌ Need to navigate between folders
- ❌ Entity and repository are in different places

**When to Use:**
- ✅ Shared domain with many entities
- ✅ Enterprise applications
- ✅ When you expect growth

**Real-World Examples:**
```java
// Spring Framework uses this
org.springframework.security/
├── core/
│   ├── Authentication.java
│   ├── GrantedAuthority.java
│   └── userdetails/
│       └── UserDetails.java
└── repository/
    └── UserRepository.java
```

---

### Option C: Grouped by Feature (Alternative)

**Structure:**
```
shared/domain/
├── user/
│   ├── User.kt
│   ├── UserRepository.kt
│   ├── Role.kt (enum)
│   └── Email.kt (value object)
│
├── company/
│   ├── Company.kt
│   └── CompanyRepository.kt
│
└── account/
    ├── Account.kt
    ├── AccountRepository.kt
    └── AccountType.kt (enum)
```

**Pros:**
- ✅ Everything for one feature together
- ✅ Best encapsulation
- ✅ Easy to extract to microservice later
- ✅ Package-by-feature (modern approach)

**Cons:**
- ❌ Can duplicate value objects (Email used by User and Company)
- ❌ Less common for shared domain
- ❌ Better suited for resources, not shared

**When to Use:**
- ❌ Not recommended for shared domain
- ✅ Use `resource/` structure instead

---

## 🏗️ Complete Recommended Structure

### For `shared/domain/` (Many Entities)

```
shared/domain/
├── entity/                          # All entities
│   ├── User.kt
│   ├── Company.kt
│   ├── Account.kt
│   ├── Category.kt
│   ├── Invoice.kt
│   └── Transaction.kt
│
├── repository/                      # All repository interfaces
│   ├── UserRepository.kt
│   ├── CompanyRepository.kt
│   ├── AccountRepository.kt
│   ├── CategoryRepository.kt
│   ├── InvoiceRepository.kt
│   └── TransactionRepository.kt
│
├── valueobject/                     # Value objects (optional)
│   ├── Money.kt
│   ├── Email.kt
│   └── Address.kt
│
└── event/                           # Domain events (optional)
    ├── UserCreatedEvent.kt
    └── CompanyDeletedEvent.kt
```

### For `resource/expense/domain/` (Few Entities)

```
resource/expense/domain/
├── Expense.kt                       # Entity
├── ExpenseRepository.kt             # Repository
├── Approval.kt                      # Entity
├── ApprovalRepository.kt            # Repository
└── ExpenseStatus.kt                 # Enum (can also be embedded in Expense.kt)
```

---

## 📋 Package Naming Conventions

### Option 1: Singular (Recommended)
```
shared/domain/
├── entity/              # Singular
├── repository/          # Singular
└── valueobject/         # Singular
```

**Why?** Standard Java/Kotlin convention

### Option 2: Plural
```
shared/domain/
├── entities/            # Plural
├── repositories/        # Plural
└── valueobjects/        # Plural
```

**Why?** Descriptive - shows it contains multiple

**Recommendation:** Use **Singular** (more common in enterprise Java/Kotlin)

---

## 🎯 Import Examples

### Flat Structure
```kotlin
import com.alsatech.finflow.shared.domain.User
import com.alsatech.finflow.shared.domain.UserRepository
```

### Grouped Structure
```kotlin
import com.alsatech.finflow.shared.domain.entity.User
import com.alsatech.finflow.shared.domain.repository.UserRepository
```

**Trade-off:**
- Flat = Shorter imports
- Grouped = Clearer package structure

---

## 📊 Decision Matrix

### Shared Domain (10+ Entities)

**Use Grouped by Type:**
```
shared/domain/
├── entity/
│   └── ...
└── repository/
    └── ...
```

**Reasons:**
- ✅ Many entities (scales better)
- ✅ Shared across resources (needs organization)
- ✅ Unlikely to change (stable structure)

### Resource Domain (2-5 Entities)

**Use Flat Structure:**
```
resource/expense/domain/
├── Expense.kt
├── ExpenseRepository.kt
├── Approval.kt
└── ApprovalRepository.kt
```

**Reasons:**
- ✅ Few entities (won't get messy)
- ✅ Self-contained (entity + repo together)
- ✅ Easy navigation

---

## 🚀 Migration Path

### Step 1: Organize Shared Domain

```bash
# Create new package structure
mkdir -p shared/domain/entity
mkdir -p shared/domain/repository

# Move entities
mv shared/domain/User.kt shared/domain/entity/
mv shared/domain/Company.kt shared/domain/entity/
mv shared/domain/Account.kt shared/domain/entity/
# ... etc

# Move repositories
mv shared/domain/UserRepository.kt shared/domain/repository/
mv shared/domain/CompanyRepository.kt shared/domain/repository/
mv shared/domain/AccountRepository.kt shared/domain/repository/
# ... etc
```

### Step 2: Update Package Declarations

```kotlin
// Before
package com.alsatech.finflow.shared.domain

// After
package com.alsatech.finflow.shared.domain.entity
```

### Step 3: Update Imports

```kotlin
// Before
import com.alsatech.finflow.shared.domain.User
import com.alsatech.finflow.shared.domain.UserRepository

// After
import com.alsatech.finflow.shared.domain.entity.User
import com.alsatech.finflow.shared.domain.repository.UserRepository
```

### Step 4: Keep Resource Domains Flat

```
resource/expense/domain/     ← Keep flat (few files)
resource/account/domain/     ← Keep flat (few files)
```

---

## 🎓 Industry Standards

### Spring Framework
```
org.springframework.data/
├── domain/
│   ├── Persistable.java
│   ├── Page.java
│   └── Sort.java
└── repository/
    ├── Repository.java
    └── CrudRepository.java
```

### Hibernate
```
org.hibernate/
├── entity/
│   └── EntityManager.java
└── repository/
    └── ...
```

### Domain-Driven Design (DDD)
```
domain/
├── model/               # Entities
├── repository/          # Repositories
├── valueobject/         # Value Objects
├── event/              # Domain Events
└── service/            # Domain Services
```

---

## 📚 Final Recommendation

### Use This Structure:

```
finflow/
│
├── resource/                        # Business Capabilities
│   ├── expense/
│   │   └── domain/                  # ← FLAT (few entities)
│   │       ├── Expense.kt
│   │       ├── ExpenseRepository.kt
│   │       ├── Approval.kt
│   │       └── ApprovalRepository.kt
│   │
│   └── account/
│       └── domain/                  # ← FLAT (few entities)
│           ├── Account.kt
│           └── AccountRepository.kt
│
└── shared/
    └── domain/                      # ← GROUPED (many entities)
        ├── entity/
        │   ├── User.kt
        │   ├── Company.kt
        │   └── ...
        │
        └── repository/
            ├── UserRepository.kt
            ├── CompanyRepository.kt
            └── ...
```

---

## ✅ Summary

| Location | Structure | Reason |
|----------|-----------|--------|
| `shared/domain/` | **Grouped by Type** | Many entities, needs organization |
| `resource/*/domain/` | **Flat** | Few entities, self-contained |

### Shared Domain (Grouped)
```
shared/domain/
├── entity/              ← All entities
└── repository/          ← All repositories
```

### Resource Domain (Flat)
```
resource/expense/domain/
├── Expense.kt           ← Entity
└── ExpenseRepository.kt ← Repository (next to entity)
```

---

## 🎯 Your Specific Case

For your shared domain with **User, Company, Account, Category, Invoice, Transaction**, use:

```
shared/domain/
├── entity/
│   ├── User.kt
│   ├── Company.kt
│   ├── Account.kt
│   ├── Category.kt
│   ├── Invoice.kt
│   └── Transaction.kt
│
└── repository/
    ├── UserRepository.kt
    ├── CompanyRepository.kt
    ├── AccountRepository.kt
    ├── CategoryRepository.kt
    ├── InvoiceRepository.kt
    └── TransactionRepository.kt
```

**Benefits:**
- ✅ Scales to 20+ entities easily
- ✅ Clear separation
- ✅ Standard enterprise pattern
- ✅ Easy to find what you need

---

**Last Updated:** 2026-08-05
**Topic:** Domain Layer Package Organization
