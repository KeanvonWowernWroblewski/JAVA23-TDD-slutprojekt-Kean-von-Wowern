package org.example;

public class User {
    private final String cardId;
    private final String pin;
    private double balance;
    private boolean locked;

    public User(String cardId, String pin, double initialBalance) {
        this.cardId = cardId;
        this.pin = pin;
        this.balance = initialBalance;
        this.locked = false;
    }

    public String getCardId() {
        return cardId;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
