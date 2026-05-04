package ro.uvt.fi.dp.gui;

import ro.uvt.fi.dp.account.Account;
import ro.uvt.fi.dp.command.CommandHistory;
import ro.uvt.fi.dp.command.TransferCommand;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TransferDialog extends JDialog {

    private final Account source;
    private final List<Account> allAccounts;
    private final CommandHistory commandHistory;
    private final Runnable onTransferComplete;
    private JComboBox<String> destCombo;
    private JTextField amountField;

    public TransferDialog(Window owner, Account source, List<Account> allAccounts,
                          CommandHistory commandHistory, Runnable onTransferComplete) {
        super(owner, "Transfer Funds", ModalityType.APPLICATION_MODAL);
        this.source = source;
        this.allAccounts = allAccounts;
        this.commandHistory = commandHistory;
        this.onTransferComplete = onTransferComplete;
        initUI();
        setSize(400, 200);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("From:"), gbc);
        gbc.gridx = 1;
        form.add(new JLabel(source.getAccountCode() + " (" + source.getCurrency()
                + ") - " + String.format("%.2f", source.getAmount())), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("To:"), gbc);
        gbc.gridx = 1;
        destCombo = new JComboBox<>();
        for (Account acc : allAccounts) {
            if (!acc.getAccountCode().equals(source.getAccountCode())) {
                destCombo.addItem(acc.getAccountCode() + " (" + acc.getCurrency() + ")");
            }
        }
        form.add(destCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        amountField = new JTextField(12);
        form.add(amountField, gbc);

        add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton transferBtn = new JButton("Transfer");
        JButton cancelBtn = new JButton("Cancel");
        transferBtn.addActionListener(e -> doTransfer());
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(transferBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void doTransfer() {
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter an amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double amount = Double.parseDouble(amountText);
            int destIdx = destCombo.getSelectedIndex();
            Account dest = null;
            int count = 0;
            for (Account acc : allAccounts) {
                if (!acc.getAccountCode().equals(source.getAccountCode())) {
                    if (count == destIdx) {
                        dest = acc;
                        break;
                    }
                    count++;
                }
            }
            if (dest == null) return;

            commandHistory.executeCommand(new TransferCommand(source, dest, amount));
            onTransferComplete.run();
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
