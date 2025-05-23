package org.example;

public interface BankInterface {


    User getUserByCardId(String cardId);


    boolean validatePin(String cardId, String pin);


    void setFailedPinAttempts(String cardId, int attempts);

    int getFailedPinAttempts(String cardId);

    void lockCard(String cardId);

    double getBalance(String cardId);


    void deposit(String cardId, double amount);

    void withdraw(String cardId, double amount);
}

