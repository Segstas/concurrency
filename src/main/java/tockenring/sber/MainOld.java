/*
package tockenring.sber;

import tockenring.sber.dummyvolatile.TokenRingDummyVolatile;
import tockenring.sber.metrics.TimeChecker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

public class MainOld {

    public static void main(String[] args) throws InterruptedException {

        TimeChecker.startTimeChecking();

        TokenRingDummyVolatile tokenRing = new TokenRingDummyVolatile(6);
        tokenRing.fillContent(3);
        tokenRing.startThreads();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long time = TimeChecker.stopTimeChecking();

        tokenRing.stopThreads();
        tokenRing.checkThroughput(time);
        List<List<Long>> latency = tokenRing.getLatency(6);

        Comparator comparator = Comparator.comparingLong(Long::longValue);
        List<Long> firstLatency = latency.get(0);

        writeLatencyToFile(firstLatency);

        for (List<Long> longs : latency) {
            System.out.println(longs.stream().max(comparator).get().toString() + " nanos");
            OptionalDouble average = longs.stream().mapToInt(e -> Math.toIntExact(e)).average();
            if (average.isPresent()) {
                System.out.println(average.getAsDouble() + " nanos");
            }
            System.out.println();
        }


  */
/*    GraficGenerator graficGenerator = new GraficGenerator();

        List<Integer> firstLatencyInt = firstLatency.stream()
                .mapToInt(Long::intValue)
                .boxed()
                .collect(Collectors.toList());

        int[] arrayX = new int[firstLatencyInt.size()];
        for (int i = 0; i < arrayX.length; i++) {
            arrayX[i] = i * 10;
        }

        int[] arrayY = toIntArray(firstLatencyInt);
        graficGenerator.setX(arrayX);
        graficGenerator.setY(arrayY);
        graficGenerator.setN(arrayX.length);


        graficGenerator.draw();*//*

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
*/
