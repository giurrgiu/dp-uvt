package ro.uvt.fi.dp.gui;

import ro.uvt.fi.dp.account.Account;
import ro.uvt.fi.dp.account.AccountFactory;
import ro.uvt.fi.dp.account.Currency;
import ro.uvt.fi.dp.account.Transaction;
import ro.uvt.fi.dp.bank.Bank;
import ro.uvt.fi.dp.client.Client;
import ro.uvt.fi.dp.command.CommandHistory;
import ro.uvt.fi.dp.command.DeposeCommand;
import ro.uvt.fi.dp.command.RetrieveCommand;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientDashboard extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Bank bank;
    private final Client client;
    private final CommandHistory commandHistory;
    private final Runnable onDataChanged;

    private JList<String> accountList;
    private DefaultListModel<String> accountListModel;
    private JLabel balanceLabel;
    private JLabel currencyLabel;
    private JLabel interestLabel;
    private JLabel totalLabel;
    private DefaultTableModel historyTableModel;
    private JButton undoBtn;

    public ClientDashboard(Bank bank, CommandHistory commandHistory, Runnable onDataChanged) {
        this.bank = bank;
        this.client = bank.getClients().get(0);
        this.commandHistory = commandHistory;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        add(createSidebar(), BorderLayout.WEST);
        add(createDetailPanel(), BorderLayout.CENTER);

        if (!accountListModel.isEmpty()) {
            accountList.setSelectedIndex(0);
        }
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(5, 5));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new TitledBorder("Accounts"));

        JLabel clientLabel = new JLabel("Client: " + client.getName());
        clientLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        sidebar.add(clientLabel, BorderLayout.NORTH);

        accountListModel = new DefaultListModel<>();
        for (Account acc : client.getAccounts()) {
            accountListModel.addElement(acc.getAccountCode() + " (" + acc.getCurrency() + ")");
        }
        accountList = new JList<>(accountListModel);
        accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refreshDetail();
        });
        sidebar.add(new JScrollPane(accountList), BorderLayout.CENTER);

        JButton newAccountBtn = new JButton("+ New Account");
        newAccountBtn.addActionListener(e -> handleNewAccount());
        sidebar.add(newAccountBtn, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createDetailPanel() {
        JPanel detail = new JPanel(new BorderLayout(10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        infoPanel.setBorder(new TitledBorder("Account Details"));
        balanceLabel = new JLabel("Balance: -");
        currencyLabel = new JLabel("Currency: -");
        interestLabel = new JLabel("Interest: -");
        totalLabel = new JLabel("Total (with interest): -");
        infoPanel.add(balanceLabel);
        infoPanel.add(currencyLabel);
        infoPanel.add(interestLabel);
        infoPanel.add(totalLabel);
        detail.add(infoPanel, BorderLayout.NORTH);

        String[] columns = {"Date/Time", "Type", "Amount"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable historyTable = new JTable(historyTableModel);
        JScrollPane tableScroll = new JScrollPane(historyTable);
        tableScroll.setBorder(new TitledBorder("Transaction History"));
        detail.add(tableScroll, BorderLayout.CENTER);

        JPanel opsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        opsPanel.setBorder(new TitledBorder("Operations"));
        JButton deposeBtn = new JButton("Deposit");
        JButton retrieveBtn = new JButton("Withdraw");
        JButton transferBtn = new JButton("Transfer");
        undoBtn = new JButton("Undo");
        JButton auditBtn = new JButton("Audit Report");

        deposeBtn.addActionListener(e -> handleDepose());
        retrieveBtn.addActionListener(e -> handleRetrieve());
        transferBtn.addActionListener(e -> handleTransfer());
        undoBtn.addActionListener(e -> handleUndo());
        auditBtn.addActionListener(e -> handleAudit());

        opsPanel.add(deposeBtn);
        opsPanel.add(retrieveBtn);
        opsPanel.add(transferBtn);
        opsPanel.add(Box.createHorizontalStrut(20));
        opsPanel.add(undoBtn);
        opsPanel.add(auditBtn);
        detail.add(opsPanel, BorderLayout.SOUTH);

        return detail;
    }

    private Account getSelectedAccount() {
        int idx = accountList.getSelectedIndex();
        if (idx < 0) return null;
        List<Account> accounts = client.getAccounts();
        return (idx < accounts.size()) ? accounts.get(idx) : null;
    }

    private void refreshDetail() {
        Account account = getSelectedAccount();
        if (account == null) {
            balanceLabel.setText("Balance: -");
            currencyLabel.setText("Currency: -");
            interestLabel.setText("Interest: -");
            totalLabel.setText("Total (with interest): -");
            historyTableModel.setRowCount(0);
            return;
        }
        balanceLabel.setText(String.format("Balance: %.2f", account.getAmount()));
        currencyLabel.setText("Currency: " + account.getCurrency());
        interestLabel.setText(String.format("Interest: %.1f%%", account.getInterest() * 100));
        totalLabel.setText(String.format("Total (with interest): %.2f", account.getTotalAmount()));

        historyTableModel.setRowCount(0);
        List<Transaction> history = account.getHistory();
        for (int i = history.size() - 1; i >= 0; i--) {
            Transaction t = history.get(i);
            historyTableModel.addRow(new Object[]{
                t.getTimestamp().format(FMT),
                t.getType(),
                String.format("%.2f", t.getAmount())
            });
        }
        undoBtn.setEnabled(!commandHistory.isEmpty());
    }

    private void handleDepose() {
        Account account = getSelectedAccount();
        if (account == null) return;
        String input = JOptionPane.showInputDialog(this, "Enter deposit amount:", "Deposit",
                JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;
        try {
            double amount = Double.parseDouble(input.trim());
            commandHistory.executeCommand(new DeposeCommand(account, amount));
            onDataChanged.run();
            refreshDetail();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRetrieve() {
        Account account = getSelectedAccount();
        if (account == null) return;
        String input = JOptionPane.showInputDialog(this, "Enter withdrawal amount:", "Withdraw",
                JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;
        try {
            double amount = Double.parseDouble(input.trim());
            commandHistory.executeCommand(new RetrieveCommand(account, amount));
            onDataChanged.run();
            refreshDetail();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTransfer() {
        Account source = getSelectedAccount();
        if (source == null) return;
        List<Account> allAccounts = client.getAccounts();
        if (allAccounts.size() < 2) {
            JOptionPane.showMessageDialog(this, "Need at least 2 accounts for a transfer.",
                    "Transfer", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TransferDialog dialog = new TransferDialog(
                SwingUtilities.getWindowAncestor(this), source, allAccounts, commandHistory, () -> {
            onDataChanged.run();
            refreshDetail();
        });
        dialog.setVisible(true);
    }

    private void handleUndo() {
        if (commandHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nothing to undo.", "Undo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Undo the last operation?",
                "Confirm Undo", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            commandHistory.undo();
            onDataChanged.run();
            refreshDetail();
        }
    }

    private void handleAudit() {
        Account account = getSelectedAccount();
        if (account == null) return;
        AuditDialog dialog = new AuditDialog(SwingUtilities.getWindowAncestor(this), account);
        dialog.setVisible(true);
    }

    private void handleNewAccount() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JComboBox<Currency> currencyBox = new JComboBox<>(Currency.values());
        JTextField codeField = new JTextField();
        JTextField amountField = new JTextField();
        panel.add(new JLabel("Currency:"));
        panel.add(currencyBox);
        panel.add(new JLabel("Account Code:"));
        panel.add(codeField);
        panel.add(new JLabel("Initial Amount:"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "New Account",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String code = codeField.getText().trim();
        String amountText = amountField.getText().trim();
        if (code.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) throw new NumberFormatException();
            Currency currency = (Currency) currencyBox.getSelectedItem();
            Account account = AccountFactory.createAccount(currency, code, amount);
            client.addAccount(account);
            accountListModel.addElement(account.getAccountCode() + " (" + account.getCurrency() + ")");
            onDataChanged.run();
            accountList.setSelectedIndex(accountListModel.size() - 1);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a positive number.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
