package org.example;

import java.util.Scanner;

public class ATM {
    private final BankInterface bank;
    private final Scanner scanner;


    public ATM(BankInterface bank, Scanner scanner) {
        this.bank = bank;
        this.scanner = scanner;
    }

    public void run() {
        System.out.println("välkommen till bankomaten");
        System.out.print("ange kort id: ");
        String cardId = scanner.nextLine();

        User user = bank.getUserByCardId(cardId);
        if (user == null) {
            System.out.println("kortet känns inte igen");
            return;
        }

        if (user.isLocked()) {
            System.out.println("kortet är låst");
            return;
        }

        int failedAttempts = bank.getFailedPinAttempts(cardId);
        while (failedAttempts < 3) {
            System.out.print("ange pin kod: ");
            String pin = scanner.nextLine();

            if (bank.validatePin(cardId, pin)) {
                System.out.println("inloggning lyckades");
                showMainMenu(cardId);
                return;
            }

            failedAttempts++;
            bank.setFailedPinAttempts(cardId, failedAttempts);

            if (failedAttempts == 3) {
                bank.lockCard(cardId);
                System.out.println("för många försök, kortet är låst");
                return;
            } else {
                System.out.println("fel pin, försök kvar: " + (3 - failedAttempts));
            }
        }
    }

    private void showMainMenu(String cardId) {
        while (true) {
            System.out.println("\nvälj ett alternativ:");
            System.out.println("1. kontrollera saldo");
            System.out.println("2. sätt in pengar");
            System.out.println("3. ta ut pengar");
            System.out.println("4. visa bankens namn");
            System.out.println("5. avsluta");
            System.out.print("ditt val: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    double balance = bank.getBalance(cardId);
                    System.out.println("ditt saldo är: " + balance + " kr");
                    break;
                case "2":
                    System.out.print("ange belopp att sätta in: ");
                    try {
                        double amount = Double.parseDouble(scanner.nextLine());
                        bank.deposit(cardId, amount);
                        System.out.println(amount + " kr har satts in");
                    } catch (NumberFormatException e) {
                        System.out.println("ogiltigt belopp");
                    }
                    break;
                case "3":
                    System.out.print("ange belopp att ta ut: ");
                    try {
                        double amount = Double.parseDouble(scanner.nextLine());
                        double balanceBefore = bank.getBalance(cardId);
                        if (balanceBefore >= amount) {
                            bank.withdraw(cardId, amount);
                            System.out.println(amount + " kr har tagits ut");
                        } else {
                            System.out.println("otillräckligt saldo");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ogiltigt belopp");
                    }
                    break;
                case "4":
                    String bankName = Bank.getBankName();
                    System.out.println("bank: " + bankName);
                    break;
                case "5":
                    System.out.println("avslutar..");
                    return;
                default:
                    System.out.println("ogiltigt val");
            }
        }
    }
}
