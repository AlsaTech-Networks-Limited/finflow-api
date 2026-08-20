# Expense API Implementation Summary

## Overview
Complete end-to-end implementation of the Expense resource following Clean Architecture and reactive programming principles.

## Architecture Pattern

### Structure
```
resource/expense/
├── api/                              # API Layer (Entry point)
│   ├── ExpenseApiConstants.kt       # Route constants and resource name
│   ├── ExpenseApiHandler.kt         # Handler functions (ServerRequest → ServerResponse)
│   └── ExpenseApiResource.kt        # Route definitions (@Bean RouterFunction)
│
├── application/dto/                  # DTOs (Commands, Queries, Responses)
│   └── ExpenseDto.kt
│
├── domain/                           # Domain Layer (Business entities & ports)
│   ├── Expense.kt                   # Expense entity
│   ├── Approval.kt                  # Approval entity
│   ├── ExpenseRepository.kt         # Repository interface (port)
│   └── ApprovalRepository.kt        # Repository interface (port)
│
└── infrastructure/                   # Infrastructure Layer (Implementations)
    ├── actions/                     # Write operations (create, update, delete)
    │   ├── ExpenseSubmitAction.kt
    │   ├── ExpenseApproveAction.kt
    │   ├── ExpenseRejectAction.kt
    │   └── usecase/
    │       ├── ExpenseSubmitUseCase.kt
    │       ├── ExpenseApproveUseCase.kt
    │       └── ExpenseRejectUseCase.kt
    │
    ├── service/                     # Service interfaces and implementations
    │   ├── ExpenseWriteService.kt   # Interface for write operations
    │   ├── ExpenseReadService.kt    # Interface for read operations
    │   └── impl/
    │       ├── ExpenseWriteServiceImpl.kt
    │       └── ExpenseReadServiceImpl.kt
    │
    └── persistence/                  # Repository adapters
        ├── ExpenseRepositoryAdapter.kt
        └── ApprovalRepositoryAdapter.kt
```

## Key Design Decisions

### 1. Actions vs Services
- **Actions**: Write operations (submit, approve, reject) that modify state
- **Read Service**: Read operations (fetch, list) that query state
- This follows CQRS-lite principles

### 2. Functional Routing (Not @RestController)
```kotlin
@Bean(name = ["expenseApi"])
fun routes(handler: ExpenseApiHandler): RouterFunction<ServerResponse> =
    RouterFunctions
        .route(POST(BASE_ROUTE).and(accept(MediaType.APPLICATION_JSON)), handler::submitExpense)
        .andRoute(GET(EXPENSE_BY_ID).and(accept(MediaType.APPLICATION_JSON)), handler::fetchExpenseById)
```

### 3. Reactive All the Way
- All methods return `Mono<T>` or `Flux<T>`
- Uses R2dbcEntityTemplate for database operations
- No blocking code

### 4. Long IDs (Not UUID)
- All entities use `BIGSERIAL` (PostgreSQL) / `Long` (Kotlin)
- Better performance and simpler joins

## API Endpoints

### Base Route: `/api/v1/expenses`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/expenses` | Submit new expense |
| GET | `/api/v1/expenses/{expenseId}` | Get expense by ID with approvals |
| GET | `/api/v1/expenses?userId=1` | List expenses by user |
| GET | `/api/v1/expenses?companyId=1` | List pending expenses by company |
| POST | `/api/v1/expenses/{expenseId}/approve` | Approve expense |
| POST | `/api/v1/expenses/{expenseId}/reject` | Reject expense |

## Database Schema

### Migrations
1. **V1__init.sql** - Base schema (companies, users, categories, accounts)
2. **V2__create_expense_tables_2025_01_15.sql** - Expense and approval tables
3. **V3__seed_test_data_2025_01_15.sql** - Test data

### Expense Table
```sql
CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    amount NUMERIC(14, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    receipt_url TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### Approval Table
```sql
CREATE TABLE approvals (
    id BIGSERIAL PRIMARY KEY,
    expense_id BIGINT NOT NULL REFERENCES expenses(id),
    approver_id BIGINT NOT NULL REFERENCES users(id),
    decision VARCHAR(50) NOT NULL CHECK (decision IN ('APPROVED', 'REJECTED')),
    comment TEXT,
    decided_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

## Testing

### 1. Run Migrations
Ensure PostgreSQL is running:
```bash
# Database should already be created as per application.yml
# Flyway will auto-run migrations on application startup
./gradlew bootRun
```

### 2. Import Postman Collection
Import: `src/main/resources/postman/FinFlow-Expense-API.postman_collection.json`

Variables:
- `baseUrl`: `http://localhost:8080`
- `expenseId`: `1` (or any valid expense ID)

### 3. Test Sequence

#### Step 1: Submit Expense
```
POST {{baseUrl}}/api/v1/expenses
{
  "userId": 1,
  "categoryId": 1,
  "amount": 150.50,
  "notes": "Client lunch meeting",
  "receiptUrl": "https://example.com/receipts/123.pdf"
}

Response:
{
  "expenseId": 5
}
```

#### Step 2: Get Expense by ID
```
GET {{baseUrl}}/api/v1/expenses/5

Response:
{
  "id": 5,
  "userId": 1,
  "categoryId": 1,
  "amount": 150.50,
  "status": "PENDING",
  "receiptUrl": "https://example.com/receipts/123.pdf",
  "notes": "Client lunch meeting",
  "approvals": [],
  "createdAt": "2025-01-15T10:30:00Z"
}
```

#### Step 3: Approve Expense
```
POST {{baseUrl}}/api/v1/expenses/5/approve
{
  "approverId": 2,
  "comment": "Approved - valid business expense"
}

Response:
{
  "status": "approved"
}
```

#### Step 4: Verify Approval
```
GET {{baseUrl}}/api/v1/expenses/5

Response:
{
  "id": 5,
  "userId": 1,
  "categoryId": 1,
  "amount": 150.50,
  "status": "APPROVED",
  "receiptUrl": "https://example.com/receipts/123.pdf",
  "notes": "Client lunch meeting",
  "approvals": [
    {
      "approverId": 2,
      "decision": "APPROVED",
      "comment": "Approved - valid business expense",
      "decidedAt": "2025-01-15T10:35:00Z"
    }
  ],
  "createdAt": "2025-01-15T10:30:00Z"
}
```

#### Step 5: List Expenses
```
GET {{baseUrl}}/api/v1/expenses?userId=1

Response (Flux):
[
  {
    "id": 1,
    "userId": 1,
    "categoryId": 6,
    "amount": 150.50,
    "status": "PENDING",
    ...
  },
  {
    "id": 2,
    "userId": 1,
    "categoryId": 4,
    "amount": 850.00,
    "status": "APPROVED",
    ...
  }
]
```

## Business Rules

### Submit Expense
- Amount must be positive
- Category and User must exist
- Initial status is PENDING

### Approve/Reject Expense
- Expense must exist
- Expense must be in PENDING status
- Users cannot approve their own expenses
- Comment is required for rejection

## Configuration

### application.yml
```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/finflow
    username: finflow
    password: finflow

  flyway:
    url: jdbc:postgresql://localhost:5432/finflow
    user: finflow
    password: finflow
    locations: classpath:db/migration

server:
  port: 8080
```

## Next Steps

1. **Add Authentication**: Integrate JWT authentication to get current user
2. **Add Validation**: Move business validation to dedicated service
3. **Add Error Handling**: Implement global exception handler for functional routing
4. **Add Logging**: Add structured logging for audit trail
5. **Add Metrics**: Integrate Micrometer for API metrics
6. **Add Tests**: Unit and integration tests for all layers

## Files Created/Modified

### Created (20+ files)
- API layer: 3 files
- Infrastructure actions: 3 action files + 3 use case files
- Infrastructure services: 2 interfaces + 2 implementations
- Database migrations: 3 SQL files
- Postman collection: 1 file
- Documentation: This file

### Modified
- ExpenseApiHandler.kt - Updated to use actions and read service
- V1__init.sql - Split to base schema only
- Domain package structure reorganization

## Success Criteria ✅

- [x] API follows functional routing pattern (like reference)
- [x] Actions for write operations
- [x] Read service for queries
- [x] Reactive all the way (Mono/Flux)
- [x] Database migrations with Flyway
- [x] Postman collection for testing
- [x] Seed data for development
- [x] Clean separation of concerns
- [x] Long IDs instead of UUID
