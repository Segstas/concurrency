package exercise86;

import java.util.concurrent.atomic.AtomicInteger;

public class SecondBarrier implements Barrier {
    private int threadNumber;
    private volatile int[] corridor;
    private AtomicInteger number;

    public SecondBarrier(int threadNumber) {
        this.threadNumber = threadNumber;
        this.corridor = new int[threadNumber];
        this.number = new AtomicInteger(0);
    }

    public boolean await() {
        int currentNumber = number.getAndIncrement();
        if (currentNumber == threadNumber - 1) {
            corridor[currentNumber] = 2;
            return true;
        }
        corridor[currentNumber] = 1;
        do {
        } while ((corridor[currentNumber + 1] != 2));

        corridor[currentNumber] = 2;
        return true;
    }
}