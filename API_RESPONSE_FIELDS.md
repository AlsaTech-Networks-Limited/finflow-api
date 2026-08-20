# API Response Fields Documentation

## Overview

This document describes all fields returned in API responses for the Expense resource.

## Response DTOs

### 1. ExpenseDto (List Response)

**Usage**: Returned by `GET /api/v1/expenses` (list endpoint)

**JSON Response:**
```json
{
  "expenseId": "exp_1704067200001",
  "userId": 1,
  "categoryId": 2,
  "amount": 150.50,
  "status": "PENDING",
  "receiptUrl": "https://example.com/receipts/001.pdf",
  "notes": "Client lunch meeting",
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-15T10:30:00Z"
}
```

**Field Descriptions:**

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `expenseId` | String | Unique expense identifier (prefixed Snowflake ID) | `"exp_1704067200001"` |
| `userId` | Long | ID of user who submitted the expense | `1` |
| `categoryId` | Long | ID of expense category | `2` |
| `amount` | Decimal | Expense amount (2 decimal places) | `150.50` |
| `status` | Enum | Expense status: `PENDING`, `APPROVED`, `REJECTED` | `"PENDING"` |
| `receiptUrl` | String? | URL to receipt image/PDF (nullable) | `"https://..."` |
| `notes` | String? | Additional notes about expense (nullable) | `"Client lunch..."` |
| `createdAt` | Timestamp | When expense was created (ISO 8601 UTC) | `"2025-01-15T10:30:00Z"` |
| `updatedAt` | Timestamp | When expense was last updated (ISO 8601 UTC) | `"2025-01-15T10:35:00Z"` |

---

### 2. ExpenseDetailDto (Single Expense Response)

**Usage**: Returned by `GET /api/v1/expenses/{expenseId}` (single expense endpoint)

**JSON Response:**
```json
{
  "expenseId": "exp_1704067200002",
  "userId": 1,
  "categoryId": 4,
  "amount": 850.00,
  "status": "APPROVED",
  "receiptUrl": "https://example.com/receipts/002.pdf",
  "notes": "Flight to San Francisco",
  "approvals": [
    {
      "approvalId": "app_1704067300001",
      "expenseId": "exp_1704067200002",
      "approverId": 2,
      "decision": "APPROVED",
      "comment": "Valid business expense",
      "decidedAt": "2025-01-15T11:00:00Z",
      "createdAt": "2025-01-15T11:00:00Z",
      "updatedAt": "2025-01-15T11:00:00Z"
    }
  ],
  "createdAt": "2025-01-14T10:00:00Z",
  "updatedAt": "2025-01-15T11:00:00Z"
}
```

**Field Descriptions:**

Same as `ExpenseDto`, plus:

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `approvals` | Array | List of approval/rejection records | See ApprovalDto below |

---

### 3. ApprovalDto (Nested in ExpenseDetailDto)

**Usage**: Nested inside `ExpenseDetailDto` to show approval history

**JSON Response:**
```json
{
  "approvalId": "app_1704067300001",
  "expenseId": "exp_1704067200002",
  "approverId": 2,
  "decision": "APPROVED",
  "comment": "Valid business expense for conference",
  "decidedAt": "2025-01-15T11:00:00Z",
  "createdAt": "2025-01-15T11:00:00Z",
  "updatedAt": "2025-01-15T11:00:00Z"
}
```

**Field Descriptions:**

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `approvalId` | String | Unique approval identifier (prefixed Snowflake ID) | `"app_1704067300001"` |
| `expenseId` | String | ID of the expense being approved/rejected | `"exp_1704067200002"` |
| `approverId` | Long | ID of user who approved/rejected | `2` |
| `decision` | Enum | Approval decision: `APPROVED` or `REJECTED` | `"APPROVED"` |
| `comment` | String? | Optional comment from approver | `"Valid business..."` |
| `decidedAt` | Timestamp | When approval decision was made (ISO 8601 UTC) | `"2025-01-15T11:00:00Z"` |
| `createdAt` | Timestamp | When approval record was created (ISO 8601 UTC) | `"2025-01-15T11:00:00Z"` |
| `updatedAt` | Timestamp | When approval record was last updated (ISO 8601 UTC) | `"2025-01-15T11:00:00Z"` |

---

## Complete API Response Examples

### GET /api/v1/expenses (List All Expenses)

**Request:**
```http
GET /api/v1/expenses?userId=1
X-RequestId: 550e8400-e29b-41d4-a716-446655440000
```

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
      "userId": 1,
      "categoryId": 6,
      "amount": 150.50,
      "status": "PENDING",
      "receiptUrl": "https://example.com/receipts/001.pdf",
      "notes": "Client lunch meeting with ABC Corp",
      "createdAt": "2025-01-13T10:30:00Z",
      "updatedAt": "2025-01-13T10:30:00Z"
    },
    {
      "expenseId": "exp_1704067200002",
      "userId": 1,
      "categoryId": 4,
      "amount": 850.00,
      "status": "APPROVED",
      "receiptUrl": "https://example.com/receipts/002.pdf",
      "notes": "Flight to San Francisco for conference",
      "createdAt": "2025-01-10T09:00:00Z",
      "updatedAt": "2025-01-11T14:30:00Z"
    },
    {
      "expenseId": "exp_1704067200003",
      "userId": 1,
      "categoryId": 3,
      "amount": 45.75,
      "status": "REJECTED",
      "receiptUrl": null,
      "notes": "Office supplies - missing receipt",
      "createdAt": "2025-01-08T15:20:00Z",
      "updatedAt": "2025-01-09T10:15:00Z"
    }
  ]
}
```

---

### GET /api/v1/expenses/{expenseId} (Get Single Expense)

**Request:**
```http
GET /api/v1/expenses/exp_1704067200002
X-RequestId: 550e8400-e29b-41d4-a716-446655440001
```

**Response:**
```json
{
  "meta": {
    "requestId": "550e8400-e29b-41d4-a716-446655440001",
    "timestamp": "2025-01-15T12:05:00Z",
    "status": "SUCCESS"
  },
  "payload": {
    "expenseId": "exp_1704067200002",
    "userId": 1,
    "categoryId": 4,
    "amount": 850.00,
    "status": "APPROVED",
    "receiptUrl": "https://example.com/receipts/002.pdf",
    "notes": "Flight to San Francisco for conference",
    "approvals": [
      {
        "approvalId": "app_1704067300001",
        "expenseId": "exp_1704067200002",
        "approverId": 2,
        "decision": "APPROVED",
        "comment": "Valid business expense for conference attendance",
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

---

### POST /api/v1/expenses (Submit New Expense)

**Request:**
```http
POST /api/v1/expenses
Content-Type: application/json
X-RequestId: 550e8400-e29b-41d4-a716-446655440002

{
  "meta": {
    "source": "postman",
    "requestId": "550e8400-e29b-41d4-a716-446655440002"
  },
  "payload": {
    "userId": 1,
    "categoryId": 2,
    "amount": 89.99,
    "notes": "Team dinner",
    "receiptUrl": "https://example.com/receipts/003.pdf"
  }
}
```

**Response:**
```json
{
  "meta": {
    "requestId": "550e8400-e29b-41d4-a716-446655440002",
    "timestamp": "2025-01-15T12:10:00Z",
    "status": "SUCCESS"
  },
  "payload": "exp_1704067200005"
}
```

**Note**: The response payload is the newly generated expense ID (prefixed Snowflake ID with 'exp_' prefix).

---

### PUT /api/v1/expenses/{expenseId}/approve (Approve Expense)

**Request:**
```http
PUT /api/v1/expenses/exp_1704067200001/approve
Content-Type: application/json
X-RequestId: 550e8400-e29b-41d4-a716-446655440003

{
  "meta": {
    "source": "postman",
    "requestId": "550e8400-e29b-41d4-a716-446655440003"
  },
  "payload": {
    "expenseId": "exp_1704067200001",
    "approverId": 2,
    "comment": "Approved for client meeting expenses"
  }
}
```

**Response:**
```json
{
  "meta": {
    "requestId": "550e8400-e29b-41d4-a716-446655440003",
    "timestamp": "2025-01-15T12:15:00Z",
    "status": "SUCCESS"
  },
  "payload": null
}
```

**Note**: The response payload is `null` for approval/rejection operations (successful completion is indicated by HTTP 200 status).

---

## Field Types Reference

### Timestamps

All timestamp fields use **ISO 8601 UTC format**:

```
2025-01-15T12:00:00Z
│    │  │  │  │  │  └─ UTC timezone indicator
│    │  │  │  │  └──── Seconds
│    │  │  │  └─────── Minutes
│    │  │  └────────── Hours (24-hour format)
│    │  └───────────── Day
│    └──────────────── Month
└─────────────────── Year
```

### Entity-Identifiable IDs

All ID fields use **entity-identifiable prefixed format**:

```
exp_1704067200001
│   │
│   └─ 64-bit Snowflake ID (time-ordered, unique)
└───── 3-letter entity prefix

Snowflake ID structure:
1704067200001
│           │
│           └─ Sequence + Worker ID (14 bits)
└───────────── Timestamp since epoch (50 bits)
```

**Prefixes by entity type:**
- `exp_` - Expenses
- `app_` - Approvals
- `acc_` - Accounts
- `txn_` - Transactions
- `usr_` - Users
- `cmp_` - Companies

**Benefits:**
- **Instantly recognizable**: Know entity type at a glance
- **Time-ordered**: Larger Snowflake IDs are newer
- **Globally unique**: No duplicates across all servers
- **Production-grade**: Follows patterns from Stripe, GitHub, Slack

### Decimal Amounts

All monetary amounts use **Decimal with 2 decimal places**:

```
850.00
│││││
││││└─ Cents (always 2 digits)
│││└── Decimal point
└└└─── Dollars (up to 12 digits)
```

- **Min**: `0.01` (1 cent)
- **Max**: `999,999,999,999.99`

### Enums

**ExpenseStatus:**
- `PENDING` - Awaiting approval
- `APPROVED` - Approved by manager
- `REJECTED` - Rejected by manager

**Decision:**
- `APPROVED` - Expense approved
- `REJECTED` - Expense rejected

---

## Nullable Fields

The following fields can be `null` in responses:

| Field | When Null | Example |
|-------|-----------|---------|
| `receiptUrl` | No receipt uploaded | `null` |
| `notes` | No notes provided | `null` |
| `comment` | Approver didn't add comment | `null` |

---

## Field Mapping: Domain vs API

### Internal (Domain Entity) → External (API Response)

| Domain Entity Field | Database Column | API Response Field | Notes |
|--------------------|-----------------|--------------------|-------|
| `expenseId` | `record_id` | `expenseId` | Consistent naming across layers |
| `approvalId` | `record_id` | `approvalId` | Consistent naming across layers |
| `userId` | `user_id` | `userId` | Snake case → Camel case |
| `categoryId` | `category_id` | `categoryId` | Snake case → Camel case |
| `receiptUrl` | `receipt_url` | `receiptUrl` | Snake case → Camel case |
| `createdAt` | `created_at` | `createdAt` | Snake case → Camel case |
| `updatedAt` | `updated_at` | `updatedAt` | Snake case → Camel case |
| `decidedAt` | `decided_at` | `decidedAt` | Snake case → Camel case |

**Why consistent naming?**
- **Domain**: Uses specific names (`expenseId`, `approvalId`) for type safety and clarity
- **Database**: Uses consistent `record_id` across all tables for uniformity
- **API**: Uses specific names (`expenseId`, `approvalId`) for clarity - no ambiguity about which entity ID

---

## Complete Response Wrapper Structure

All API responses are wrapped in a standard envelope:

```json
{
  "meta": {
    "requestId": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2025-01-15T12:00:00Z",
    "status": "SUCCESS"
  },
  "payload": {
    // Actual data here (ExpenseDto, ExpenseDetailDto, etc.)
  }
}
```

### Error Response

```json
{
  "meta": {
    "requestId": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2025-01-15T12:00:00Z",
    "status": "ERROR"
  },
  "error": {
    "code": "EXPENSE_NOT_FOUND",
    "message": "Expense not found with ID: 1234567890",
    "details": null
  }
}
```

---

## Summary

### Response DTOs

1. **ExpenseDto** - Lightweight list view (9 fields)
2. **ExpenseDetailDto** - Full detail view with approvals (10 fields)
3. **ApprovalDto** - Approval history (8 fields)

### All Response Fields

**Expense Fields:**
- `expenseId`, `userId`, `categoryId`, `amount`, `status`
- `receiptUrl`, `notes`
- `createdAt`, `updatedAt`
- `approvals` (array, only in ExpenseDetailDto)

**Approval Fields:**
- `approvalId`, `expenseId`, `approverId`, `decision`
- `comment`, `decidedAt`
- `createdAt`, `updatedAt`

### Timestamps Included

✅ All entities now return both:
- `createdAt` - When record was first created
- `updatedAt` - When record was last modified

This allows API consumers to:
- Track when expenses were submitted
- See when status changes occurred
- Monitor approval timestamps
- Implement caching strategies based on modification times
