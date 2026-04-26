package ro.uvt.fi.dp.visitor;

import ro.uvt.fi.dp.account.AccountEUR;
import ro.uvt.fi.dp.account.AccountRON;

public interface AccountVisitor {
    String visit(AccountEUR account);
    String visit(AccountRON account);
}
