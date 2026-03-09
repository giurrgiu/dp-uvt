package ro.uvt.fi.dp.client;

import ro.uvt.fi.dp.account.Account;
import ro.uvt.fi.dp.logging.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class Client {

    private String name;
    private String address;
    private List<Account> accounts;

    private static final AppLogger log = AppLogger.getInstance();

    public Client(String name, String address) {
        this.name = name;
        this.address = address;
        this.accounts = new ArrayList<>();
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

    @Override
    public String toString() {
        return "\n\tClient [name=" + name + ", address=" + address + ", accounts=" + accounts + "]";
    }
}
