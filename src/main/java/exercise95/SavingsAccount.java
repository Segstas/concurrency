package exercise95;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SavingsAccount implements ISavingsAccount {
    private String name;
    private int balance;
    private int preferredWithdrawAwaitingMoney = 0;

    private final ReentrantLock basicLock = new ReentrantLock();
    private final Condition condition = basicLock.newCondition();

    SavingsAccount(int balance) {
        this.balance = balance;
    }

    SavingsAccount(int balance, String name) {
        this.name = name;
        this.balance = balance;
    }

    int getBalance() {
        return this.balance;
    }

    void deposit(int moneyAmount) {
        try {
            basicLock.lock();
            this.balance += moneyAmount;
            System.out.println(name +  " +" + moneyAmount + " to balance, balance = " + balance + "\n");
            condition.signalAll();
        } finally {
            basicLock.unlock();
        }
    }

    void withdraw(int moneyAmount) throws InterruptedException {
        try {
            basicLock.lock();
               while(this.balance < moneyAmount || this.preferredWithdrawAwaitingMoney > 0) {
                   condition.await();
            }
            this.balance -= moneyAmount;
            System.out.println(name + " -" + moneyAmount + " to balance, balance = " + balance + "\n");
        } finally {
            basicLock.unlock();
        }
    }

    void preferredWithdraw(int moneyAmount) {
        try {
            basicLock.lock();
            synchronized(this) {
                this.preferredWithdrawAwaitingMoney++;
            }
            while(this.balance < moneyAmount) {
                condition.await();
            }
            this.balance -= moneyAmount;
            System.out.println(name + " preferred -" + moneyAmount + " to balance, balance = " + balance + "\n");
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            synchronized(this) {
                this.preferredWithdrawAwaitingMoney--;
            }
            basicLock.unlock();
        }
    }

    void transfer(int k, SavingsAccount reserve) {
        basicLock.lock();
        try {
            System.out.println(name + " start transfer, " + name + " balance = " + balance + "\n");
            reserve.withdraw(k);
            deposit(k);
            System.out.println(name + " end transfer, " + name + " balance = " + balance + "\n");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {basicLock.unlock();}

    }
}