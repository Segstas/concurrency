package exercise93;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class SimpleReadWriteLock implements ReadWriteLock {
    private boolean writer;
    private int readers;
    private Lock readLock;
    private Lock writeLock;
    private final Object condition = new Object();
    private final Object lock = new Object();

    public Lock readLock() {
        return readLock;
    }

    public Lock writeLock() {
        return writeLock;
    }

    public SimpleReadWriteLock() {
        this.writer = false;
        this.readers = 0;
        this.readLock = new ReadLock();
        this.writeLock = new WriteLock();
    }

    private class WriteLock implements Lock {

        @Override
        public void lock() {
            synchronized (lock) {
                while (writer || readers > 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                writer = true;
            }
        }

        @Override
        public void unlock() {
            synchronized (lock) {
                writer = false;
                lock.notifyAll();
            }
        }

        public void lockInterruptibly() {
            // not implemented
        }

        public boolean tryLock() {
            // not implemented
            return false;
        }

        public boolean tryLock(long time, TimeUnit unit) {
            // not implemented
            return false;
        }

        public Condition newCondition() {
            // not implemented
            return null;
        }
    }

    private class ReadLock implements Lock {

        @Override
        public void lock() {
            {
                synchronized (lock) {
                    while (writer) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    readers++;
                }
            }
        }

        @Override
        public void unlock() {
            synchronized (lock) {
                readers--;
                if (readers == 0)
                    lock.notifyAll();
            }
        }

        public void lockInterruptibly() {
            // not implemented
        }

        public boolean tryLock() {
            // not implemented
            return false;
        }

        public boolean tryLock(long time, TimeUnit unit) {
            // not implemented
            return false;
        }

        public Condition newCondition() {
            // not implemented
            return null;
        }
    }
}