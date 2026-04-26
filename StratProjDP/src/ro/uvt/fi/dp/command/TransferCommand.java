package ro.uvt.fi.dp.command;

import ro.uvt.fi.dp.account.Account;
import ro.uvt.fi.dp.account.CurrencyConverter;

public class TransferCommand implements AccountCommand {

    private final Account source;
    private final Account dest;
    private final double amount;
    private double convertedAmount;

    public TransferCommand(Account source, Account dest, double amount) {
        this.source = source;
        this.dest = dest;
        this.amount = amount;
    }

    @Override
    public void execute() {
        convertedAmount = CurrencyConverter.convert(amount, source.getCurrency(), dest.getCurrency());
        source.transferTo(dest, amount);
    }

    @Override
    public void undo() {
        dest.transferTo(source, convertedAmount);
    }
}
