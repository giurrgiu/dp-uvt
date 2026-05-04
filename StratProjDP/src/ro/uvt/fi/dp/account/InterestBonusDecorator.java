package ro.uvt.fi.dp.account;

public class InterestBonusDecorator extends AccountDecorator {

    private static final long serialVersionUID = 1L;

    private final double bonusRate;

    public InterestBonusDecorator(Account wrapped, double bonusRate) {
        super(wrapped);
        this.bonusRate = bonusRate;
    }

    @Override
    public double getInterest() {
        return wrapped.getInterest() + bonusRate;
    }

    @Override
    public double getTotalAmount() {
        double amount = wrapped.getAmount();
        return amount + amount * getInterest();
    }

    public double getBonusRate() {
        return bonusRate;
    }
}
