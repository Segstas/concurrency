package exercise93;

import java.util.concurrent.locks.ReadWriteLock;

public class Main {

    public static void main(String[] args) {

        final Integer[] testValue = {0};
        ReadWriteLock simpleReadWriteLock = new SimpleReadWriteLock();
        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                simpleReadWriteLock.writeLock().lock();
                try {
                    testValue[0] = testValue[0] + 1;
                    System.out.println("Writer wrote the value" + " " + testValue[0]);
                } finally {
                    simpleReadWriteLock.writeLock().unlock();
                }
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                simpleReadWriteLock.readLock().lock();
                try {
                    System.out.println("1st read the value" + testValue[0]);
                } finally {
                    simpleReadWriteLock.readLock().unlock();
                }
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                simpleReadWriteLock.readLock().lock();
                try {
                    System.out.println("2nd read the value" + testValue[0]);
                } finally {
                    simpleReadWriteLock.readLock().unlock();
                }
            }
        }).start();
    }
}