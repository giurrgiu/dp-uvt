package ro.uvt.fi.dp.client;

import ro.uvt.fi.dp.account.Account;
import ro.uvt.fi.dp.logging.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class Client {

    // required fields
    private final String name;
    private final String address;
    private final List<Account> accounts;

    // optional fields
    private String email;

    private static final AppLogger log = AppLogger.getInstance();

    private Client(Builder builder) {
        this.name = builder.name;
        this.address = builder.address;
        this.accounts = builder.accounts;
        this.email = builder.email;
        log.info("CLIENT CREATED: name=" + name + ", address=" + address);
    }

    public void addAccount(Account account) {
        accounts.add(account);
        log.info("ACCOUNT ADDED to client [" + name + "]: " + account.getAccountCode());
    }

    public Account getAccount(String accountCode) {
        for (Account account : accounts) {
            if (account.getAccountCode().equals(accountCode)) {
                log.info("ACCOUNT FOUND: client=" + name + ", code=" + accountCode);
                return account;
            }
        }
        log.warning("ACCOUNT NOT FOUND: client=" + name + ", code=" + accountCode);
        return null;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "\n\tClient [name=" + name + ", address=" + address + ", email=" + email + ", accounts=" + accounts
                + "]";
    }

    public static class Builder {
        // required

        private final String name;
        private final String address;
        private final List<Account> accounts = new ArrayList<>();

        // optional
        private String email;

        public Builder(String name, String address) {
            this.name = name;
            this.address = address;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Client build() {
            return new Client(this);
        }
    }
}
