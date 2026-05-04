package ro.uvt.fi.dp.bank;

import ro.uvt.fi.dp.client.Client;
import ro.uvt.fi.dp.logging.AppLogger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bank implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bankCode;
    private List<Client> clients;

    private static final AppLogger log = AppLogger.getInstance();

    public Bank(String bankCode) {
        this.bankCode = bankCode;
        this.clients = new ArrayList<>();
        log.info("BANK CREATED: code=" + bankCode);
    }

    public void addClient(Client client) {
        clients.add(client);
        log.info("CLIENT ADDED to bank [" + bankCode + "]: " + client.getName());
    }

    public Client getClient(String name) {
        for (Client client : clients) {
            if (client.getName().equals(name)) {
                log.info("CLIENT FOUND: bank=" + bankCode + ", name=" + name);
                return client;
            }
        }
        log.warning("CLIENT NOT FOUND: bank=" + bankCode + ", name=" + name);
        return null;
    }

    @Override
    public String toString() {
        return "Bank [code=" + bankCode + ", clients=" + clients + "]";
    }

    public String getBankCode() {
        return bankCode;
    }

    public List<Client> getClients() {
        return Collections.unmodifiableList(clients);
    }
}
