package exercise86;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TTASLock implements Lock {
    AtomicBoolean isLocked;

    public TTASLock() {
        isLocked = new AtomicBoolean(false);
    }

    @Override
    public void lock() {
        while (true) {
            while (isLocked.get()) {
                Thread.yield();
            }
            if (!isLocked.getAndSet(true)) {
                return;
            }
        }
    }

    @Override
    public void unlock() {
        isLocked.set(false);
    }

    @Override
    public void lockInterruptibly() {
        // not implemented
    }

    @Override
    public boolean tryLock() {
        // not implemented
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        // not implemented
        return false;
    }

    @Override
    public Condition newCondition() {
        // not implemented
        return null;
    }
}