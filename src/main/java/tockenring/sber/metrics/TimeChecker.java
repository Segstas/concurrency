package tockenring.sber.metrics;

public class TimeChecker {

    public static long startTime;

    public static void startTimeChecking() {
        startTime = System.currentTimeMillis();
    }

    public static long stopTimeChecking() {
        long finish = System.currentTimeMillis();
        return (finish - startTime);
    }
}
