# Entity-Identifiable IDs - Production Grade

## Overview

Following industry best practices from **Stripe**, **GitHub**, **Slack**, and other big tech companies, our IDs are:
- **Entity-identifiable** via prefixes
- **Time-ordered** using Snowflake algorithm
- **Human-readable** and debuggable
- **Globally unique** across all servers

## ID Format

```
{prefix}_{snowflake_id}
   │         │
   │         └─ 64-bit Snowflake ID (time-ordered, unique)
   └─────────── 3-letter entity type prefix
```

### Examples

```
exp_1704067200001  ← Expense
app_1704067300001  ← Approval
acc_1704068400001  ← Account
txn_1704069500001  ← Transaction
inv_1704070600001  ← Invoice
usr_1704071700001  ← User
cmp_1704072800001  ← Company
```

## All Entity Prefixes

| Prefix | Entity Type | Description | Example ID |
|--------|-------------|-------------|------------|
| `exp` | EXPENSE | Expense record | `exp_1704067200001` |
| `app` | APPROVAL | Expense approval/rejection | `app_1704067300001` |
| `acc` | ACCOUNT | Financial account | `acc_1704068400001` |
| `txn` | TRANSACTION | Financial transaction | `txn_1704069500001` |
| `inv` | INVOICE | Invoice | `inv_1704070600001` |
| `cat` | CATEGORY | Expense category | `cat_1704071800001` |
| `usr` | USER | User account | `usr_1704072900001` |
| `cmp` | COMPANY | Company/Organization | `cmp_1704074000001` |
| `bud` | BUDGET | Budget | `bud_1704075100001` |
| `rpt` | REPORT | Report | `rpt_1704076200001` |
| `att` | ATTACHMENT | File attachment | `att_1704077300001` |

## Benefits Over Generic IDs

### ❌ Before (Generic Long IDs)

```json
{
  "id": 1704067200001,          // ❌ What entity is this?
  "userId": 1704067200002,      // ❌ User or something else?
  "approvals": [
    {
      "id": 1704067300001       // ❌ Approval? Expense? Confused!
    }
  ]
}
```

**Problems:**
- Can't tell entity type from ID
- Easy to mix up IDs in logs
- Debugging is hard
- Support tickets require extra lookups

### ✅ After (Prefixed IDs)

```json
{
  "expenseId": "exp_1704067200001",    // ✅ Clearly an expense
  "userId": "usr_1704067200002",       // ✅ Clearly a user
  "approvals": [
    {
      "approvalId": "app_1704067300001",  // ✅ Clearly an approval
      "expenseId": "exp_1704067200001"    // ✅ Clear relationship
    }
  ]
}
```

**Benefits:**
- ✅ Instantly recognize entity type
- ✅ Easy debugging in logs
- ✅ Self-documenting
- ✅ Prevents ID confusion
- ✅ Better support experience

## Code Examples

### Generating IDs

```kotlin
@Service
class ExpenseWriteServiceImpl(
    private val idGenerator: IdGenerator
) {
    fun submitExpense(command: SubmitExpenseCommand): Mono<String> {
        // Generate expense-specific ID
        val expenseId = idGenerator.generateId(EntityType.EXPENSE)
        // Result: "exp_1704067200001"

        val expense = Expense(
            expenseId = expenseId,
            userId = command.userId,
            amount = command.amount,
            ...
        )

        return expenseRepository.save(expense)
            .map { it.expenseId }
    }

    fun approveExpense(command: ApproveExpenseCommand): Mono<Unit> {
        // Generate approval-specific ID
        val approvalId = idGenerator.generateId(EntityType.APPROVAL)
        // Result: "app_1704067300001"

        val approval = Approval(
            approvalId = approvalId,
            expenseId = command.expenseId,  // "exp_1704067200001"
            approverId = command.approverId,
            ...
        )

        return approvalRepository.save(approval)
            .then(Mono.just(Unit))
    }
}
```

### Batch Generation

```kotlin
// Generate 100 expense IDs at once
val expenseIds = idGenerator.generateBatch(EntityType.EXPENSE, 100)
// Result: ["exp_1704067200001", "exp_1704067200002", ..., "exp_1704067200100"]
```

### Extracting Information

```kotlin
val expenseId = "exp_1704067200001"

// Extract timestamp (when was this created?)
val timestamp = idGenerator.extractTimestamp(expenseId)
// Result: 2024-01-01T00:00:00.000Z

// Extract worker ID (which server created this?)
val workerId = idGenerator.extractWorkerId(expenseId)
// Result: 0

// Extract entity type (what kind of entity is this?)
val entityType = idGenerator.extractEntityType(expenseId)
// Result: EntityType.EXPENSE

// Validate ID format
val isValid = idGenerator.isValidId("exp_1704067200001")
// Result: true

val isInvalid = idGenerator.isValidId("invalid_id")
// Result: false
```

## API Response Examples

### POST /api/v1/expenses (Submit Expense)

**Response:**
```json
{
  "meta": {
    "requestId": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2025-01-15T12:00:00Z",
    "status": "SUCCESS"
  },
  "payload": "exp_1704067200001"
}
```

### GET /api/v1/expenses (List Expenses)

**Response:**
```json
{
  "meta": {
    "requestId": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2025-01-15T12:00:00Z",
    "status": "SUCCESS"
  },
  "payload": [
    {
      "expenseId": "exp_1704067200001",
      "userId": "usr_1704067100001",
      "categoryId": "cat_1704066900001",
      "amount": 150.50,
      "status": "PENDING",
      "createdAt": "2025-01-13T10:30:00Z",
      "updatedAt": "2025-01-13T10:30:00Z"
    },
    {
      "expenseId": "exp_1704067200002",
      "userId": "usr_1704067100001",
      "categoryId": "cat_1704066900002",
      "amount": 850.00,
      "status": "APPROVED",
      "createdAt": "2025-01-10T09:00:00Z",
      "updatedAt": "2025-01-11T14:30:00Z"
    }
  ]
}
```

### GET /api/v1/expenses/{expenseId} (Get Expense Detail)

**Response:**
```json
{
  "meta": {
    "requestId": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2025-01-15T12:05:00Z",
    "status": "SUCCESS"
  },
  "payload": {
    "expenseId": "exp_1704067200002",
    "userId": "usr_1704067100001",
    "categoryId": "cat_1704066900002",
    "amount": 850.00,
    "status": "APPROVED",
    "receiptUrl": "https://example.com/receipts/002.pdf",
    "notes": "Flight to San Francisco for conference",
    "approvals": [
      {
        "approvalId": "app_1704067300001",
        "expenseId": "exp_1704067200002",
        "approverId": "usr_1704067100002",
        "decision": "APPROVED",
        "comment": "Valid business expense",
        "decidedAt": "2025-01-11T14:30:00Z",
        "createdAt": "2025-01-11T14:30:00Z",
        "updatedAt": "2025-01-11T14:30:00Z"
      }
    ],
    "createdAt": "2025-01-10T09:00:00Z",
    "updatedAt": "2025-01-11T14:30:00Z"
  }
}
```

## Database Schema

### Table Design

IDs are stored as **VARCHAR(30)** in the database:

```sql
CREATE TABLE expenses (
    record_id VARCHAR(30) PRIMARY KEY,        -- "exp_1704067200001"
    user_id VARCHAR(30) NOT NULL,             -- "usr_1704067100001"
    category_id VARCHAR(30) NOT NULL,         -- "cat_1704066900001"
    amount NUMERIC(14, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(record_id),
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(record_id)
);

CREATE TABLE approvals (
    record_id VARCHAR(30) PRIMARY KEY,        -- "app_1704067300001"
    expense_id VARCHAR(30) NOT NULL,          -- "exp_1704067200001"
    approver_id VARCHAR(30) NOT NULL,         -- "usr_1704067100002"
    decision VARCHAR(50) NOT NULL,
    comment TEXT,
    decided_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_expense FOREIGN KEY (expense_id) REFERENCES expenses(record_id),
    CONSTRAINT fk_approver FOREIGN KEY (approver_id) REFERENCES users(record_id)
);
```

### Indexing

Prefixed IDs are still **highly efficient** for indexing:

```sql
-- Primary key index (automatic)
CREATE INDEX idx_expenses_pk ON expenses(record_id);

-- Foreign key indexes
CREATE INDEX idx_expenses_user_id ON expenses(user_id);
CREATE INDEX idx_expenses_category_id ON expenses(category_id);

-- Approval indexes
CREATE INDEX idx_approvals_expense_id ON approvals(expense_id);
CREATE INDEX idx_approvals_approver_id ON approvals(approver_id);
```

**Performance Notes:**
- VARCHAR(30) is efficient for indexing
- IDs are still time-ordered (sortable by creation time)
- Foreign key lookups are fast with indexes
- String comparison is negligible overhead vs BIGINT

## Logging & Debugging

### Log Examples

**Before (Generic IDs):**
```
ERROR: Failed to approve expense 1704067200001 by user 1704067100001
```
❌ What are these numbers? Expense? User? Not clear!

**After (Prefixed IDs):**
```
ERROR: Failed to approve expense exp_1704067200001 by user usr_1704067100001
```
✅ Instantly clear: an expense and a user!

### Support Scenarios

**Customer reports issue:**
> "I submitted expense `exp_1704067200001` but it wasn't approved"

**Support team can instantly:**
- Identify it's an expense (not approval, invoice, etc.)
- Search logs for `exp_1704067200001`
- Check approvals table for matching `expense_id`
- See timeline from timestamp extraction

## Real-World Comparisons

### Stripe IDs

```
cus_Nj3aT4z9K5qRBl  ← Customer
ch_3NzI9vL7zBW2eC   ← Charge
pi_3NzI9vL7zBW2eD   ← Payment Intent
inv_1NzI9vL7zBW2eE  ← Invoice
sub_1NzI9vL7zBW2eF  ← Subscription
```

### GitHub IDs

```
I_kwDOABC123          ← Issue
PR_kwDOABC456         ← Pull Request
MDEwOlJlcG9zaXRvcnk=  ← Repository (base64)
```

### Our IDs (Cleaner!)

```
exp_1704067200001  ← Expense (shorter, clearer)
app_1704067300001  ← Approval
acc_1704068400001  ← Account
```

**Why ours are better:**
- ✅ Fixed-length Snowflake part (sortable)
- ✅ Shorter prefixes (3 chars vs Stripe's varied length)
- ✅ Human-readable numeric part
- ✅ Easy to type and communicate

## Migration Strategy

### Phase 1: Add New ID Generation (Current)

- ✅ `IdGenerator` generates prefixed IDs
- ✅ `EntityType` enum defines all prefixes
- ✅ Type-safe ID generation

### Phase 2: Update Entities (Next)

```kotlin
// Before
@Table("expenses")
data class Expense(
    @Id
    @Column("record_id")
    val expenseId: Long,  // ❌ Long
    ...
)

// After
@Table("expenses")
data class Expense(
    @Id
    @Column("record_id")
    val expenseId: String,  // ✅ String
    ...
)
```

### Phase 3: Update Database Schema

```sql
-- Backup data
CREATE TABLE expenses_backup AS SELECT * FROM expenses;

-- Alter column type
ALTER TABLE expenses ALTER COLUMN record_id TYPE VARCHAR(30);

-- Migrate existing IDs (add prefixes)
UPDATE expenses SET record_id = 'exp_' || record_id;

-- Do same for foreign keys
ALTER TABLE approvals ALTER COLUMN expense_id TYPE VARCHAR(30);
UPDATE approvals SET expense_id = 'exp_' || expense_id;
```

### Phase 4: Update All Services

Update all services to use `generateId(EntityType)`.

## Validation

### Valid IDs

```kotlin
idGenerator.isValidId("exp_1704067200001")  // ✅ true
idGenerator.isValidId("app_1704067300001")  // ✅ true
idGenerator.isValidId("usr_1704072900001")  // ✅ true
```

### Invalid IDs

```kotlin
idGenerator.isValidId("invalid")            // ❌ false (no prefix)
idGenerator.isValidId("xyz_1234")           // ❌ false (unknown prefix)
idGenerator.isValidId("exp_")               // ❌ false (no Snowflake ID)
idGenerator.isValidId("exp_abc")            // ❌ false (not numeric)
```

## Summary

### ID Examples by Entity

```
Expenses:      exp_1704067200001, exp_1704067200002, exp_1704067200003
Approvals:     app_1704067300001, app_1704067300002, app_1704067300003
Accounts:      acc_1704068400001, acc_1704068400002, acc_1704068400003
Transactions:  txn_1704069500001, txn_1704069500002, txn_1704069500003
Invoices:      inv_1704070600001, inv_1704070600002, inv_1704070600003
Categories:    cat_1704071800001, cat_1704071800002, cat_1704071800003
Users:         usr_1704072900001, usr_1704072900002, usr_1704072900003
Companies:     cmp_1704074000001, cmp_1704074000002, cmp_1704074000003
```

### Key Benefits

✅ **Instantly recognizable** - Know entity type at a glance
✅ **Production-grade** - Used by Stripe, GitHub, Slack
✅ **Debuggable** - Easy to trace in logs
✅ **Human-readable** - Can be spoken over phone/chat
✅ **Type-safe** - EntityType enum prevents errors
✅ **Time-ordered** - Still sortable by creation time
✅ **Globally unique** - Snowflake algorithm guarantees
✅ **Self-documenting** - IDs explain themselves

This is the **professional standard** for modern APIs! 🚀
