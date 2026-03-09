package ro.uvt.fi.dp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ro.uvt.fi.dp.account.AccountEUR;
import ro.uvt.fi.dp.account.AccountRON;
import ro.uvt.fi.dp.exception.InvalidAmountException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountEUR Tests")
class AccountEURTest {

    private AccountEUR account;

    @BeforeEach
    void setup() {
        account = new AccountEUR("EUR001", 700.0);
    }

    // Tests for depose feature

    @Test
    @DisplayName("depose adds the given amount to the balance")
    void depose_addsAmount() {
        account.depose(200.0);
        assertEquals(900.0, account.getAmount(), 1e-9);
    }

    @Test
    @DisplayName("depose with zero throws InvalidAmountException")
    void depose_zeroAmount_throwsException() {
        assertThrows(InvalidAmountException.class, () -> account.depose(0.0));
    }

    @Test
    @DisplayName("depose with negative amount throws InvalidAmountException")
    void depose_negativeAmount_throwsException() {
        assertThrows(InvalidAmountException.class, () -> account.depose(-100.0));
    }

    @Test
    @DisplayName("depose exception does not modify the balance")
    void depose_exceptionLeavesBalanceUnchanged() {
        assertThrows(InvalidAmountException.class, () -> account.depose(-1.0));
        assertEquals(700.0, account.getAmount(), 1e-9);
    }

    // Tests for retrieve feature

    @Test
    @DisplayName("retrieve subtracts the given amount from the balance")
    void retrieve_subtractsAmount() {
        account.retrieve(200.0);
        assertEquals(500.0, account.getAmount(), 1e-9);
    }

    @Test
    @DisplayName("retrieve entire balance results in zero")
    void retrieve_entireBalance() {
        account.retrieve(700.0);
        assertEquals(0.0, account.getAmount(), 1e-9);
    }

    @Test
    @DisplayName("retrieve more than balance goes negative")
    void retrieve_moreThanBalance_goesNegative() {
        account.retrieve(1000.0);
        assertEquals(-300.0, account.getAmount(), 1e-9);
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

    // Test for interest feature

    @Test
    @DisplayName("getInterest always returns 1% regardless of balance")
    void getInterest_alwaysReturns1Percent() {
        assertEquals(0.01, account.getInterest(), 1e-9);
    }

    // Test for total amount feature

    @Test
    @DisplayName("getTotalAmount = amount + amount * 0.01")
    void getTotalAmount_correctWithInterest() {
        assertEquals(707.0, account.getTotalAmount(), 1e-9);
    }

    // Tests for transfer feature

    @Test
    @DisplayName("transferTo deducts from source and adds to destination (EUR to EUR)")
    void transfer_eurToEur() {
        AccountEUR source = new AccountEUR("SRC", 500.0);
        AccountEUR dest = new AccountEUR("DST", 100.0);
        source.transferTo(dest, 200.0);
        assertEquals(300.0, source.getAmount(), 1e-9);
        assertEquals(300.0, dest.getAmount(), 1e-9);
    }

    @Test
    @DisplayName("transferTo to RON dest: source (EUR) loses amount, dest (RON) gains converted amount")
    void transfer_eurToRon() {
        // source = EUR account (700), dest = RON account (300)
        // transferTo(ronAccount, 100) means withdraw 100 EUR from this (source)
        // deposit CurrencyConverter.convert(100 EUR to RON) = 500 RON into ronAccount
        AccountRON ronAccount = new AccountRON("RON001", 300.0);
        account.transferTo(ronAccount, 100.0);
        assertEquals(600.0, account.getAmount(), 1e-9); // 700 - 100 EUR
        assertEquals(800.0, ronAccount.getAmount(), 1e-9); // 300 + 500 RON
    }

}
