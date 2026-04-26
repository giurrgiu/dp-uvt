package ro.uvt.fi.dp.visitor;

import ro.uvt.fi.dp.account.AccountEUR;
import ro.uvt.fi.dp.account.AccountRON;

public class AuditVisitor implements AccountVisitor {

    @Override
    public String visit(AccountEUR account) {
        return "=== EUR Account Audit ===\n"
                + "Account Code: " + account.getAccountCode() + "\n"
                + "Currency: EUR\n"
                + "Balance: " + String.format("%.2f", account.getAmount()) + "\n"
                + "Interest Rate: " + (account.getInterest() * 100) + "%\n"
                + "Total (with interest): " + String.format("%.2f", account.getTotalAmount()) + "\n"
                + "Transactions: " + account.getHistory().size() + "\n"
                + "EU Compliance: Subject to European Central Bank regulations";
    }

    @Override
    public String visit(AccountRON account) {
        return "=== RON Account Audit ===\n"
                + "Account Code: " + account.getAccountCode() + "\n"
                + "Currency: RON\n"
                + "Balance: " + String.format("%.2f", account.getAmount()) + "\n"
                + "Interest Rate: " + (account.getInterest() * 100) + "%\n"
                + "Total (with interest): " + String.format("%.2f", account.getTotalAmount()) + "\n"
                + "Transactions: " + account.getHistory().size() + "\n"
                + "Local Compliance: Subject to National Bank of Romania regulations";
    }
}
