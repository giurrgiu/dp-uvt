package ro.uvt.fi.dp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uvt.fi.dp.account.AccountEUR;
import ro.uvt.fi.dp.account.AccountRON;
import ro.uvt.fi.dp.visitor.AccountVisitor;
import ro.uvt.fi.dp.visitor.AuditVisitor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Visitor Pattern Tests")
class VisitorTest {

    private AccountEUR eurAccount;
    private AccountRON ronAccount;
    private AccountVisitor auditor;

    @BeforeEach
    void setUp() {
        eurAccount = new AccountEUR("EUR001", 1000);
        ronAccount = new AccountRON("RON001", 600);
        auditor = new AuditVisitor();
    }

    @Test
    @DisplayName("AuditVisitor produces report for EUR account")
    void auditEurAccount() {
        String report = eurAccount.accept(auditor);
        assertNotNull(report);
        assertTrue(report.contains("EUR Account Audit"));
    }

    @Test
    @DisplayName("AuditVisitor produces report for RON account")
    void auditRonAccount() {
        String report = ronAccount.accept(auditor);
        assertNotNull(report);
        assertTrue(report.contains("RON Account Audit"));
    }

    @Test
    @DisplayName("EUR audit report contains account code")
    void eurReportContainsAccountCode() {
        String report = eurAccount.accept(auditor);
        assertTrue(report.contains("EUR001"));
    }

    @Test
    @DisplayName("RON audit report contains account code")
    void ronReportContainsAccountCode() {
        String report = ronAccount.accept(auditor);
        assertTrue(report.contains("RON001"));
    }

    @Test
    @DisplayName("EUR audit report contains balance")
    void eurReportContainsBalance() {
        String report = eurAccount.accept(auditor);
        assertTrue(report.contains("Balance:"));
        assertTrue(report.contains("1000"));
    }

    @Test
    @DisplayName("RON audit report contains balance")
    void ronReportContainsBalance() {
        String report = ronAccount.accept(auditor);
        assertTrue(report.contains("Balance:"));
        assertTrue(report.contains("600"));
    }

    @Test
    @DisplayName("EUR audit contains EU compliance information")
    void eurReportContainsEUCompliance() {
        String report = eurAccount.accept(auditor);
        assertTrue(report.contains("European Central Bank"));
    }

    @Test
    @DisplayName("RON audit contains local compliance information")
    void ronReportContainsLocalCompliance() {
        String report = ronAccount.accept(auditor);
        assertTrue(report.contains("National Bank of Romania"));
    }

    @Test
    @DisplayName("EUR audit report contains interest rate")
    void eurReportContainsInterestRate() {
        String report = eurAccount.accept(auditor);
        assertTrue(report.contains("1.0%"));
    }

    @Test
    @DisplayName("RON audit report contains interest rate for high balance")
    void ronReportContainsHighInterestRate() {
        String report = ronAccount.accept(auditor);
        assertTrue(report.contains("8.0%"));
    }

    @Test
    @DisplayName("Audit report reflects transaction count")
    void auditReportReflectsTransactions() {
        eurAccount.depose(500);
        eurAccount.retrieve(200);
        String report = eurAccount.accept(auditor);
        assertTrue(report.contains("Transactions: 3"));
    }

    @Test
    @DisplayName("Different currencies produce different audit content")
    void differentCurrenciesDifferentContent() {
        String eurReport = eurAccount.accept(auditor);
        String ronReport = ronAccount.accept(auditor);
        assertNotEquals(eurReport, ronReport);
        assertTrue(eurReport.contains("European Central Bank"));
        assertTrue(ronReport.contains("National Bank of Romania"));
    }
}
