package org.example;

import java.util.HashMap;
import java.util.Map;

public class Bank implements BankInterface {

    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Integer> failedAttempts = new HashMap<>();

    @Override
    public User getUserByCardId(String cardId) {
        return users.get(cardId);
    }

    @Override
    public boolean validatePin(String cardId, String pin) {
        User user = users.get(cardId);
        return user != null && user.getPin().equals(pin);
    }

    @Override
    public void setFailedPinAttempts(String cardId, int attempts) {
        failedAttempts.put(cardId, attempts);
    }

    @Override
    public int getFailedPinAttempts(String cardId) {
        return failedAttempts.getOrDefault(cardId, 0);
    }

    @Override
    public void lockCard(String cardId) {
        User user = users.get(cardId);
        if (user != null) {
            user.setLocked(true);
        }
    }

    @Override
    public double getBalance(String cardId) {
        User user = users.get(cardId);
        return user != null ? user.getBalance() : 0.0;
    }

    @Override
    public void deposit(String cardId, double amount) {
        User user = users.get(cardId);
        if (user != null && amount > 0) {
            user.setBalance(user.getBalance() + amount);
        }
    }

    @Override
    public void withdraw(String cardId, double amount) {
        User user = users.get(cardId);
        if (user != null && amount > 0 && user.getBalance() >= amount) {
            user.setBalance(user.getBalance() - amount);
        }
    }

    public static String getBankName() {
        return "fejk bankomat";
    }
}

