# Final Project Features

---

## Chosen Feature

Client GUI with serialization

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
