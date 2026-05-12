package bankaccountmanager;

import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private List<Account> accounts = new ArrayList<>();
    private int accCounter = 1001;

    public AccountManager() {
        // Add some demo accounts
        Account a1 = new Account("ACC1001", "Rahul Sharma", "Savings", 15000.00);
        a1.deposit(5000);
        a1.withdraw(2000);
        accounts.add(a1);

        Account a2 = new Account("ACC1002", "Priya Patel", "Current", 50000.00);
        a2.deposit(10000);
        accounts.add(a2);

        accCounter = 1003;
    }

    public String generateAccountNumber() {
        return "ACC" + (accCounter++);
    }

    public Account createAccount(String name, String type, double initialBalance) {
        Account acc = new Account(generateAccountNumber(), name, type, initialBalance);
        accounts.add(acc);
        return acc;
    }

    public boolean deleteAccount(String accNumber) {
        return accounts.removeIf(a -> a.getAccountNumber().equals(accNumber));
    }

    public Account findAccount(String accNumber) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber().equalsIgnoreCase(accNumber))
                .findFirst().orElse(null);
    }

    public List<Account> getAllAccounts() { return accounts; }

    public List<Account> searchByName(String name) {
        List<Account> result = new ArrayList<>();
        for (Account a : accounts) {
            if (a.getHolderName().toLowerCase().contains(name.toLowerCase())) {
                result.add(a);
            }
        }
        return result;
    }

    public double getTotalDeposits() {
        return accounts.stream().mapToDouble(Account::getBalance).sum();
    }
}
