package ro.uvt.fi.dp.command;

import ro.uvt.fi.dp.account.Account;

public class DeposeCommand implements AccountCommand {

    private final Account account;
    private final double amount;

    public DeposeCommand(Account account, double amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void execute() {
        account.depose(amount);
    }

    @Override
    public void undo() {
        account.retrieve(amount);
    }
}
