package ro.uvt.fi.dp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uvt.fi.dp.account.AccountEUR;
import ro.uvt.fi.dp.account.AccountRON;
import ro.uvt.fi.dp.command.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Command Pattern Tests")
class CommandTest {

    private static final double EPSILON = 1e-9;
    private AccountEUR eurAccount;
    private AccountRON ronAccount;

    @BeforeEach
    void setUp() {
        eurAccount = new AccountEUR("EUR001", 1000);
        ronAccount = new AccountRON("RON001", 5000);
    }

    @Test
    @DisplayName("DeposeCommand execute deposits amount")
    void deposeCommandExecute() {
        AccountCommand cmd = new DeposeCommand(eurAccount, 500);
        cmd.execute();
        assertEquals(1500, eurAccount.getAmount(), EPSILON);
    }

    @Test
    @DisplayName("DeposeCommand undo retrieves amount")
    void deposeCommandUndo() {
        AccountCommand cmd = new DeposeCommand(eurAccount, 500);
        cmd.execute();
        cmd.undo();
        assertEquals(1000, eurAccount.getAmount(), EPSILON);
    }

    @Test
    @DisplayName("RetrieveCommand execute retrieves amount")
    void retrieveCommandExecute() {
        AccountCommand cmd = new RetrieveCommand(eurAccount, 300);
        cmd.execute();
        assertEquals(700, eurAccount.getAmount(), EPSILON);
    }

    @Test
    @DisplayName("RetrieveCommand undo deposits amount back")
    void retrieveCommandUndo() {
        AccountCommand cmd = new RetrieveCommand(eurAccount, 300);
        cmd.execute();
        cmd.undo();
        assertEquals(1000, eurAccount.getAmount(), EPSILON);
    }

    @Test
    @DisplayName("TransferCommand execute transfers between same currency")
    void transferCommandSameCurrency() {
        AccountEUR dest = new AccountEUR("EUR002", 500);
        AccountCommand cmd = new TransferCommand(eurAccount, dest, 200);
        cmd.execute();
        assertEquals(800, eurAccount.getAmount(), EPSILON);
        assertEquals(700, dest.getAmount(), EPSILON);
    }

    @Test
    @DisplayName("TransferCommand undo reverses same-currency transfer")
    void transferCommandUndoSameCurrency() {
        AccountEUR dest = new AccountEUR("EUR002", 500);
        AccountCommand cmd = new TransferCommand(eurAccount, dest, 200);
        cmd.execute();
        cmd.undo();
        assertEquals(1000, eurAccount.getAmount(), EPSILON);
        assertEquals(500, dest.getAmount(), EPSILON);
    }

    @Test
    @DisplayName("TransferCommand execute transfers between different currencies")
    void transferCommandCrossCurrency() {
        AccountCommand cmd = new TransferCommand(eurAccount, ronAccount, 100);
        cmd.execute();
        assertEquals(900, eurAccount.getAmount(), EPSILON);
        assertEquals(5500, ronAccount.getAmount(), EPSILON);
    }

    @Test
    @DisplayName("TransferCommand undo reverses cross-currency transfer")
    void transferCommandUndoCrossCurrency() {
        AccountCommand cmd = new TransferCommand(eurAccount, ronAccount, 100);
        cmd.execute();
        cmd.undo();
        assertEquals(1000, eurAccount.getAmount(), EPSILON);
        assertEquals(5000, ronAccount.getAmount(), EPSILON);
    }

    @Test
    @DisplayName("CommandHistory executes and tracks commands")
    void commandHistoryExecute() {
        CommandHistory history = new CommandHistory();
        history.executeCommand(new DeposeCommand(eurAccount, 500));
        history.executeCommand(new DeposeCommand(eurAccount, 300));
        assertEquals(1800, eurAccount.getAmount(), EPSILON);
        assertEquals(2, history.size());
    }

    @Test
    @DisplayName("CommandHistory undoes in LIFO order")
    void commandHistoryUndoLIFO() {
        CommandHistory history = new CommandHistory();
        history.executeCommand(new DeposeCommand(eurAccount, 500));
        history.executeCommand(new RetrieveCommand(eurAccount, 200));
        assertEquals(1300, eurAccount.getAmount(), EPSILON);

        history.undo();
        assertEquals(1500, eurAccount.getAmount(), EPSILON);

        history.undo();
        assertEquals(1000, eurAccount.getAmount(), EPSILON);
        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("CommandHistory undo on empty throws exception")
    void commandHistoryUndoEmpty() {
        CommandHistory history = new CommandHistory();
        assertThrows(IllegalStateException.class, history::undo);
    }

    @Test
    @DisplayName("CommandHistory isEmpty returns correct state")
    void commandHistoryIsEmpty() {
        CommandHistory history = new CommandHistory();
        assertTrue(history.isEmpty());
        history.executeCommand(new DeposeCommand(eurAccount, 100));
        assertFalse(history.isEmpty());
    }
}
