package ro.uvt.fi.dp.account;

public class AccountFactory {

    public static Account createAccount(Currency currency, String accountCode, double initialAmount) {
        if (currency == Currency.EUR) {
            return new AccountEUR(accountCode, initialAmount);
        } else {
            return new AccountRON(accountCode, initialAmount);
        }
    }
}