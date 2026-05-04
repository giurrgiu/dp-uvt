package ro.uvt.fi.dp.account;

import java.io.Serializable;

public interface Transfer extends Serializable {
    void transferTo(Account dest, double amount);
}
