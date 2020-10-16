package exercise86;

/*
    Реализация c помощью встроенных примитивов синхронизации языка Java
*/
public class FirstBarrierSync implements Barrier {
    private final TTASLock ttasLock;
    private final int threadNumber;
    private int counter;

    public FirstBarrierSync(int threadNumber) {
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
        if (counter >= threadNumber) {
            synchronized (FirstBarrierSync.class) {
                FirstBarrierSync.class.notifyAll();
            }
        } else {
            try {
                synchronized (FirstBarrierSync.class) {
                    FirstBarrierSync.class.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}