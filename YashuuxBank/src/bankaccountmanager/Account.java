package bankaccountmanager;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String accountNumber;
    private String holderName;
    private String accountType;
    private double balance;
    private List<String> transactions;

    public Account(String accountNumber, String holderName, String accountType, double initialBalance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.accountType = accountType;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        transactions.add("Account opened with initial deposit: ₹" + String.format("%.2f", initialBalance));
    }

    public void deposit(double amount) {
        balance += amount;
        transactions.add("Deposited: ₹" + String.format("%.2f", amount) + "  |  Balance: ₹" + String.format("%.2f", balance));
    }

    public boolean withdraw(double amount) {
        if (amount > balance) return false;
        balance -= amount;
        transactions.add("Withdrawn: ₹" + String.format("%.2f", amount) + "  |  Balance: ₹" + String.format("%.2f", balance));
        return true;
    }

    public boolean transfer(Account target, double amount) {
        if (amount > balance) return false;
        balance -= amount;
        target.balance += amount;
        transactions.add("Transferred: ₹" + String.format("%.2f", amount) + " to Acc# " + target.accountNumber + "  |  Balance: ₹" + String.format("%.2f", balance));
        target.transactions.add("Received: ₹" + String.format("%.2f", amount) + " from Acc# " + accountNumber + "  |  Balance: ₹" + String.format("%.2f", target.balance));
        return true;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getHolderName()    { return holderName; }
    public String getAccountType()   { return accountType; }
    public double getBalance()       { return balance; }
    public List<String> getTransactions() { return transactions; }

    public void setHolderName(String holderName) { this.holderName = holderName; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    @Override
    public String toString() {
        return accountNumber + " | " + holderName + " | " + accountType + " | ₹" + String.format("%.2f", balance);
    }
}
