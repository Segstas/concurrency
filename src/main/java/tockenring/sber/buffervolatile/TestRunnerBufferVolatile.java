package tockenring.sber.buffervolatile;

import tockenring.sber.metrics.TimeChecker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

public class TestRunnerBufferVolatile {

    public static void main(String[] args) throws InterruptedException {
        System.out.println(
                "Test volatile with buffer START"
        );
        for (int i = 2; i <= 8; i++) {
            for (int j = 1; j <= i; j++) {
                runTest(i, j);
            }
        }
        System.out.println(
                "Test volatile with buffer STOP"
        );
    }

    private static void runTest(int nodeCount, int contentCount) {

        System.out.println(
                "START TEST node count = " + nodeCount + ", content count = " + contentCount
        );
        TimeChecker.startTimeChecking();

        TokenRingBufferVolatile tokenRing = new TokenRingBufferVolatile(nodeCount);
        tokenRing.fillContent(contentCount);
        tokenRing.startThreads();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long time = TimeChecker.stopTimeChecking();

        tokenRing.stopThreads();

        long throughput = tokenRing.checkThroughput(time);
        System.out.println("Throughput = " + throughput);
        List<List<Long>> latency = tokenRing.getLatency(nodeCount);

        Comparator comparator = Comparator.comparingLong(Long::longValue);

/*        List<Long> firstLatency = latency.get(0);
        writeLatencyToFile(firstLatency);*/

        for (List<Long> longs : latency) {
            System.out.println("Max latency =" + longs.stream().max(comparator).get().toString() + " nanos");
            OptionalDouble average = longs.stream().mapToLong(num -> num).average();
            if (average.isPresent()) {
                System.out.println("Average latency =" + average.getAsDouble() + " nanos");
            }
        }

        System.out.println(
                "STOP TEST node count = " + nodeCount + ",content count = " + contentCount + "\n" + "\n"
        );
    }

    private static void writeLatencyToFile(List<Long> firstLatency) {
        try (FileWriter writer = new FileWriter("Latency.txt", false)) {
            int j = 0;
            for (Long aLong : firstLatency) {
                j = j + 1;
                writer.append(j + " ");
                writer.append(aLong + " ");
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = list.get(i) / 10000;
        return ret;
    }
}
