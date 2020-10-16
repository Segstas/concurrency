package exercise86;

import java.util.concurrent.atomic.AtomicInteger;

/*
    Реализация c помощью встроенных примитивов синхронизации языка Java
*/
public class SecondBarrierSync implements Barrier {
    private int threadNumber;
    private volatile int[] corridor;
    private AtomicInteger number;

    public SecondBarrierSync(int threadNumber) {
        this.threadNumber = threadNumber;
        this.corridor = new int[threadNumber];
        this.number = new AtomicInteger(0);
    }

    public boolean await() {
        int currentNumber = number.getAndIncrement();
        if (currentNumber == threadNumber - 1) {
            corridor[currentNumber] = 2;
            synchronized (this) {
                this.notifyAll();
            }
            return true;
        } else {
            corridor[currentNumber] = 1;
            do {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while ((corridor[currentNumber + 1] != 2));
        }
        corridor[currentNumber] = 2;
        synchronized (this) {
            this.notifyAll();
        }
        return true;
    }
}