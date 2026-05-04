package ro.uvt.fi.dp.account;

import ro.uvt.fi.dp.logging.AppLogger;
import ro.uvt.fi.dp.visitor.AccountVisitor;

public class AccountRON extends Account {

    private static final long serialVersionUID = 1L;

    private static final AppLogger log = AppLogger.getInstance();

    public AccountRON(String accountCode, double amount) {
        super(accountCode, amount);
        log.info("ACCOUNT CREATED [RON]: code=" + accountCode + ", initialAmount=" + amount);
    }

    @Override
    public Currency getCurrency() {
        return Currency.RON;
    }

    @Override
    public double getInterest() {
        if (getAmount() < 500) {
            return 0.03;
        } else {
            return 0.08;
        }
    }

    @Override
    public void transferTo(Account dest, double amount) {
        log.info("TRANSFER [RON]: from=" + getAccountCode()
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
        return "Account RON: code=" + getAccountCode() + ", amount=" + getAmount();
    }
}
