# FinFlow API Postman Collection

## Overview
This Postman collection contains all API endpoints for the FinFlow application, organized by resource folders.

## Collection Structure

```
FinFlow API/
├── Expense/
│   ├── Submit Expense (POST)
│   ├── Get Expense by ID (GET)
│   ├── List Expenses by User (GET)
│   ├── List Pending Expenses by Company (GET)
│   ├── Approve Expense (PUT)
│   └── Reject Expense (PUT)
└── [Future: Account, Invoice, Transaction, etc.]
```

## Request/Response Format

All API requests and responses use a **wrapped meta/payload structure**:

### Request Format (for POST/PUT/PATCH)
```json
{
  "meta": {
    "source": "postman",
    "requestId": "{{$guid}}"
  },
  "payload": {
    // Your actual request data here
  }
}
```

### Response Format
```json
{
  "meta": {
    "requestId": "abc-123-def",
    "timestamp": "2025-01-15T10:30:00Z",
    "status": "SUCCESS"
  },
  "payload": {
    // Your actual response data here
  }
}
```

## Required Headers

### For All Requests
- **X-RequestId**: Client-generated request trace ID (auto-generated as `{{$guid}}`)
- **Accept**: `application/json`

### For Write Operations (POST/PUT/PATCH)
- **Content-Type**: `application/json`
- **X-RequestId**: Client-generated request trace ID

### Optional (Multi-tenancy)
- **X-TenantCode**: Tenant identifier (disabled by default, enable when needed)

## Collection Variables

| Variable | Default Value | Description |
|----------|---------------|-------------|
| `baseUrl` | `http://localhost:8080` | Base URL for the API |
| `expenseId` | `1` | Sample expense ID for testing |
| `tenantCode` | `TENANT001` | Tenant code for multi-tenancy |

## Usage Guide

### 1. Import Collection
1. Open Postman
2. Click **Import**
3. Select `FinFlow-API.postman_collection.json`
4. Collection will appear in your workspace

### 2. Set Environment Variables (Optional)
You can override collection variables by creating an environment:
1. Create new environment (e.g., "FinFlow Dev")
2. Add variables:
   - `baseUrl`: `http://localhost:8080`
   - `expenseId`: `1`
   - `tenantCode`: `TENANT001`

### 3. Test the Endpoints

#### Example: Submit Expense
```
POST {{baseUrl}}/api/v1/expenses

Headers:
  Content-Type: application/json
  X-RequestId: {{$guid}}

Body:
{
  "meta": {
    "source": "postman",
    "requestId": "{{$guid}}"
  },
  "payload": {
    "userId": 1,
    "categoryId": 1,
    "amount": 150.50,
    "notes": "Client lunch meeting",
    "receiptUrl": "https://example.com/receipts/123.pdf"
  }
}

Response:
{
  "meta": {
    "requestId": "...",
    "timestamp": "...",
    "status": "SUCCESS"
  },
  "payload": {
    "expenseId": 5
  }
}
```

## Adding New Resource Folders

To add a new resource (e.g., Account, Invoice):

1. **In Postman UI:**
   - Right-click on "FinFlow API" collection
   - Select "Add Folder"
   - Name it (e.g., "Account")
   - Add requests inside the folder

2. **In JSON (manual edit):**
   ```json
   {
     "item": [
       {
         "name": "Expense",
         "item": [...]
       },
       {
         "name": "Account",
         "item": [
           {
             "name": "Create Account",
             "request": {
               "method": "POST",
               "header": [...],
               "body": {
                 "mode": "raw",
                 "raw": "{\n  \"meta\": {...},\n  \"payload\": {...}\n}"
               },
               "url": {...}
             }
           }
         ],
         "description": "Account management endpoints"
       }
     ]
   }
   ```

## Example: Complete Flow

### 1. Submit Expense
```
POST /api/v1/expenses
→ Returns: { "payload": { "expenseId": 5 } }
```

### 2. Update Collection Variable
Set `expenseId` to `5`

### 3. Get Expense Details
```
GET /api/v1/expenses/5
→ Returns expense with status PENDING
```

### 4. Approve Expense
```
PUT /api/v1/expenses/5/approve
Body: {
  "meta": {...},
  "payload": {
    "approverId": 2,
    "comment": "Approved"
  }
}
```

### 5. Verify Approval
```
GET /api/v1/expenses/5
→ Returns expense with status APPROVED and approval record
```

## Testing with Seed Data

The database includes seed data from migration `R__expense_seed_data.sql`:

- **Companies**: ID 1, 2
- **Users**: ID 1 (user), 2 (manager), 3 (admin), 4 (user)
- **Categories**: ID 1-7
- **Expenses**: ID 1-4 (various statuses)

You can use these IDs directly in your tests:
- Get expense 1: `GET /api/v1/expenses/1`
- List expenses for user 1: `GET /api/v1/expenses?userId=1`
- List pending for company 1: `GET /api/v1/expenses?companyId=1`

## Troubleshooting

### Issue: 400 Bad Request
- **Cause**: Request body not properly wrapped
- **Fix**: Ensure body has `meta` and `payload` structure

### Issue: 500 Internal Server Error
- **Cause**: Database not initialized or application not running
- **Fix**:
  1. Check PostgreSQL is running
  2. Run: `./gradlew bootRun`
  3. Flyway should auto-run migrations

### Issue: Missing X-RequestId header
- **Cause**: Required header not sent
- **Fix**: Enable X-RequestId header with value `{{$guid}}`

## Next Steps

1. ✅ Import collection to Postman
2. ✅ Start the application: `./gradlew bootRun`
3. ✅ Test "Submit Expense" endpoint
4. ✅ Add more resource folders as needed (Account, Invoice, etc.)
5. ✅ Share collection with team

## Notes

- All POST/PUT methods now use wrapped payload structure
- GET methods don't require body, but responses are still wrapped
- `{{$guid}}` auto-generates unique request IDs
- Approve/Reject use PUT method (RESTful convention for updates)
