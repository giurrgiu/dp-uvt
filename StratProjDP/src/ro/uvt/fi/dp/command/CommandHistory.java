package ro.uvt.fi.dp.command;

import java.util.Stack;

public class CommandHistory {

    private final Stack<AccountCommand> history = new Stack<>();

    public void executeCommand(AccountCommand command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (history.isEmpty()) {
            throw new IllegalStateException("No commands to undo");
        }
        AccountCommand command = history.pop();
        command.undo();
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }

    public int size() {
        return history.size();
    }
}
