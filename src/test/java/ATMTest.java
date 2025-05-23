import org.example.ATM;
import org.example.Bank;
import org.example.BankInterface;
import org.example.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Scanner;

import static org.mockito.Mockito.*;

public class ATMTest {

    private BankInterface bankMock;

    @BeforeEach
    void setUp() {
        bankMock = mock(BankInterface.class);
    }

    @Test
    @DisplayName("kort känns inte igen, verifierar att bank anropas")
    void testUnknownCard() {
        Scanner scanner = new Scanner("0000\n");
        ATM atm = new ATM(bankMock, scanner);

        when(bankMock.getUserByCardId("0000")).thenReturn(null);

        atm.run();

        verify(bankMock).getUserByCardId("0000");
    }

    @Test
    @DisplayName("kort är låst, verifierar att användaren nekas")
    void testCardIsLocked() {
        Scanner scanner = new Scanner("1111\n");
        ATM atm = new ATM(bankMock, scanner);

        User user = new User("1111", "0000", 0);
        user.setLocked(true);
        when(bankMock.getUserByCardId("1111")).thenReturn(user);

        atm.run();

        verify(bankMock).getUserByCardId("1111");
    }

    @Test
    @DisplayName("kort låses efter tre felaktiga pin försök")
    void testCardLockedAfterThreeFailedPins() {
        Scanner scanner = new Scanner("2222\n0000\n0000\n0000\n");
        ATM atm = new ATM(bankMock, scanner);

        User user = new User("2222", "9999", 500);
        when(bankMock.getUserByCardId("2222")).thenReturn(user);
        when(bankMock.getFailedPinAttempts("2222")).thenReturn(0, 1, 2);
        when(bankMock.validatePin(anyString(), anyString())).thenReturn(false);

        atm.run();

        verify(bankMock, times(3)).setFailedPinAttempts(eq("2222"), anyInt());
        verify(bankMock).lockCard("2222");
    }

    @Test
    @DisplayName("statisk metod Bank.getBankName mockas korrekt")
    void testStaticBankNameMocked() {
        Scanner scanner = new Scanner("3333\n1234\n4\n5\n");
        ATM atm = new ATM(bankMock, scanner);

        User user = new User("3333", "1234", 1000);
        when(bankMock.getUserByCardId("3333")).thenReturn(user);
        when(bankMock.getFailedPinAttempts("3333")).thenReturn(0);
        when(bankMock.validatePin("3333", "1234")).thenReturn(true);
        when(bankMock.getBalance("3333")).thenReturn(1000.0);

        try (MockedStatic<Bank> staticMock = Mockito.mockStatic(Bank.class)) {
            staticMock.when(Bank::getBankName).thenReturn("TestBank");

            atm.run();

            staticMock.verify(Bank::getBankName);
        }
    }

    @Test
    @DisplayName("användaren loggar in och kontrollerar saldo")
    void testCheckBalance() {
        Scanner scanner = new Scanner("9999\n1234\n1\n5\n");
        ATM atm = new ATM(bankMock, scanner);

        User user = new User("9999", "1234", 100);
        when(bankMock.getUserByCardId("9999")).thenReturn(user);
        when(bankMock.getFailedPinAttempts("9999")).thenReturn(0);
        when(bankMock.validatePin("9999", "1234")).thenReturn(true);
        when(bankMock.getBalance("9999")).thenReturn(100.0);

        atm.run();

        verify(bankMock).getBalance("9999");
    }

    @Test
    @DisplayName("användaren loggar in och sätter in pengar")
    void testDeposit() {
        Scanner scanner = new Scanner("8888\n1111\n2\n500\n5\n");
        ATM atm = new ATM(bankMock, scanner);

        User user = new User("8888", "1111", 0);
        when(bankMock.getUserByCardId("8888")).thenReturn(user);
        when(bankMock.getFailedPinAttempts("8888")).thenReturn(0);
        when(bankMock.validatePin("8888", "1111")).thenReturn(true);

        doAnswer(invocation -> {
            double amount = invocation.getArgument(1);
            user.setBalance(user.getBalance() + amount);
            return null;
        }).when(bankMock).deposit(eq("8888"), anyDouble());

        atm.run();

        verify(bankMock).deposit("8888", 500.0);
        assertEquals(500.0, user.getBalance(), 0.001);
    }

    @Test
    @DisplayName("användaren tar ut pengar med tillräckligt saldo")
    void testWithdrawSuccess() {
        Scanner scanner = new Scanner("7777\n1234\n3\n100\n5\n");
        ATM atm = new ATM(bankMock, scanner);

        User user = new User("7777", "1234", 1000);
        when(bankMock.getUserByCardId("7777")).thenReturn(user);
        when(bankMock.getFailedPinAttempts("7777")).thenReturn(0);
        when(bankMock.validatePin("7777", "1234")).thenReturn(true);
        when(bankMock.getBalance("7777")).thenReturn(1000.0);

        doAnswer(invocation -> {
            double amount = invocation.getArgument(1);
            user.setBalance(user.getBalance() - amount);
            return null;
        }).when(bankMock).withdraw(eq("7777"), anyDouble());

        atm.run();

        verify(bankMock).withdraw("7777", 100.0);
        assertEquals(900.0, user.getBalance(), 0.001);
    }
}
