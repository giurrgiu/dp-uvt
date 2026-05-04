package ro.uvt.fi.dp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uvt.fi.dp.account.AccountFactory;
import ro.uvt.fi.dp.account.Currency;
import ro.uvt.fi.dp.bank.Bank;
import ro.uvt.fi.dp.client.Client;
import ro.uvt.fi.dp.persistence.BankDataStore;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BankDataStore Tests")
class BankDataStoreTest {

    private static final String TEST_FILE = "test_bank_data.dat";
    private BankDataStore dataStore;

    @BeforeEach
    void setUp() {
        dataStore = new BankDataStore(TEST_FILE);
    }

    @AfterEach
    void tearDown() {
        new File(TEST_FILE).delete();
    }

    @Test
    @DisplayName("load returns null when file does not exist")
    void load_noFile_returnsNull() {
        assertNull(dataStore.load());
    }

    @Test
    @DisplayName("save and load preserves bank code")
    void saveAndLoad_preservesBankCode() {
        Bank bank = new Bank("BCR");
        dataStore.save(bank);
        Bank loaded = dataStore.load();
        assertNotNull(loaded);
        assertEquals("BCR", loaded.getBankCode());
    }

    @Test
    @DisplayName("save and load preserves client data")
    void saveAndLoad_preservesClient() {
        Bank bank = new Bank("BCR");
        Client client = new Client.Builder("Ion Popescu", "Timisoara")
                .email("ion@mail.com")
                .build();
        bank.addClient(client);
        dataStore.save(bank);

        Bank loaded = dataStore.load();
        Client loadedClient = loaded.getClient("Ion Popescu");
        assertNotNull(loadedClient);
        assertEquals("Ion Popescu", loadedClient.getName());
        assertEquals("ion@mail.com", loadedClient.getEmail());
    }

    @Test
    @DisplayName("save and load preserves accounts with balances")
    void saveAndLoad_preservesAccounts() {
        Bank bank = new Bank("BCR");
        Client client = new Client.Builder("Ion Popescu", "Timisoara").build();
        client.addAccount(AccountFactory.createAccount(Currency.EUR, "EUR001", 500.0));
        client.addAccount(AccountFactory.createAccount(Currency.RON, "RON001", 1000.0));
        bank.addClient(client);
        dataStore.save(bank);

        Bank loaded = dataStore.load();
        Client lc = loaded.getClient("Ion Popescu");
        assertEquals(500.0, lc.getAccount("EUR001").getAmount(), 1e-9);
        assertEquals(1000.0, lc.getAccount("RON001").getAmount(), 1e-9);
    }

    @Test
    @DisplayName("save and load preserves transaction history")
    void saveAndLoad_preservesTransactionHistory() {
        Bank bank = new Bank("BCR");
        Client client = new Client.Builder("Ion Popescu", "Timisoara").build();
        client.addAccount(AccountFactory.createAccount(Currency.EUR, "EUR001", 500.0));
        client.getAccount("EUR001").depose(200.0);
        bank.addClient(client);
        dataStore.save(bank);

        Bank loaded = dataStore.load();
        assertEquals(2, loaded.getClient("Ion Popescu").getAccount("EUR001").getHistory().size());
    }

    @Test
    @DisplayName("save and load preserves account currency")
    void saveAndLoad_preservesCurrency() {
        Bank bank = new Bank("BCR");
        Client client = new Client.Builder("Test", "Addr").build();
        client.addAccount(AccountFactory.createAccount(Currency.EUR, "EUR001", 100.0));
        bank.addClient(client);
        dataStore.save(bank);

        Bank loaded = dataStore.load();
        assertEquals(Currency.EUR, loaded.getClient("Test").getAccount("EUR001").getCurrency());
    }

    @Test
    @DisplayName("save overwrites previous data")
    void save_overwritesPrevious() {
        Bank bank1 = new Bank("BCR");
        dataStore.save(bank1);
        Bank bank2 = new Bank("BRD");
        dataStore.save(bank2);

        Bank loaded = dataStore.load();
        assertEquals("BRD", loaded.getBankCode());
    }
}
