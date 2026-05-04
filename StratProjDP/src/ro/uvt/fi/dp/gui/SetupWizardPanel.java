package ro.uvt.fi.dp.gui;

import ro.uvt.fi.dp.account.Account;
import ro.uvt.fi.dp.account.AccountFactory;
import ro.uvt.fi.dp.account.Currency;
import ro.uvt.fi.dp.bank.Bank;
import ro.uvt.fi.dp.client.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SetupWizardPanel extends JPanel {

    private final CardLayout stepLayout;
    private final JPanel stepsPanel;
    private final JLabel stepLabel;
    private final JButton prevButton;
    private final JButton nextButton;
    private int currentStep = 0;

    private JTextField nameField;
    private JTextField addressField;
    private JTextField emailField;
    private final List<AccountEntry> accountEntries = new ArrayList<>();
    private DefaultListModel<String> accountListModel;
    private JTextArea confirmationArea;
    private final Consumer<Bank> onComplete;

    public SetupWizardPanel(Consumer<Bank> onComplete) {
        this.onComplete = onComplete;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        stepLabel = new JLabel("Step 1 of 3: Client Information");
        stepLabel.setFont(stepLabel.getFont().deriveFont(Font.BOLD, 16f));
        add(stepLabel, BorderLayout.NORTH);

        stepLayout = new CardLayout();
        stepsPanel = new JPanel(stepLayout);
        stepsPanel.add(createStep1(), "step1");
        stepsPanel.add(createStep2(), "step2");
        stepsPanel.add(createStep3(), "step3");
        add(stepsPanel, BorderLayout.CENTER);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        prevButton = new JButton("← Back");
        nextButton = new JButton("Next →");
        prevButton.addActionListener(e -> previousStep());
        nextButton.addActionListener(e -> nextStep());
        prevButton.setEnabled(false);
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        add(navPanel, BorderLayout.SOUTH);
    }

    private JPanel createStep1() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Full Name *:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(25);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Address *:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        addressField = new JTextField(25);
        panel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Email (optional):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        emailField = new JTextField(25);
        panel.add(emailField, gbc);

        return panel;
    }

    private JPanel createStep2() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Currency> currencyBox = new JComboBox<>(Currency.values());
        JTextField codeField = new JTextField(10);
        JTextField amountField = new JTextField(8);
        JButton addBtn = new JButton("+ Add Account");

        addPanel.add(new JLabel("Currency:"));
        addPanel.add(currencyBox);
        addPanel.add(new JLabel("Code:"));
        addPanel.add(codeField);
        addPanel.add(new JLabel("Initial Amount:"));
        addPanel.add(amountField);
        addPanel.add(addBtn);
        panel.add(addPanel, BorderLayout.NORTH);

        accountListModel = new DefaultListModel<>();
        JList<String> accountList = new JList<>(accountListModel);
        panel.add(new JScrollPane(accountList), BorderLayout.CENTER);

        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.addActionListener(e -> {
            int idx = accountList.getSelectedIndex();
            if (idx >= 0) {
                accountEntries.remove(idx);
                accountListModel.remove(idx);
            }
        });
        panel.add(removeBtn, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            String code = codeField.getText().trim();
            String amountText = amountField.getText().trim();
            if (code.isEmpty() || amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in code and amount.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double amount = Double.parseDouble(amountText);
                if (amount <= 0) throw new NumberFormatException();
                Currency currency = (Currency) currencyBox.getSelectedItem();
                accountEntries.add(new AccountEntry(currency, code, amount));
                accountListModel.addElement(currency + " | " + code + " | " + String.format("%.2f", amount));
                codeField.setText("");
                amountField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Amount must be a positive number.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createStep3() {
        JPanel panel = new JPanel(new BorderLayout());
        confirmationArea = new JTextArea();
        confirmationArea.setEditable(false);
        confirmationArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        panel.add(new JScrollPane(confirmationArea), BorderLayout.CENTER);
        return panel;
    }

    private void nextStep() {
        if (currentStep == 0 && !validateStep1()) return;
        if (currentStep == 1 && !validateStep2()) return;
        if (currentStep == 2) {
            completeWizard();
            return;
        }
        currentStep++;
        updateStepView();
    }

    private void previousStep() {
        if (currentStep > 0) {
            currentStep--;
            updateStepView();
        }
    }

    private void updateStepView() {
        stepLayout.show(stepsPanel, "step" + (currentStep + 1));
        prevButton.setEnabled(currentStep > 0);
        nextButton.setText(currentStep == 2 ? "Finish" : "Next →");
        String[] titles = {
            "Step 1 of 3: Client Information",
            "Step 2 of 3: Add Accounts",
            "Step 3 of 3: Confirm & Create"
        };
        stepLabel.setText(titles[currentStep]);
        if (currentStep == 2) {
            populateConfirmation();
        }
    }

    private boolean validateStep1() {
        if (nameField.getText().trim().isEmpty() || addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Address are required.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean validateStep2() {
        if (accountEntries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one account.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void populateConfirmation() {
        StringBuilder sb = new StringBuilder();
        sb.append("Client: ").append(nameField.getText().trim()).append("\n");
        sb.append("Address: ").append(addressField.getText().trim()).append("\n");
        String email = emailField.getText().trim();
        if (!email.isEmpty()) sb.append("Email: ").append(email).append("\n");
        sb.append("\nAccounts:\n");
        for (AccountEntry entry : accountEntries) {
            sb.append("  - ").append(entry.currency).append(" | ")
              .append(entry.code).append(" | ")
              .append(String.format("%.2f", entry.amount)).append("\n");
        }
        confirmationArea.setText(sb.toString());
    }

    private void completeWizard() {
        Bank bank = new Bank("MyBank");
        Client.Builder builder = new Client.Builder(
                nameField.getText().trim(), addressField.getText().trim());
        String email = emailField.getText().trim();
        if (!email.isEmpty()) builder.email(email);
        Client client = builder.build();

        for (AccountEntry entry : accountEntries) {
            Account account = AccountFactory.createAccount(entry.currency, entry.code, entry.amount);
            client.addAccount(account);
        }
        bank.addClient(client);
        onComplete.accept(bank);
    }

    private static class AccountEntry {
        final Currency currency;
        final String code;
        final double amount;

        AccountEntry(Currency currency, String code, double amount) {
            this.currency = currency;
            this.code = code;
            this.amount = amount;
        }
    }
}
