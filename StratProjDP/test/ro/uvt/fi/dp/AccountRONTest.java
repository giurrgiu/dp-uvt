package ro.uvt.fi.dp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ro.uvt.fi.dp.account.AccountEUR;
import ro.uvt.fi.dp.account.AccountRON;
import ro.uvt.fi.dp.exception.InvalidAmountException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountRON Tests")
class AccountRONTest {

    private AccountRON account;

    @BeforeEach
    void setup() {
        account = new AccountRON("RON001", 200.0);
    }

    // Tests for depose feature

    @Test
    @DisplayName("depose adds the given amount to the balance")
    void depose_addsAmount() {
        account.depose(100.0);
        assertEquals(300.0, account.getAmount(), 1e-9);
    }

    @Test
    @DisplayName("depose with zero throws InvalidAmountException")
    void depose_zeroAmount_throwsException() {
        assertThrows(InvalidAmountException.class, () -> account.depose(0.0));
    }

    @Test
    @DisplayName("depose with negative amount throws InvalidAmountException")
    void depose_negativeAmount_throwsException() {
        assertThrows(InvalidAmountException.class, () -> account.depose(-50.0));
    }

    @Test
    @DisplayName("depose exception does not modify the balance")
    void depose_exceptionLeavesBalanceUnchanged() {
        assertThrows(InvalidAmountException.class, () -> account.depose(-10.0));
        assertEquals(200.0, account.getAmount(), 1e-9);
    }

    // Tests for retrieve feature

    @Test
    @DisplayName("retrieve subtracts the given amount from the balance")
    void retrieve_subtractsAmount() {
        account.retrieve(100.0);
        assertEquals(100.0, account.getAmount(), 1e-9);
    }

    @Test
    @DisplayName("retrieve entire balance results in zero")
    void retrieve_entireBalance() {
        account.retrieve(200.0);
        assertEquals(0.0, account.getAmount(), 1e-9);
    }

    @Test
    @DisplayName("retrieve more than balance goes negative")
    void retrieve_moreThanBalance_goesNegative() {
        account.retrieve(300.0);
        assertEquals(-100.0, account.getAmount(), 1e-9);
    }

    @Test
    @DisplayName("retrieve with zero throws InvalidAmountException")
    void retrieve_zeroAmount_throwsException() {
        assertThrows(InvalidAmountException.class, () -> account.retrieve(0.0));
    }

    @Test
    @DisplayName("retrieve with negative amount throws InvalidAmountException")
    void retrieve_negativeAmount_throwsException() {
        assertThrows(InvalidAmountException.class, () -> account.retrieve(-50.0));
    }

    // Tests for interest feature

    @Test
    @DisplayName("getInterest returns 3% when amount is below 500")
    void getInterest_below500_returns3Percent() {
        assertEquals(0.03, account.getInterest(), 1e-9);
    }

    @Test
    @DisplayName("getInterest returns 8% when amount is exactly 500")
    void getInterest_exactly500_returns8Percent() {
        AccountRON acc = new AccountRON("RON003", 500.0);
        assertEquals(0.08, acc.getInterest(), 1e-9);
    }

    @Test
    @DisplayName("getInterest returns 8% when amount is above 500")
    void getInterest_above500_returns8Percent() {
        AccountRON acc = new AccountRON("RON004", 1000.0);
        assertEquals(0.08, acc.getInterest(), 1e-9);
    }

    @Test
    @DisplayName("use updated interest after balance hits 500")
    void getInterest_afterDeposeCrosses500() {
        assertEquals(0.03, account.getInterest(), 1e-9);
        account.depose(300.0);
        assertEquals(0.08, account.getInterest(), 1e-9);
    }

    // Tests for total amount feature

    @Test
    @DisplayName("getTotalAmount = amount + amount * interest (below 500)")
    void getTotalAmount_below500() {
        assertEquals(206.0, account.getTotalAmount(), 1e-9);
    }

    @Test
    @DisplayName("getTotalAmount = amount + amount * interest (above 500)")
    void getTotalAmount_above500() {
        AccountRON acc = new AccountRON("RON005", 1000.0);
        assertEquals(1080.0, acc.getTotalAmount(), 1e-9);
    }

    // Tests for transfer feature

    @Test
    @DisplayName("transferTo deducts from source and adds to destination (RON to RON)")
    void transfer_ronToRon() {
        AccountRON source = new AccountRON("SRC", 300.0);
        AccountRON dest = new AccountRON("DST", 100.0);
        source.transferTo(dest, 50.0);
        assertEquals(250.0, source.getAmount(), 1e-9);
        assertEquals(150.0, dest.getAmount(), 1e-9);
    }

    @Test
    @DisplayName("transferTo to EUR dest: source (RON) loses amount, dest (EUR) gains converted amount")
    void transfer_ronToEur() {
        // source = RON account (200), dest = EUR account (500)
        // transferTo(eurAccount, 100) means withdraw 100 RON from this (source)
        // deposit CurrencyConverter.convert(100 RON to EUR) = 20 EUR into eurAccount
        AccountEUR eurAccount = new AccountEUR("EUR001", 500.0);
        account.transferTo(eurAccount, 100.0);
        assertEquals(100.0, account.getAmount(), 1e-9); // 200 - 100 RON
        assertEquals(520.0, eurAccount.getAmount(), 1e-9); // 500 + 20 EUR
    }

}
