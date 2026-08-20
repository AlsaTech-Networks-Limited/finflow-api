# Notifications Module - Implementation Specification

## Overview
The Notifications module provides an in-app activity feed for users, showing approval decisions, invoice status changes, team invites, and other important events.


---

## Requirements

### 1. Database Schema

**Migration File:** `V7__create_notifications_table_2025_01_15.sql`

**Table:** `exp_finlow.notifications`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-incrementing ID |
| `user_id` | BIGINT | NOT NULL, FK to users(id) ON DELETE CASCADE | Recipient user |
| `type` | VARCHAR(50) | NOT NULL | Notification type enum |
| `title` | VARCHAR(250) | NOT NULL | Notification headline |
| `body` | TEXT | NOT NULL | Notification message |
| `related_entity_type` | VARCHAR(50) | NULL | Type of related entity (e.g., "EXPENSE", "INVOICE") |
| `related_entity_id` | VARCHAR(250) | NULL | ID of related entity |
| `read_at` | TIMESTAMP | NULL | When user marked as read |
| `created_at` | TIMESTAMP | NOT NULL DEFAULT NOW() | Creation timestamp |

**Indexes:**
- `idx_notifications_user_id` on `user_id`
- `idx_notifications_read_at` on `read_at`
- `idx_notifications_created_at` on `created_at`

**Notification Types (Enum):**
- `EXPENSE_APPROVED`
- `EXPENSE_REJECTED`
- `INVOICE_SENT`
- `INVOICE_PAID`
- `TEAM_INVITE`

---

### 2. Entities to Create

**Location:** `src/main/kotlin/com/alsatech/finflow/shared/domain/entity/`

**Entity:** `Notification`
- Map to table `notifications`
- Use `@Table` annotation
- Use `LocalDateTime` for timestamps (NOT Instant)
- Create `NotificationType` enum with the 5 types listed above
- All fields immutable (data class)

---

### 3. Repository Layer

#### Domain Repository Interface
**Location:** `src/main/kotlin/com/alsatech/finflow/shared/domain/repository/`

**Interface:** `NotificationRepository`

**Methods to implement:**
- `findById(id: Long): Mono<Notification>`
- `findByUserId(userId: Long): Flux<Notification>` - sorted by created_at DESC
- `findUnreadByUserId(userId: Long): Flux<Notification>` - where read_at is NULL
- `save(notification: Notification): Mono<Notification>`
- `markAsRead(notificationId: Long): Mono<Notification>` - sets read_at to now
- `markAllAsRead(userId: Long): Mono<Unit>` - marks all user's notifications as read

#### Infrastructure Repository Adapter
**Location:** `src/main/kotlin/com/alsatech/finflow/shared/infrastructure/persistence/`

**Class:** `NotificationRepositoryAdapter`
- Implement `NotificationRepository` interface
- Use `R2dbcEntityTemplate` for queries
- Use `@Repository` annotation
- Follow pattern from `ExpenseRepositoryAdapter` and `InvoiceRepositoryAdapter`

---

### 4. DTOs to Create

**Location:** `src/main/kotlin/com/alsatech/finflow/resource/notification/dto/`

**File:** `NotificationDto.kt`

**Command DTOs (with validation):**
- `MarkAsReadCommand`
  - Fields: `notificationId: Long`, `auditInfo: AuditInfo?`
  - Validation: @NotNull, @Positive on notificationId

- `MarkAllAsReadCommand`
  - Fields: `userId: Long`, `auditInfo: AuditInfo?`
  - Validation: @NotNull, @Positive on userId

- `ListNotificationsQuery`
  - Fields: `userId: Long`, `unreadOnly: Boolean = false`

**Response DTOs:**
- `NotificationDto`
  - All notification fields
  - Add `isRead: Boolean` (computed from readAt != null)
  - Use `@JsonFormat` for date fields (pattern: "yyyy-MM-dd'T'HH:mm:ss")

---

### 5. Validation Service

**Domain Interface Location:** `src/main/kotlin/com/alsatech/finflow/resource/notification/domain/service/`

**Interface:** `NotificationValidationService`

**Methods:**
- `validateMarkAsRead(command: MarkAsReadCommand): Mono<MarkAsReadCommand>`

**Implementation Location:** `src/main/kotlin/com/alsatech/finflow/resource/notification/infrastructure/service/`

**Class:** `NotificationValidationServiceImpl`

**Validation Rules:**
- Check notification exists
- (Optional) Check notification belongs to requesting user

---

### 6. Read and Write Services

#### Read Service

**Interface:** `NotificationReadService`
**Implementation:** `NotificationReadServiceImpl`
**Location:** `src/main/kotlin/com/alsatech/finflow/resource/notification/infrastructure/service/` (interface) and `impl/` (implementation)

**Methods:**
- `fetchNotificationById(id: Long): Mono<NotificationDto>`
- `fetchNotificationsByUser(userId: Long, unreadOnly: Boolean): Flux<NotificationDto>`

#### Write Service

**Interface:** `NotificationWriteService`
**Implementation:** `NotificationWriteServiceImpl`
**Location:** Same as read service

**Methods:**
- `markAsRead(command: MarkAsReadCommand): Mono<Unit>`
- `markAllAsRead(command: MarkAllAsReadCommand): Mono<Unit>`

---

### 7. Use Cases and Actions

**Location:** `src/main/kotlin/com/alsatech/finflow/resource/notification/infrastructure/actions/`

#### Use Cases (in `usecase/` subdirectory)

**UseCase:** `NotificationMarkAsReadUseCase`
- Implement `ActionWriteService<MarkAsReadCommand, Unit>`
- Call `NotificationWriteService.markAsRead()`
- `@Service` annotation

**UseCase:** `NotificationMarkAllAsReadUseCase`
- Implement `ActionWriteService<MarkAllAsReadCommand, Unit>`
- Call `NotificationWriteService.markAllAsRead()`
- `@Service` annotation

#### Actions

**Action:** `NotificationMarkAsReadAction`
- Implement `ActionWorkFlowService<MarkAsReadCommand>`
- Inject `NotificationMarkAsReadUseCase` and `NotificationValidationService`
- Override `validate()` and `processRequest()` methods
- Use `handleActionExecution()` in processRequest

**Action:** `NotificationMarkAllAsReadAction`
- Same pattern as above for mark all as read

---

### 8. API Layer

**Location:** `src/main/kotlin/com/alsatech/finflow/resource/notification/api/`

#### API Constants
**File:** `NotificationApiConstants.kt`

**Constants to define:**
```
BASE_ROUTE = "/api/v1/notifications"
NOTIFICATION_BY_ID = "$BASE_ROUTE/{notificationId}"
MARK_AS_READ = "$BASE_ROUTE/{notificationId}/mark-read"
MARK_ALL_AS_READ = "$BASE_ROUTE/mark-all-read"
RESOURCE_NAME = "NOTIFICATION"
```

#### API Handler
**File:** `NotificationApiHandler.kt`

**Methods to implement:**
- `fetchNotificationById(serverRequest)` - GET by ID
- `fetchNotificationsByUser(serverRequest)` - GET list with query params (userId, unreadOnly)
- `markAsRead(serverRequest)` - POST to mark single as read
- `markAllAsRead(serverRequest)` - POST to mark all as read

**Use these helper functions:**
- `wrapGetRequestApiResponse()` for GET endpoints
- `wrapInFluxApiResponse()` for list endpoints
- `wrapRequestWithBodyInApiResponse()` for POST endpoints

#### API Resource
**File:** `NotificationApiResource.kt`

**Register routes:**
- `GET /api/v1/notifications?userId={id}&unreadOnly={bool}`
- `GET /api/v1/notifications/{notificationId}`
- `POST /api/v1/notifications/{notificationId}/mark-read`
- `POST /api/v1/notifications/mark-all-read?userId={id}`

Use `@Bean(name = ["notificationApiRoute"])` and `RouterFunctions`

---

## Implementation Order (Recommended)

1. Create database migration and run it
2. Create Notification entity and NotificationType enum
3. Create NotificationRepository interface
4. Create NotificationRepositoryAdapter implementation
5. Create all DTOs
6. Create NotificationValidationService interface and implementation
7. Create NotificationReadService and NotificationWriteService (interfaces and implementations)
8. Create use cases
9. Create actions
10. Create API constants, handler, and resource
11. Test with Postman

---

## Testing Checklist

Once implemented, test these scenarios:

- [ ] List all notifications for a user
- [ ] List only unread notifications for a user
- [ ] Get single notification by ID
- [ ] Mark single notification as read
- [ ] Mark all notifications as read for a user
- [ ] Verify notifications are sorted by created_at DESC
- [ ] Verify readAt timestamp is set correctly when marking as read
- [ ] Verify validation errors when notification doesn't exist

---

## Reference Implementations

Look at these existing modules for patterns:
- **Expenses** - Complete CRUD with validation (most similar)
- **Invoices** - Multiple actions, status transitions
- **Reimbursements** - Simple mark-as-paid pattern

---

## Common Pitfalls to Avoid

1. **Don't skip validation** - Always implement ValidationService
2. **Remember reactive types** - Use Mono/Flux, not blocking operations
3. **Follow naming conventions** - Service interfaces in domain/, implementations in infrastructure/
4. **Use LocalDateTime** - Not Instant (matches other entities)
5. **Don't forget @Service annotation** - On all service implementations
6. **Import the right Query** - Use `org.springframework.data.relational.core.query.Query`
7. **Use snake_case in SQL** - Column names like `user_id`, `read_at`, `created_at`
8. **Follow existing patterns** - Don't reinvent, copy from expenses/invoices

---

## Directory Structure You'll Create

```
src/main/kotlin/com/alsatech/finflow/
├── shared/
│   ├── domain/
│   │   ├── entity/
│   │   │   └── Notification.kt
│   │   └── repository/
│   │       └── NotificationRepository.kt
│   └── infrastructure/
│       └── persistence/
│           └── NotificationRepositoryAdapter.kt
└── resource/
    └── notification/
        ├── dto/
        │   └── NotificationDto.kt
        ├── domain/
        │   └── service/
        │       └── NotificationValidationService.kt
        ├── infrastructure/
        │   ├── service/
        │   │   ├── NotificationValidationServiceImpl.kt
        │   │   ├── NotificationReadService.kt
        │   │   ├── NotificationWriteService.kt
        │   │   └── impl/
        │   │       ├── NotificationReadServiceImpl.kt
        │   │       └── NotificationWriteServiceImpl.kt
        │   └── actions/
        │       ├── NotificationMarkAsReadAction.kt
        │       ├── NotificationMarkAllAsReadAction.kt
        │       └── usecase/
        │           ├── NotificationMarkAsReadUseCase.kt
        │           └── NotificationMarkAllAsReadUseCase.kt
        └── api/
            ├── NotificationApiConstants.kt
            ├── NotificationApiHandler.kt
            └── NotificationApiResource.kt
```
