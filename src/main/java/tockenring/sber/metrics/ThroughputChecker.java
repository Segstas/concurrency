package tockenring.sber.metrics;

import java.util.concurrent.atomic.AtomicInteger;

public class ThroughputChecker {

    protected AtomicInteger count = new AtomicInteger(0);

    protected void countIncrement() {
        this.count.incrementAndGet();
    }

    public long checkThroughput(long time) {
        return (count.get() / time);
    }
}
