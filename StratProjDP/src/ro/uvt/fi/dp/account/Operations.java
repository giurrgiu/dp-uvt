package ro.uvt.fi.dp.account;

public interface Operations {
    double getTotalAmount();

    void depose(double amount);

    void retrieve(double amount);
}
