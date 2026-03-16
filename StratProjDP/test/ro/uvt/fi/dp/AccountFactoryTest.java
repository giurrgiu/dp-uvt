package ro.uvt.fi.dp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ro.uvt.fi.dp.account.Account;
import ro.uvt.fi.dp.account.AccountEUR;
import ro.uvt.fi.dp.account.AccountFactory;
import ro.uvt.fi.dp.account.AccountRON;
import ro.uvt.fi.dp.account.Currency;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountFactory Tests")
class AccountFactoryTest {

    @Test
    @DisplayName("createAccount with EUR returns an AccountEUR instance")
    void createAccount_eur_returnsAccountEUR() {
        Account account = AccountFactory.createAccount(Currency.EUR, "EUR001", 500.0);
        assertInstanceOf(AccountEUR.class, account);
    }

    @Test
    @DisplayName("createAccount with RON returns an AccountRON instance")
    void createAccount_ron_returnsAccountRON() {
        Account account = AccountFactory.createAccount(Currency.RON, "RON001", 200.0);
        assertInstanceOf(AccountRON.class, account);
    }

    @Test
    @DisplayName("createAccount sets the correct account code")
    void createAccount_setsAccountCode() {
        Account account = AccountFactory.createAccount(Currency.EUR, "EUR042", 100.0);
        assertEquals("EUR042", account.getAccountCode());
    }

    @Test
    @DisplayName("createAccount sets the correct initial amount")
    void createAccount_setsInitialAmount() {
        Account account = AccountFactory.createAccount(Currency.RON, "RON042", 350.0);
        assertEquals(350.0, account.getAmount(), 1e-9);
    }

    @Test
    @DisplayName("createAccount EUR has EUR currency")
    void createAccount_eur_hasCurrencyEUR() {
        Account account = AccountFactory.createAccount(Currency.EUR, "EUR001", 100.0);
        assertEquals(Currency.EUR, account.getCurrency());
    }

    @Test
    @DisplayName("createAccount RON has RON currency")
    void createAccount_ron_hasCurrencyRON() {
        Account account = AccountFactory.createAccount(Currency.RON, "RON001", 100.0);
        assertEquals(Currency.RON, account.getCurrency());
    }

}
