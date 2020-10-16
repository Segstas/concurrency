package exercise95;

public class Main {

    public static void main(String[] args) {
        SavingsAccount savingsAccount1 = new SavingsAccount((int) (Math.random() * 200), "Alise");
        SavingsAccount savingsAccount2 = new SavingsAccount((int) (Math.random() * 200), "Bob");
        SavingsAccount savingsAccountBoss = new SavingsAccount(100000, "Dmitri the Boss");

        createDepositThread(savingsAccount1);
        createWithdrawThread(savingsAccount1);
        createPreferredWithdrawThread(savingsAccount1);
        createTransferThread(1, savingsAccount1, savingsAccount2);
        createTransferThread(1000, savingsAccount1, savingsAccount2);
        createTransferThread(2000, savingsAccount2, savingsAccountBoss);
    }

    public static void createWithdrawThread(SavingsAccount savingsAccount) {
        new Thread(() -> {
            try {
                savingsAccount.withdraw(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void createDepositThread(SavingsAccount savingsAccount) {
        new Thread(() -> {
            savingsAccount.deposit(300);
        }).start();
    }

    public static void createPreferredWithdrawThread(SavingsAccount savingsAccount) {
        new Thread(() -> {
            savingsAccount.preferredWithdraw(400);
        }).start();
    }

    public static void createTransferThread(int money, SavingsAccount savingsAccountFrom, SavingsAccount savingsAccountTo) {
        new Thread(() -> {
            savingsAccountFrom.transfer(money, savingsAccountTo);
        }).start();
    }
}