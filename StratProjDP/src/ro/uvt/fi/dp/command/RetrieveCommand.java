package ro.uvt.fi.dp.command;

import ro.uvt.fi.dp.account.Account;

public class RetrieveCommand implements AccountCommand {

    private final Account account;
    private final double amount;

    public RetrieveCommand(Account account, double amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void execute() {
        account.retrieve(amount);
    }

    @Override
    public void undo() {
        account.depose(amount);
    }
}
