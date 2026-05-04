package ro.uvt.fi.dp.gui;

import ro.uvt.fi.dp.bank.Bank;
import ro.uvt.fi.dp.command.CommandHistory;
import ro.uvt.fi.dp.persistence.BankDataStore;

import javax.swing.*;
import java.awt.*;

public class BankApp extends JFrame {

    private static final String WIZARD_CARD = "wizard";
    private static final String DASHBOARD_CARD = "dashboard";

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final BankDataStore dataStore;
    private final CommandHistory commandHistory;
    private Bank bank;

    public BankApp() {
        super("Banking Application");
        this.dataStore = new BankDataStore();
        this.commandHistory = new CommandHistory();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        add(mainPanel);

        bank = dataStore.load();
        if (bank != null && !bank.getClients().isEmpty()) {
            showDashboard();
        } else {
            showWizard();
        }
    }

    private void showWizard() {
        SetupWizardPanel wizard = new SetupWizardPanel(this::onWizardComplete);
        mainPanel.add(wizard, WIZARD_CARD);
        cardLayout.show(mainPanel, WIZARD_CARD);
    }

    private void showDashboard() {
        for (Component c : mainPanel.getComponents()) {
            if (DASHBOARD_CARD.equals(c.getName())) {
                mainPanel.remove(c);
            }
        }
        ClientDashboard dashboard = new ClientDashboard(bank, commandHistory, this::saveData);
        dashboard.setName(DASHBOARD_CARD);
        mainPanel.add(dashboard, DASHBOARD_CARD);
        cardLayout.show(mainPanel, DASHBOARD_CARD);
    }

    private void onWizardComplete(Bank newBank) {
        this.bank = newBank;
        saveData();
        showDashboard();
    }

    private void saveData() {
        dataStore.save(bank);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new BankApp().setVisible(true);
        });
    }
}
