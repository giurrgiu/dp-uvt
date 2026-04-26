# Final Project Features

---

## Chosen Features

1. Client GUI with serialization
2. Bank officer GUI with serialization
3. 3-4 behavioral design patterns
4. Refactor: remove per currency Account subclasses

---

## 1. Client GUI (JavaFX + Serialization)

A graphical interface from the **client's perspective** allowing account management and transactions.

**Screens:**

```
 [Login Screen]          [Dashboard]                  [Transfer Screen]
 ┌──────────────┐       ┌────────────────────────┐   ┌──────────────────────┐
 │ Client Name: │       │ Welcome, <name>        │   │ From: [ACC-001  v]   │
 │ [__________] │──────▶│                        │   │ To:   [ACC-002  v]   │
 │              │       │ Accounts:              │   │ Amount: [________]   │
 │ [  Login   ] │       │ ┌────────────────────┐ │   │                      │
 └──────────────┘       │ │ EUR-001  €1200.00  │ │   │ [ Transfer ]         │
                        │ │ RON-001  ₺3500.00  │ │   └──────────────────────┘
                        │ └────────────────────┘ │
                        │                        │
                        │ [Deposit] [Withdraw]   │
                        │ [Transfer] [History]   │
                        └────────────────────────┘
```

**Key functionality:**

- View all accounts and balances
- Deposit / withdraw with amount validation
- Transfer between own accounts (cross-currency supported)
- View transaction history per account
- Data persisted to disk via Java serialization

---

## 2. Bank Officer GUI (JavaFX + Serialization)

A graphical interface from the **bank officer's perspective** for administrative operations.

**Screens:**

```
 [Officer Login]        [Admin Dashboard]              [Client Detail]
 ┌──────────────┐      ┌─────────────────────────┐   ┌───────────────────────┐
 │ Officer ID:  │      │ Bank: BTUV              │   │ Client: Ion Popescu   │
 │ [__________] │─────▶│                         │   │ Address: Timisoara    │
 │              │      │ Clients:                │   │                       │
 │ [  Login   ] │      │ ┌─────────────────────┐ │   │ Accounts:             │
 └──────────────┘      │ │ Ion Popescu         │ │──▶│ ┌───────────────────┐ │
                       │ │ Maria Ionescu       │ │   │ │ EUR-001  €1200    │ │
                       │ │ ...                 │ │   │ │ RON-001  ₺3500    │ │
                       │ └─────────────────────┘ │   │ └───────────────────┘ │
                       │                         │   │                       │
                       │ [Add Client] [Remove]   │   │ [Add Account] [Close] │
                       └─────────────────────────┘   └───────────────────────┘
```

**Key functionality:**

- Create / remove clients
- Open / close accounts for any client
- View all clients and their account summaries
- Shared serialization file with the client GUI

---

## 3. Behavioral Design Patterns

Three behavioral patterns already implemented (lab6 branch), plus one new pattern to be added:

**Already implemented:**

| Pattern       | Where                  | Description                                                                                                                     |
| ------------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| **Command**   | Transaction processing | Each operation (deposit, withdraw, transfer) is encapsulated as a `Command` object with `execute()` / `undo()` support.         |
| **Visitor**   | Account auditing       | `AccountVisitor` interface with `AuditVisitor` implementation for generating account reports without modifying account classes. |
| **Decorator** | Account enhancements   | `AccountDecorator` base with `InterestBonusDecorator` and `NotificationDecorator` for adding behavior to accounts dynamically.  |

**To be added:**

| Pattern      | Where         | Description                                                                                              |
| ------------ | ------------- | -------------------------------------------------------------------------------------------------------- |
| **Observer** | Account → GUI | Accounts notify registered listeners on balance changes so the GUI updates in real-time without polling. |

These patterns add to the four already implemented (Factory, Builder, Singleton, Strategy), bringing the total to **8 design patterns**.

---

## 4. Refactor: Unified Account Class

**Current state:** `AccountEUR` and `AccountRON` are separate subclasses that differ only in their `getInterest()` and `getCurrency()` implementations.

**Proposed change:** Remove `AccountEUR` and `AccountRON`. Instead, the `Account` class holds a `Currency` enum value and an `InterestStrategy` (interface with `double calculate(double balance)`). Interest logic is injected at construction time via the Factory.

```
Before:                          After:
Account (abstract)               Account (concrete)
  ├── AccountEUR                   ├── has Currency enum
  └── AccountRON                   └── has InterestStrategy
                                         ├── FixedInterestStrategy (1% for EUR)
                                         └── TieredInterestStrategy (3%/8% for RON)
```

This makes adding new currencies trivial (e.g., USD, GBP) without creating new subclasses, and it makes the existing Strategy pattern explicit rather than implicit in the class hierarchy.

---
