package ro.uvt.fi.dp.account;

import ro.uvt.fi.dp.exception.InvalidAmountException;
import ro.uvt.fi.dp.logging.AppLogger;
import ro.uvt.fi.dp.visitor.AccountVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account implements Operations, Transfer {

    private String accountCode;
    private double amount;
    private List<Transaction> history;

    private static final AppLogger log = AppLogger.getInstance();

    protected Account(String accountCode, double initialAmount) {
        this.accountCode = accountCode;
        this.amount = 0;
        this.history = new ArrayList<>();
        depose(initialAmount);
    }

    protected Account(Account wrapped) {
        this.accountCode = wrapped.getAccountCode();
        this.amount = 0;
        this.history = new ArrayList<>();
    }

    @Override
    public double getTotalAmount() {
        double total = amount + amount * getInterest();
        log.info("getTotalAmount [" + accountCode + "]: amount=" + amount
                + ", interest=" + getInterest() + ", total=" + total);
        return total;
    }

    @Override
    public void depose(double amount) {
        if (amount <= 0) {
            String msg = "Deposit amount must be positive, got: " + amount + " [account=" + accountCode + "]";
            log.warning("INVALID DEPOSE: " + msg);
            throw new InvalidAmountException(msg);
        }
        this.amount += amount;
        history.add(new Transaction(Transaction.Type.DEPOSE, amount));
        log.info("DEPOSE [" + accountCode + "]: +" + amount + " → balance=" + this.amount);
    }

    @Override
    public void retrieve(double amount) {
        if (amount <= 0) {
            String msg = "Withdrawal amount must be positive, got: " + amount + " [account=" + accountCode + "]";
            log.warning("INVALID RETRIEVE: " + msg);
            throw new InvalidAmountException(msg);
        }
        this.amount -= amount;
        history.add(new Transaction(Transaction.Type.RETRIEVE, amount));
        log.info("RETRIEVE [" + accountCode + "]: -" + amount + " → balance=" + this.amount);
        if (this.amount < 0) {
            log.warning("OVERDRAFT [" + accountCode + "]: balance is negative (" + this.amount + ")");
        }
    }

    protected void withdrawForTransfer(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Transfer amount must be positive, got: " + amount + " [account=" + accountCode + "]");
        }
        this.amount -= amount;
        history.add(new Transaction(Transaction.Type.TRANSFER_OUT, amount));
        log.info("TRANSFER_OUT [" + accountCode + "]: -" + amount + " → balance=" + this.amount);
    }

    protected void depositForTransfer(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Transfer amount must be positive, got: " + amount + " [account=" + accountCode + "]");
        }
        this.amount += amount;
        history.add(new Transaction(Transaction.Type.TRANSFER_IN, amount));
        log.info("TRANSFER_IN [" + accountCode + "]: +" + amount + " → balance=" + this.amount);
    }

    public List<Transaction> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void printStatement() {
        System.out.println("--- Statement for account " + accountCode + " (" + getCurrency() + ") ---");
        if (history.isEmpty()) {
            System.out.println("  No transactions.");
        } else {
            for (Transaction t : history) {
                System.out.println("  " + t);
            }
        }
        System.out.printf("  Current balance: %.2f %s%n", amount, getCurrency());
        System.out.println("-".repeat(55));
    }

    public String getAccountCode() {
        return accountCode;
    }

    public double getAmount() {
        return amount;
    }

    public abstract double getInterest();

    public abstract Currency getCurrency();

    public abstract String accept(AccountVisitor visitor);

    @Override
    public abstract void transferTo(Account dest, double amount);

    @Override
    public abstract String toString();
}
