package ro.uvt.fi.dp.account;

import java.io.Serializable;

public interface Operations extends Serializable {
    double getTotalAmount();

    void depose(double amount);

    void retrieve(double amount);
}
