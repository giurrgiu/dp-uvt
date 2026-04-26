package ro.uvt.fi.dp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.uvt.fi.dp.account.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Decorator Pattern Tests")
class DecoratorTest {

    private static final double EPSILON = 1e-9;
    private Account eurAccount;
    private Account ronAccount;

    @BeforeEach
    void setUp() {
        eurAccount = new AccountEUR("EUR001", 1000);
        ronAccount = new AccountRON("RON001", 1000);
    }

    @Test
    @DisplayName("NotificationDecorator delegates depose correctly")
    void notificationDecoratorDepose() {
        Account decorated = new NotificationDecorator(eurAccount, "SMS");
        decorated.depose(500);
        assertEquals(1500, decorated.getAmount(), EPSILON);
    }

    @Test
    @DisplayName("NotificationDecorator delegates retrieve correctly")
    void notificationDecoratorRetrieve() {
        Account decorated = new NotificationDecorator(eurAccount, "EMAIL");
        decorated.retrieve(200);
        assertEquals(800, decorated.getAmount(), EPSILON);
    }

    @Test
    @DisplayName("NotificationDecorator delegates transfer correctly")
    void notificationDecoratorTransfer() {
        Account decorated = new NotificationDecorator(eurAccount, "SMS");
        Account dest = new AccountEUR("EUR002", 500);
        decorated.transferTo(dest, 300);
        assertEquals(700, decorated.getAmount(), EPSILON);
        assertEquals(800, dest.getAmount(), EPSILON);
    }

    @Test
    @DisplayName("NotificationDecorator preserves account code and currency")
    void notificationDecoratorPreservesIdentity() {
        Account decorated = new NotificationDecorator(eurAccount, "SMS");
        assertEquals("EUR001", decorated.getAccountCode());
        assertEquals(Currency.EUR, decorated.getCurrency());
    }

    @Test
    @DisplayName("NotificationDecorator returns notification type")
    void notificationDecoratorType() {
        NotificationDecorator decorated = new NotificationDecorator(eurAccount, "SMS");
        assertEquals("SMS", decorated.getNotificationType());
    }

    @Test
    @DisplayName("InterestBonusDecorator adds bonus to EUR interest")
    void interestBonusDecoratorEUR() {
        Account decorated = new InterestBonusDecorator(eurAccount, 0.02);
        assertEquals(0.03, decorated.getInterest(), EPSILON);
    }

    @Test
    @DisplayName("InterestBonusDecorator adds bonus to RON interest")
    void interestBonusDecoratorRON() {
        Account decorated = new InterestBonusDecorator(ronAccount, 0.02);
        assertEquals(0.10, decorated.getInterest(), EPSILON);
    }

    @Test
    @DisplayName("InterestBonusDecorator affects getTotalAmount")
    void interestBonusDecoratorTotalAmount() {
        Account decorated = new InterestBonusDecorator(eurAccount, 0.02);
        double expected = 1000 + 1000 * 0.03;
        assertEquals(expected, decorated.getTotalAmount(), EPSILON);
    }

    @Test
    @DisplayName("InterestBonusDecorator returns bonus rate")
    void interestBonusDecoratorBonusRate() {
        InterestBonusDecorator decorated = new InterestBonusDecorator(eurAccount, 0.05);
        assertEquals(0.05, decorated.getBonusRate(), EPSILON);
    }

    @Test
    @DisplayName("Stacking decorators: notification + interest bonus")
    void stackedDecorators() {
        Account decorated = new NotificationDecorator(
                new InterestBonusDecorator(eurAccount, 0.02), "SMS");
        decorated.depose(500);
        assertEquals(1500, decorated.getAmount(), EPSILON);
        assertEquals(0.03, decorated.getInterest(), EPSILON);
    }

    @Test
    @DisplayName("Stacking decorators: interest bonus + notification")
    void stackedDecoratorsReversed() {
        Account decorated = new InterestBonusDecorator(
                new NotificationDecorator(eurAccount, "EMAIL"), 0.04);
        assertEquals(0.05, decorated.getInterest(), EPSILON);
        decorated.depose(200);
        assertEquals(1200, decorated.getAmount(), EPSILON);
    }

    @Test
    @DisplayName("Decorator delegates getHistory to wrapped account")
    void decoratorDelegatesHistory() {
        Account decorated = new NotificationDecorator(eurAccount, "SMS");
        decorated.depose(100);
        assertEquals(2, decorated.getHistory().size());
    }

    @Test
    @DisplayName("Decorator delegates printStatement without error")
    void decoratorDelegatesPrintStatement() {
        Account decorated = new NotificationDecorator(eurAccount, "SMS");
        assertDoesNotThrow(() -> decorated.printStatement());
    }
}
