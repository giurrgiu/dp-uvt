package ro.uvt.fi.dp.account;

import ro.uvt.fi.dp.logging.AppLogger;

public class NotificationDecorator extends AccountDecorator {

    private static final long serialVersionUID = 1L;

    private final String notificationType;
    private static final AppLogger log = AppLogger.getInstance();

    public NotificationDecorator(Account wrapped, String notificationType) {
        super(wrapped);
        this.notificationType = notificationType;
    }

    @Override
    public void depose(double amount) {
        wrapped.depose(amount);
        notify("Deposit of " + amount + " " + getCurrency());
    }

    @Override
    public void retrieve(double amount) {
        wrapped.retrieve(amount);
        notify("Withdrawal of " + amount + " " + getCurrency());
    }

    @Override
    public void transferTo(Account dest, double amount) {
        wrapped.transferTo(dest, amount);
        notify("Transfer of " + amount + " " + getCurrency() + " to " + dest.getAccountCode());
    }

    private void notify(String message) {
        log.info("[" + notificationType + " NOTIFICATION] " + message + " on account " + getAccountCode());
    }

    public String getNotificationType() {
        return notificationType;
    }
}
