# 🎯 Clean Architecture - Resource vs Shared Grouping Guide

## The Problem You Identified

**Current Structure:**
```
shared/domain/
├── Account.kt
├── AccountRepository.kt
├── User.kt
├── Company.kt
├── Category.kt
...
```

**The Question:** Why is `Account` in `shared/` when it has its own business workflows?

**Answer:** You're RIGHT to question this! Let me explain the proper distinction.

---

## 🔑 The Decision Rule

### When to Put in `resource/`

An entity belongs in `resource/{name}/` if it meets **ANY** of these criteria:

✅ **Has its own business workflows/use cases**
- Example: Create Account, Update Account, Close Account

✅ **Can be managed independently**
- Example: Accounts can be created/updated/deleted without affecting other resources

✅ **Represents a core business capability**
- Example: Account Management is a standalone feature

✅ **Has complex business logic**
- Example: Account balance calculations, transaction posting

✅ **Will grow over time**
- Example: Accounts might need reconciliation, statements, etc.

### When to Put in `shared/`

An entity belongs in `shared/domain/` if it meets **ALL** of these criteria:

✅ **Pure infrastructure/cross-cutting concern**
- Example: User (authentication/authorization)
- Example: Company (multi-tenancy context)

✅ **No standalone business workflows**
- Example: User doesn't have "business logic" - it's just identity

✅ **Referenced by many resources**
- Example: Every resource needs to know the Company context

✅ **Stable and unlikely to change**
- Example: User structure is fixed (id, email, role)

---

## 📊 Proper Classification

### ❌ WRONG (Current Structure)

```
shared/domain/
├── Account.kt          ❌ Has business workflows!
├── Category.kt         ❌ Has CRUD operations!
├── Invoice.kt          ❌ Complete business capability!
├── Transaction.kt      ❌ Complex business logic!
├── User.kt             ✅ OK - pure infrastructure
└── Company.kt          ✅ OK - multi-tenancy context
```

### ✅ CORRECT (Recommended Structure)

```
resource/
├── account/            ✅ Account is a business capability
│   ├── domain/
│   │   ├── Account.kt
│   │   └── AccountRepository.kt
│   ├── application/
│   │   └── usecase/
│   │       ├── CreateAccountUseCase.kt
│   │       ├── UpdateBalanceUseCase.kt
│   │       └── CloseAccountUseCase.kt
│   ├── infrastructure/
│   └── api/
│
├── category/           ✅ Category management
│   ├── domain/
│   │   ├── Category.kt
│   │   └── CategoryRepository.kt
│   ├── application/
│   └── api/
│
├── invoice/            ✅ Invoice management
│   ├── domain/
│   ├── application/
│   └── api/
│
├── transaction/        ✅ Transaction management
│   ├── domain/
│   ├── application/
│   └── api/
│
└── expense/            ✅ Already done!
    └── ...

shared/
├── domain/             ✅ ONLY infrastructure
│   ├── User.kt         ✅ Authentication entity
│   ├── Company.kt      ✅ Multi-tenancy context
│   └── AuditLog.kt     ✅ Cross-cutting concern
│
└── infrastructure/
    ├── security/       ✅ JWT, Auth
    ├── config/         ✅ Spring config
    └── audit/          ✅ Logging
```

---

## 🎓 Decision Matrix

| Entity | Has Use Cases? | Business Logic? | Changes Often? | Decision | Location |
|--------|---------------|-----------------|----------------|----------|----------|
| **Account** | ✅ Yes (Create, Update, Close) | ✅ Yes (Balance, Transactions) | ✅ Yes | **Resource** | `resource/account/` |
| **Category** | ✅ Yes (Create, Update, Delete) | ⚠️ Some (Validation) | ⚠️ Maybe | **Resource** | `resource/category/` |
| **Invoice** | ✅ Yes (Create, Send, Pay) | ✅ Yes (Calculation, Status) | ✅ Yes | **Resource** | `resource/invoice/` |
| **Transaction** | ✅ Yes (Post, Reconcile) | ✅ Yes (Validation, Balance) | ✅ Yes | **Resource** | `resource/transaction/` |
| **Expense** | ✅ Yes (Submit, Approve) | ✅ Yes (Approval workflow) | ✅ Yes | **Resource** | `resource/expense/` ✅ |
| **User** | ❌ No (Just identity) | ❌ No (Framework handles) | ❌ No | **Shared** | `shared/domain/` ✅ |
| **Company** | ❌ No (Just context) | ❌ No (Multi-tenancy) | ❌ No | **Shared** | `shared/domain/` ✅ |

---

## 📋 Examples

### Example 1: Account

**Question:** Should Account be in `shared/` or `resource/`?

**Analysis:**
- ✅ Has use cases: CreateAccount, UpdateBalance, CloseAccount
- ✅ Has business logic: Balance calculations, account types
- ✅ Will grow: Account statements, reconciliation, etc.

**Decision:** `resource/account/` ✅

**Structure:**
```kotlin
// resource/account/domain/Account.kt
data class Account(
    val id: Long? = null,
    val companyId: Long,      // References shared entity
    val name: String,
    val type: AccountType,
    val balance: BigDecimal
)

// resource/account/application/usecase/CreateAccountUseCase.kt
class CreateAccountUseCase(
    private val accountRepository: AccountRepository,
    private val companyRepository: CompanyRepository  // Uses shared repo
) {
    fun execute(command: CreateAccountCommand): Mono<Long> {
        // Validate company exists (cross-resource check)
        return companyRepository.findById(command.companyId)
            .switchIfEmpty(Mono.error(CompanyNotFoundException()))
            .flatMap { company ->
                // Create account
                val account = Account(...)
                accountRepository.save(account)
            }
    }
}
```

---

### Example 2: Category

**Question:** Should Category be in `shared/` or `resource/`?

**Analysis:**
- ✅ Has use cases: CreateCategory, UpdateCategory, DeleteCategory
- ⚠️ Simple business logic: Just CRUD
- ⚠️ Referenced by Expense resource

**Decision:** `resource/category/` ✅

**Why?**
- Even though it's referenced by Expense, it has its own management workflows
- Categories can be created/managed independently
- Future: Might add category budgets, limits, etc.

**Cross-Resource Reference:**
```kotlin
// resource/expense/domain/Expense.kt
data class Expense(
    val categoryId: Long,  // References category by ID (loose coupling)
    ...
)

// resource/expense/application/usecase/SubmitExpenseUseCase.kt
class SubmitExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository  // Uses category repo
) {
    fun execute(command: SubmitExpenseCommand): Mono<Long> {
        // Validate category exists
        return categoryRepository.findById(command.categoryId)
            .switchIfEmpty(Mono.error(CategoryNotFoundException()))
            .flatMap { category ->
                // Create expense
                val expense = Expense(...)
                expenseRepository.save(expense)
            }
    }
}
```

---

### Example 3: User (Correctly in Shared)

**Question:** Why is User in `shared/`?

**Analysis:**
- ❌ No use cases: Authentication is handled by framework
- ❌ No business logic: Just identity and roles
- ✅ Referenced everywhere: Every resource needs user context
- ❌ Stable: User structure rarely changes

**Decision:** `shared/domain/` ✅

**Why?**
- User is an **infrastructure concern** (authentication/authorization)
- Not a business capability
- Managed by security framework, not business logic

**Usage:**
```kotlin
// Expense uses User ID for reference
data class Expense(
    val userId: Long,  // References User by ID
    ...
)

// But no business logic around User
// Authentication handled by Spring Security
```

---

### Example 4: Company (Correctly in Shared)

**Question:** Why is Company in `shared/`?

**Analysis:**
- ❌ No use cases: Company is created during onboarding (separate concern)
- ❌ No business logic in main app: Just context
- ✅ Referenced everywhere: Multi-tenancy context
- ❌ Stable: Company structure is fixed

**Decision:** `shared/domain/` ✅

**Why?**
- Company represents **multi-tenancy context**
- Every resource operates within a company
- Not a business capability in the main app
- Company management would be a separate admin app

---

## 🏗️ Corrected Structure

### Full Recommended Structure

```
finflow/
│
├── resource/                           # Business Capabilities
│   ├── account/                        # ✅ Account Management
│   │   ├── domain/
│   │   │   ├── Account.kt
│   │   │   ├── AccountType.kt (enum)
│   │   │   └── AccountRepository.kt
│   │   ├── application/
│   │   │   ├── usecase/
│   │   │   │   ├── CreateAccountUseCase.kt
│   │   │   │   ├── UpdateBalanceUseCase.kt
│   │   │   │   └── CloseAccountUseCase.kt
│   │   │   └── dto/
│   │   │       └── AccountDto.kt
│   │   ├── infrastructure/
│   │   │   └── persistence/
│   │   │       └── AccountRepositoryAdapter.kt
│   │   └── api/
│   │       └── AccountController.kt
│   │
│   ├── category/                       # ✅ Category Management
│   │   ├── domain/
│   │   │   ├── Category.kt
│   │   │   └── CategoryRepository.kt
│   │   ├── application/
│   │   │   ├── usecase/
│   │   │   │   ├── CreateCategoryUseCase.kt
│   │   │   │   ├── UpdateCategoryUseCase.kt
│   │   │   │   └── DeleteCategoryUseCase.kt
│   │   │   └── dto/
│   │   └── api/
│   │
│   ├── invoice/                        # ✅ Invoice Management
│   │   ├── domain/
│   │   │   ├── Invoice.kt
│   │   │   ├── InvoiceStatus.kt (enum)
│   │   │   └── InvoiceRepository.kt
│   │   ├── application/
│   │   │   └── usecase/
│   │   │       ├── CreateInvoiceUseCase.kt
│   │   │       ├── SendInvoiceUseCase.kt
│   │   │       └── MarkPaidUseCase.kt
│   │   └── api/
│   │
│   ├── transaction/                    # ✅ Transaction Management
│   │   ├── domain/
│   │   │   ├── Transaction.kt
│   │   │   ├── TransactionType.kt (enum)
│   │   │   └── TransactionRepository.kt
│   │   ├── application/
│   │   │   └── usecase/
│   │   │       ├── PostTransactionUseCase.kt
│   │   │       └── ReconcileTransactionUseCase.kt
│   │   └── api/
│   │
│   └── expense/                        # ✅ Already done!
│       └── ...
│
└── shared/                             # Infrastructure Only
    ├── domain/                         # Cross-cutting entities
    │   ├── User.kt                     # ✅ Authentication
    │   ├── UserRepository.kt
    │   ├── Company.kt                  # ✅ Multi-tenancy
    │   ├── CompanyRepository.kt
    │   ├── AuditLog.kt                 # ✅ Cross-cutting
    │   └── AuditLogRepository.kt
    │
    └── infrastructure/
        ├── persistence/
        │   ├── UserRepositoryAdapter.kt
        │   ├── CompanyRepositoryAdapter.kt
        │   └── AuditLogRepositoryAdapter.kt
        ├── security/                   # JWT, Auth, etc.
        ├── config/                     # Spring config
        └── external/                   # LLM, Email, Storage
```

---

## 🎯 Key Principles

### 1. **Resource = Business Capability**

If it has use cases and business logic → **Resource**

```
resource/account/
resource/invoice/
resource/expense/
```

### 2. **Shared = Infrastructure Concern**

If it's authentication, multi-tenancy, or cross-cutting → **Shared**

```
shared/domain/User.kt      (Authentication)
shared/domain/Company.kt   (Multi-tenancy)
```

### 3. **Cross-Resource References**

Resources reference each other by **ID only** (loose coupling):

```kotlin
// Expense references Category by ID
data class Expense(
    val categoryId: Long,  // Not Category object!
    ...
)

// Use case can access CategoryRepository
class SubmitExpenseUseCase(
    private val categoryRepository: CategoryRepository  // Cross-resource
)
```

### 4. **Dependency Direction**

Resources can depend on shared, but **never the other way around**:

```
✅ Expense → User (by userId)
✅ Account → Company (by companyId)
❌ User → Expense (NEVER!)
```

---

## 📝 Summary

### What Goes in `resource/`?

| Criteria | Example |
|----------|---------|
| Has use cases | CreateAccount, SendInvoice |
| Has business logic | Balance calculations, Invoice status |
| Can be managed independently | CRUD operations |
| Represents business capability | Account Management, Invoice Management |

### What Goes in `shared/`?

| Criteria | Example |
|----------|---------|
| Infrastructure concern | User (auth), Company (multi-tenancy) |
| No business workflows | User has no "business logic" |
| Referenced everywhere | Every resource needs Company context |
| Stable structure | User/Company rarely change |

---

## 🚀 Migration Path

To fix the current structure:

1. **Move Account to resource/**
   ```bash
   mv shared/domain/Account.kt → resource/account/domain/
   mv shared/domain/AccountRepository.kt → resource/account/domain/
   ```

2. **Move Category to resource/**
3. **Move Invoice to resource/**
4. **Move Transaction to resource/**

5. **Keep in shared/**
   - User.kt
   - Company.kt
   - AuditLog.kt (if you add it)

---

**Rule of Thumb:**
> If you can say "I need to manage [X]" → It's a **Resource**
> If you can say "[X] is just context" → It's **Shared**

---

**Last Updated:** 2026-08-05
**Author:** Clean Architecture Guide
