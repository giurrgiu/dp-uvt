package ro.uvt.fi.dp.account;

import ro.uvt.fi.dp.logging.AppLogger;
import ro.uvt.fi.dp.visitor.AccountVisitor;

public class AccountEUR extends Account {

    private static final AppLogger log = AppLogger.getInstance();

    public AccountEUR(String accountCode, double amount) {
        super(accountCode, amount);
        log.info("ACCOUNT CREATED [EUR]: code=" + accountCode + ", initialAmount=" + amount);
    }

    @Override
    public Currency getCurrency() {
        return Currency.EUR;
    }

    @Override
    public double getInterest() {
        return 0.01;
    }

    @Override
    public void transferTo(Account dest, double amount) {
        log.info("TRANSFER [EUR]: from=" + getAccountCode()
                + " to=" + dest.getAccountCode() + ", amount=" + amount + " " + getCurrency());

        double amountToDeposit = CurrencyConverter.convert(amount, getCurrency(), dest.getCurrency());
        this.withdrawForTransfer(amount);
        dest.depositForTransfer(amountToDeposit);

        log.success("TRANSFER COMPLETE: source balance=" + getAmount()
                + " " + getCurrency() + ", dest balance=" + dest.getAmount() + " " + dest.getCurrency());
    }

    @Override
    public String accept(AccountVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Account EUR: code=" + getAccountCode() + ", amount=" + getAmount();
    }
}
