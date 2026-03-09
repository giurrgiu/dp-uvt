package ro.uvt.fi.dp.account;

public class CurrencyConverter {

    public static final double EUR_TO_RON = 5.0;
    public static final double RON_TO_EUR = 0.2;

    private CurrencyConverter() {
    }

    public static double convert(double amount, Currency from, Currency to) {
        if (from == to) {
            return amount;
        }
        if (from == Currency.EUR && to == Currency.RON) {
            return amount * EUR_TO_RON;
        }
        if (from == Currency.RON && to == Currency.EUR) {
            return amount * RON_TO_EUR;
        }
        throw new IllegalArgumentException("Unsupported conversion: " + from + " to " + to);
    }
}
