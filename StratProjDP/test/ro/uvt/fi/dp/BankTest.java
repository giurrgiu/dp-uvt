package ro.uvt.fi.dp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ro.uvt.fi.dp.account.Account;
import ro.uvt.fi.dp.account.AccountEUR;
import ro.uvt.fi.dp.account.AccountRON;
import ro.uvt.fi.dp.bank.Bank;
import ro.uvt.fi.dp.client.Client;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bank Tests")
class BankTest {

    private Bank bank;
    private Client client1;
    private Client client2;

    @BeforeEach
    void setup() {
        bank = new Bank("BCR Bank");
        client1 = new Client.Builder("John Pork", "Timisoara, Str. 67").build();
        client2 = new Client.Builder("Kiryu Kazuma", "Timisoara, Str. 68").build();
        client1.addAccount(new AccountEUR("EUR001", 200.0));
        client1.addAccount(new AccountRON("RON001", 400.0));
        client2.addAccount(new AccountRON("RON002", 100.0));
    }

    // Tests for adding clients feature

    @Test
    @DisplayName("addClient makes client retrievable by name")
    void addClient_clientRetrievable() {
        bank.addClient(client1);
        assertSame(client1, bank.getClient("John Pork"));
    }

    @Test
    @DisplayName("addClient allows multiple clients")
    void addClient_multipleClients() {
        bank.addClient(client1);
        bank.addClient(client2);
        assertSame(client1, bank.getClient("John Pork"));
        assertSame(client2, bank.getClient("Kiryu Kazuma"));
    }

    // Tests for getting clients feature

    @Test
    @DisplayName("getClient returns null when name does not exist")
    void getClient_unknownName_returnsNull() {
        bank.addClient(client1);
        assertNull(bank.getClient("Unknown Person"));
    }

    @Test
    @DisplayName("getClient is case-sensitive")
    void getClient_caseSensitive() {
        bank.addClient(client1);
        assertNull(bank.getClient("John pork"));
    }

    @Test
    @DisplayName("getClient returns correct client when multiple exist")
    void getClient_multipleClients_returnsCorrectOne() {
        bank.addClient(client1);
        bank.addClient(client2);
        assertSame(client2, bank.getClient("Kiryu Kazuma"));
    }

    // Some integrations tests for operations on accounts via bank navigation

    @Test
    @DisplayName("depose via bank navigation updates correct account")
    void integration_depose_viaBank() {
        bank.addClient(client2);
        bank.getClient("Kiryu Kazuma").getAccount("RON002").depose(400.0);
        assertEquals(500.0,
                bank.getClient("Kiryu Kazuma").getAccount("RON002").getAmount(),
                1e-9);
    }

    @Test
    @DisplayName("retrieve via bank navigation updates correct account")
    void integration_retrieve_viaBank() {
        bank.addClient(client2);
        bank.getClient("Kiryu Kazuma").getAccount("RON002").retrieve(67.0);
        assertEquals(33.0,
                bank.getClient("Kiryu Kazuma").getAccount("RON002").getAmount(),
                1e-9);
    }

    @Test
    @DisplayName("transfer between two clients' accounts via bank navigation")
    void integration_transfer_betweenClientsAccounts() {
        bank.addClient(client1);
        bank.addClient(client2);

        Account source = bank.getClient("Kiryu Kazuma").getAccount("RON002"); // 100
        Account dest = bank.getClient("John Pork").getAccount("RON001"); // 400

        source.transferTo(dest, 40.0);

        assertEquals(60.0, source.getAmount(), 1e-9);
        assertEquals(440.0, dest.getAmount(), 1e-9);
    }

}
