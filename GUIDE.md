# FinFlow Backend — Contributor Guide

Read [../TECHNICAL-PRODUCT-MANAGER.md](../TECHNICAL-PRODUCT-MANAGER.md) and [../BACKEND-README.md](../BACKEND-README.md) first — the schema and endpoint tables live there, this guide is about *how the code is organized* and *what order to build things in*.

## Project Structure (Clean Architecture)

```
api/
├── pom.xml                     # Maven, groupId com.alsatech, artifactId finflow
└── src/main/kotlin/com/alsatech/finflow/
    ├── domain/
    │   ├── model/               # data classes for the 8 tables — no framework logic
    │   ├── repository/          # ports (interfaces) — no Spring/R2DBC imports allowed here
    │   └── exception/
    ├── application/
    │   ├── usecase/             # one class per business operation (SubmitExpenseUseCase, etc.)
    │   ├── port/                # interfaces for external services (LlmClient, EmailSender, FileStorage)
    │   ├── dto/                 # empty on purpose — add DTOs here as use cases need them
    │   └── mapper/               # empty on purpose — add mappers here as needed
    ├── infrastructure/
    │   ├── persistence/          # R2DBC repository interfaces + adapters implementing domain ports
    │   ├── security/              # JWT + Spring Security (reactive) config
    │   ├── config/                 # empty on purpose — add @Configuration classes as needed
    │   └── external/                # LLM/email/storage adapters implementing application/port interfaces
    └── interfaces/
        ├── rest/                     # @RestController classes, one per resource
        ├── dto/                      # empty on purpose — add request/response DTOs per endpoint
        └── advice/                    # global exception -> HTTP status mapping
```

**Dependency rule**: `interfaces → application → domain`, and `infrastructure → domain` (implements domain ports). Nothing in `domain` may import Spring, R2DBC, or any other framework type — it should be plain Kotlin.

Why: this is what makes "reactive Kotlin instead of JPA" a contained decision — if we ever needed to swap R2DBC for something else, only `infrastructure/persistence` changes, not the use cases or controllers.

## Reuse Before Rewrite

- One R2DBC repository interface + one adapter per entity, all following the exact shape of `ExpenseR2dbcRepository` / `ExpenseRepositoryAdapter` — don't invent a different pattern per entity.
- One `LlmClient`/`EmailSender`/`FileStorage` implementation per external provider, selected by config — never call a vendor SDK directly from a use case or controller.
- Every rejection/approval flows through `DecideExpenseUseCase`, whether triggered by a human click or (Phase 2) a voice draft. Don't add a second write path.
- `DecideExpenseUseCase` is also the only place a `reimbursements` row gets created (for `PERSONAL_FUNDS` approvals) — don't add a separate "create reimbursement" call site elsewhere; if reimbursement creation logic needs to change, it changes in one use case.

## How to Add a New Feature

1. Add/extend a `domain/model` if the schema needs a new table or column — write the Flyway migration in `src/main/resources/db/migration/` as the next `V{n}__description.sql` (never edit an applied migration).
2. Add a `domain/repository` port interface for the new queries.
3. Implement it in `infrastructure/persistence` (R2DBC repository + adapter).
4. Add a `domain/exception` type if a new failure case doesn't fit existing ones.
5. Add an `application/usecase` class that orchestrates the operation.
6. Add `interfaces/dto` request/response types and wire an `interfaces/rest` controller method, mapped to the path in BACKEND-README.md's endpoint table.
7. Add a test under `src/test/kotlin/...` mirroring the package path.

## Contributor Work Order (2-day increments)

| Days | Block | Deliverable |
|---|---|---|
| 1–2 | **Foundations** | R2DBC connection wired to a real local Postgres, Flyway migration runs clean (`V1__init.sql`), `JwtService` + `SecurityConfig` implemented, `/api/auth/login` and `/api/auth/register` working end to end |
| 3 | **Auth completeness** | `V2__mvp_expansion.sql` adds `password_reset_tokens` + `invitations`; `/api/auth/forgot-password`, `/api/auth/reset-password`, invite-accept flow (backs `10-register.html`, `11-forgot-password.html`) |
| 4–5 | **Accounts + Transactions** | `AccountRepositoryAdapter`, `TransactionRepositoryAdapter`, CSV parsing in `ImportTransactionsUseCase`, `AccountController` + `TransactionController` endpoints, including single-resource detail routes (backs `12-account-detail.html`, `13-transaction-detail.html`) |
| 6–7 | **Expenses** | `ExpenseRepositoryAdapter` completed, `SubmitExpenseUseCase` implemented (payload requires `payment_method`), `S3FileStorage.presignUploadUrl` for receipts, `ExpenseController` endpoints incl. detail route (backs `14-expense-detail.html`) |
| 8–9 | **Approvals** | `ApprovalRepositoryAdapter`, `DecideExpenseUseCase` (approve/reject + audit row), `ResendEmailSender` wired for notification emails, `approval_rules` table + routing logic |
| 10 | **Reimbursements** | `reimbursements` table, `ReimbursementRepositoryAdapter`, `MarkReimbursementPaidUseCase`, `ReimbursementController`. Critically: `DecideExpenseUseCase` from the block above must be *extended*, not duplicated — approving an expense with `payment_method = PERSONAL_FUNDS` creates the `reimbursements` row (`status = PENDING`) in the same transaction as the approval. Company-card expenses never touch this table (backs `17-reimbursements.html`) |
| 11–12 | **Invoices** | `InvoiceRepositoryAdapter` + `invoice_line_items`, `CreateInvoiceUseCase`, status transitions, `InvoiceController` endpoints (backs `15-invoice-detail.html`, `16-invoice-create.html`) |
| 13–14 | **Reports + Dashboard + Notifications** | Aggregation queries for `DashboardController`/`ReportController`, `notifications` table + `NotificationController` (header bell across all pages) |
| 15–16 | **Company/Settings** | `CompanyRepositoryAdapter`, `UserRepositoryAdapter`, team invite + role assignment, category management, `budgets` table (backs `09-settings.html` tabs) |
| 17–18 | **Tests + Hardening** | Unit tests per use case, integration tests per controller (WebTestClient + Testcontainers Postgres), `GlobalExceptionHandler` mapping every `DomainException` to a proper HTTP status |
| 19–20 | **Backoffice (Super Admin)** | `audit_logs` table + write-path hooked into every state-changing use case, `AdminCompanyController`/`AdminUserController`/`AdminAuditLogController` (backs `admin-03` through `admin-08`); `feature_flags`/`plans`/`subscriptions` only if a payment provider is being integrated — otherwise stub `admin-09-billing.html` behind static data |
| 21+ | **Phase 2 — Voice AI** | Only after Blocks 1–20 are stable. Implement `GroqLlmClient`, `ParseVoiceExpenseUseCase`, `AiController` — always return a draft, never auto-submit or auto-approve |

**Rule for every block**: don't invent a new persistence/config pattern if `ExpenseRepositoryAdapter`/`SecurityConfig` already show the shape — copy the pattern.

## Setup
```bash
cd api
# requires a local PostgreSQL database named `finflow` (see application.yml)
mvn spring-boot:run
```
