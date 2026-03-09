package ro.uvt.fi.dp.account;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    public enum Type {
        DEPOSE,
        RETRIEVE,
        TRANSFER_IN,
        TRANSFER_OUT
    }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Type type;
    private final double amount;
    private final LocalDateTime timestamp;

    public Transaction(Type type, double amount) {
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-12s  amount=%.2f",
                timestamp.format(FMT), type, amount);
    }
}
