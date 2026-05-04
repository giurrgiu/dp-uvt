package ro.uvt.fi.dp.account;

import ro.uvt.fi.dp.visitor.AccountVisitor;

import java.util.List;

public abstract class AccountDecorator extends Account {

    private static final long serialVersionUID = 1L;

    protected final Account wrapped;

    protected AccountDecorator(Account wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public void depose(double amount) {
        wrapped.depose(amount);
    }

    @Override
    public void retrieve(double amount) {
        wrapped.retrieve(amount);
    }

    @Override
    public double getTotalAmount() {
        return wrapped.getTotalAmount();
    }

    @Override
    public double getInterest() {
        return wrapped.getInterest();
    }

    @Override
    public Currency getCurrency() {
        return wrapped.getCurrency();
    }

    @Override
    public void transferTo(Account dest, double amount) {
        wrapped.transferTo(dest, amount);
    }

    @Override
    public double getAmount() {
        return wrapped.getAmount();
    }

    @Override
    public String getAccountCode() {
        return wrapped.getAccountCode();
    }

    @Override
    public List<Transaction> getHistory() {
        return wrapped.getHistory();
    }

    @Override
    public void printStatement() {
        wrapped.printStatement();
    }

    @Override
    public String accept(AccountVisitor visitor) {
        return wrapped.accept(visitor);
    }

    @Override
    public String toString() {
        return wrapped.toString();
    }
}
