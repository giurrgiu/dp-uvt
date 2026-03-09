package ro.uvt.fi.dp.account;

public interface Transfer {
    void transferTo(Account dest, double amount);
}
