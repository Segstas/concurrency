package tockenring.sber.metrics;

public class ThroughputChecker {

    protected Integer count = 0;

    protected void countIncrement() {
        this.count++;
    }

    public void setCountToZero(){
        count = 0;
    }

    public long checkThroughput(long time) {
        return (long) ((Double.valueOf(count) / time) * 1000); ////   p/sec
    }
}
