package exercise86;

public class FirstBarrier implements Barrier {
    private final TTASLock ttasLock;
    private final int threadNumber;
    private volatile int counter;

    public FirstBarrier(int threadNumber) {
        this.threadNumber = threadNumber;
        this.counter = 0;
        this.ttasLock = new TTASLock();
    }

    public boolean await() {
        ttasLock.lock();
        try {
            counter++;
        } finally {
            ttasLock.unlock();
        }
        do {
        } while (counter < threadNumber);
        return true;
    }
}