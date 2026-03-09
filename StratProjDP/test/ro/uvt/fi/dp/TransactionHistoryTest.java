package ro.uvt.fi.dp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ro.uvt.fi.dp.account.AccountEUR;
import ro.uvt.fi.dp.account.AccountRON;
import ro.uvt.fi.dp.account.Transaction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Transaction History Tests")
class TransactionHistoryTest {

    private AccountRON ronAccount;
    private AccountEUR eurAccount;

    @BeforeEach
    void setup() {
        ronAccount = new AccountRON("RON001", 200.0);
        eurAccount = new AccountEUR("EUR001", 100.0);
    }

    // Tests for initial transaction history state (after account creation)

    @Test
    @DisplayName("new account has exactly one DEPOSE transaction (the initial deposit)")
    void history_newAccount_hasOneDepose() {
        List<Transaction> history = ronAccount.getHistory();
        assertEquals(1, history.size());
        assertEquals(Transaction.Type.DEPOSE, history.get(0).getType());
    }

    @Test
    @DisplayName("initial DEPOSE transaction records the correct amount")
    void history_initialDepose_correctAmount() {
        assertEquals(200.0, ronAccount.getHistory().get(0).getAmount(), 1e-9);
    }

    // Depose history

    @Test
    @DisplayName("depose adds a DEPOSE transaction to history")
    void history_depose_addsDeposeRecord() {
        ronAccount.depose(50.0);
        List<Transaction> history = ronAccount.getHistory();
        assertEquals(2, history.size());
        assertEquals(Transaction.Type.DEPOSE, history.get(1).getType());
        assertEquals(50.0, history.get(1).getAmount(), 1e-9);
    }

    @Test
    @DisplayName("multiple deposes produce multiple DEPOSE records in order")
    void history_multipleDeposes_inOrder() {
        ronAccount.depose(10.0);
        ronAccount.depose(20.0);
        List<Transaction> history = ronAccount.getHistory();
        assertEquals(3, history.size());
        assertEquals(10.0, history.get(1).getAmount(), 1e-9);
        assertEquals(20.0, history.get(2).getAmount(), 1e-9);
    }

    // Retrieve history
    @Test
    @DisplayName("retrieve adds a RETRIEVE transaction to history")
    void history_retrieve_addsRetrieveRecord() {
        ronAccount.retrieve(30.0);
        List<Transaction> history = ronAccount.getHistory();
        assertEquals(2, history.size());
        assertEquals(Transaction.Type.RETRIEVE, history.get(1).getType());
        assertEquals(30.0, history.get(1).getAmount(), 1e-9);
    }

    // Tests for transfer (same currency)

    @Test
    @DisplayName("same currency transfer: source gets TRANSFER_OUT, dest gets TRANSFER_IN")
    void history_transfer_sameCurrency_correctTypes() {
        AccountRON source = new AccountRON("SRC", 300.0);
        AccountRON dest = new AccountRON("DST", 100.0);

        source.transferTo(dest, 50.0);

        // source: 1 initial DEPOSE + 1 TRANSFER_OUT
        List<Transaction> srcHistory = source.getHistory();
        assertEquals(2, srcHistory.size());
        assertEquals(Transaction.Type.TRANSFER_OUT, srcHistory.get(1).getType());
        assertEquals(50.0, srcHistory.get(1).getAmount(), 1e-9);

        // dest: 1 initial DEPOSE + 1 TRANSFER_IN
        List<Transaction> dstHistory = dest.getHistory();
        assertEquals(2, dstHistory.size());
        assertEquals(Transaction.Type.TRANSFER_IN, dstHistory.get(1).getType());
        assertEquals(50.0, dstHistory.get(1).getAmount(), 1e-9);
    }

    // Tests for transfer with currency conversion

    @Test
    @DisplayName("cross currency transferTo RON to EUR: source (RON) gets TRANSFER_OUT in RON, dest (EUR) gets TRANSFER_IN in EUR")
    void history_transfer_crossCurrency_ronToEur() {
        // source = RON(200), dest = EUR(100)
        // transferTo(eurAccount, 10 RON): withdraw 10 RON from source
        // deposit convert(10 RON to EUR) = 2 EUR into dest
        ronAccount.transferTo(eurAccount, 10.0);

        List<Transaction> srcHistory = ronAccount.getHistory();
        assertEquals(2, srcHistory.size());
        assertEquals(Transaction.Type.TRANSFER_OUT, srcHistory.get(1).getType());
        assertEquals(10.0, srcHistory.get(1).getAmount(), 1e-9); // 10 RON withdrawn

        List<Transaction> dstHistory = eurAccount.getHistory();
        assertEquals(2, dstHistory.size());
        assertEquals(Transaction.Type.TRANSFER_IN, dstHistory.get(1).getType());
        assertEquals(2.0, dstHistory.get(1).getAmount(), 1e-9); // 10 RON * 0.2 = 2 EUR deposited
    }

    @Test
    @DisplayName("cross currency transferTo EUR to RON: source (EUR) gets TRANSFER_OUT in EUR, dest (RON) gets TRANSFER_IN in RON")
    void history_transfer_crossCurrency_eurToRon() {
        // source = EUR(100), dest = RON(200)
        // transferTo(ronAccount, 50 EUR): withdraw 50 EUR from source
        // deposit convert(50 EUR to RON) = 250 RON into dest
        eurAccount.transferTo(ronAccount, 50.0);

        List<Transaction> srcHistory = eurAccount.getHistory();
        assertEquals(2, srcHistory.size());
        assertEquals(Transaction.Type.TRANSFER_OUT, srcHistory.get(1).getType());
        assertEquals(50.0, srcHistory.get(1).getAmount(), 1e-9); // 50 EUR withdrawn

        List<Transaction> dstHistory = ronAccount.getHistory();
        assertEquals(2, dstHistory.size());
        assertEquals(Transaction.Type.TRANSFER_IN, dstHistory.get(1).getType());
        assertEquals(250.0, dstHistory.get(1).getAmount(), 1e-9); // 50 EUR * 5 = 250 RON deposited
    }

    // Test for getHistory immutability

    @Test
    @DisplayName("getHistory returns an unmodifiable list, adding to it throws UnsupportedOperationException")
    void history_getHistory_isUnmodifiable() {
        List<Transaction> history = ronAccount.getHistory();
        assertThrows(UnsupportedOperationException.class,
                () -> history.add(new Transaction(Transaction.Type.DEPOSE, 1.0)));
    }

    // Test for transaction timestamp

    @Test
    @DisplayName("each transaction has a non-null timestamp")
    void history_transaction_hasTimestamp() {
        ronAccount.depose(10.0);
        ronAccount.retrieve(5.0);
        for (Transaction t : ronAccount.getHistory()) {
            assertNotNull(t.getTimestamp(), "timestamp must not be null for transaction: " + t.getType());
        }
    }
}
