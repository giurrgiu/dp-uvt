package ro.uvt.fi.dp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ro.uvt.fi.dp.account.AccountEUR;
import ro.uvt.fi.dp.account.AccountRON;
import ro.uvt.fi.dp.client.Client;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Client Tests")
class ClientTest {

    private Client client;

    @BeforeEach
    void setup() {
        client = new Client("John Pork", "Timisoara, Str. 67");
    }

    // Tests for adding accounts feature

    @Test
    @DisplayName("addAccount stores a RON that can be retrieved by code")
    void addAccount_ronAccountRetrievable() {
        AccountRON ron = new AccountRON("RON001", 100.0);
        client.addAccount(ron);
        assertSame(ron, client.getAccount("RON001"));
    }

    @Test
    @DisplayName("addAccount stores an EUR that can be retrieved by code")
    void addAccount_eurAccountRetrievable() {
        AccountEUR eur = new AccountEUR("EUR001", 500.0);
        client.addAccount(eur);
        assertSame(eur, client.getAccount("EUR001"));
    }

    @Test
    @DisplayName("addAccount allows adding multiple accounts of different types")
    void addAccount_multipleAccounts() {
        AccountRON ron = new AccountRON("RON001", 100.0);
        AccountEUR eur = new AccountEUR("EUR001", 500.0);
        client.addAccount(ron);
        client.addAccount(eur);
        assertSame(ron, client.getAccount("RON001"));
        assertSame(eur, client.getAccount("EUR001"));
    }

    @Test
    @DisplayName("addAccount allows more than 5 accounts ")
    void addAccount_moreThan5Accounts() {
        for (int i = 0; i < 10; i++) {
            client.addAccount(new AccountRON("RON" + i, 10.0));
        }
        assertNotNull(client.getAccount("RON9"));
    }

    // Tests for getting accounts feature

    @Test
    @DisplayName("getAccount returns null when code does not exist")
    void getAccount_unknownCode_returnsNull() {
        client.addAccount(new AccountRON("RON001", 100.0));
        assertNull(client.getAccount("UNKNOWN"));
    }

    @Test
    @DisplayName("getAccount is case sensitive for account codes")
    void getAccount_caseSensitive() {
        client.addAccount(new AccountRON("RON001", 100.0));
        assertNull(client.getAccount("ron001"));
    }

    // Tests for testing operations on accounts

    @Test
    @DisplayName("depose by getAccount updates the account balance")
    void depose_byGetAccount_updatesBalance() {
        client.addAccount(new AccountRON("RON001", 100.0));
        client.getAccount("RON001").depose(200.0);
        assertEquals(300.0, client.getAccount("RON001").getAmount(), 1e-9);
    }

    @Test
    @DisplayName("retrieve by getAccount updates the account balance")
    void retrieve_byGetAccount_updatesBalance() {
        client.addAccount(new AccountRON("RON001", 400.0));
        client.getAccount("RON001").retrieve(100.0);
        assertEquals(300.0, client.getAccount("RON001").getAmount(), 1e-9);
    }

}
