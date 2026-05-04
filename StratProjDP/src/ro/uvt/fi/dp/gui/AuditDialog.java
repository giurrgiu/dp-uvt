package ro.uvt.fi.dp.gui;

import ro.uvt.fi.dp.account.Account;
import ro.uvt.fi.dp.visitor.AuditVisitor;

import javax.swing.*;
import java.awt.*;

public class AuditDialog extends JDialog {

    public AuditDialog(Window owner, Account account) {
        super(owner, "Audit Report - " + account.getAccountCode(), ModalityType.APPLICATION_MODAL);

        AuditVisitor visitor = new AuditVisitor();
        String report = account.accept(visitor);

        JTextArea textArea = new JTextArea(report);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(closeBtn);

        setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        setSize(450, 350);
        setLocationRelativeTo(owner);
    }
}
