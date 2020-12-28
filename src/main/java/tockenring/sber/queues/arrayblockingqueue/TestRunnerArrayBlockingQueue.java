package tockenring.sber.queues.arrayblockingqueue;

import tockenring.sber.ContentPackage;
import tockenring.sber.metrics.TimeChecker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TestRunnerArrayBlockingQueue {

    public static void main(String[] args) {

        deleteFiles();

        System.out.println(
                "Test queue START"
        );
        for (int i = 2; i <= 2; i = i + 2) {
            for (int j = 30000; j <= 30000; j = j + 5) {
                runTest(i, j);
            }
        }
        System.out.println(
                "Test queue STOP"
        );
        System.exit(200);
    }

    private static void runTest(int nodeCount, int contentCount) {


        try (FileWriter testNameFile = new FileWriter("testdata/testName.txt", true);
        ) {
            testNameFile.append("ArrayBlockingQueue" + '\n');
            testNameFile.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println(
                "START TEST node count = " + nodeCount + ", content count = " + contentCount
        );

        TokenRingArrayBlockingQueue tokenRing = new TokenRingArrayBlockingQueue(nodeCount);
        tokenRing.fillContent(contentCount);
        tokenRing.startThreads();


        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tokenRing.setLatencyToZero();
        TimeChecker.startTimeChecking();
        tokenRing.setCountToZero(0);////0 means nothing
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long time = TimeChecker.stopTimeChecking();

        tokenRing.stopThreads();

        generateThroughput(nodeCount, contentCount, tokenRing, time);
        generateLatency(nodeCount, contentCount, tokenRing);


        try (FileWriter nodeCountFile = new FileWriter("testdata/nodeCount.txt", true);
             FileWriter contentCountFile = new FileWriter("testdata/contentCount.txt", true);
        ) {
            nodeCountFile.append(String.valueOf(nodeCount) + '\n');
            contentCountFile.append(String.valueOf(contentCount) + '\n');


            nodeCountFile.flush();
            contentCountFile.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());


        }
    }

    private static void deleteFiles() {
        File file1 = new File("testdata/contentCount.txt");
        File file2 = new File("testdata/nodeCount.txt");
        File file3 = new File("testdata/MinThroughput.txt");
        File file4 = new File("testdata/AvgThroughput.txt");
        File file5 = new File("testdata/MaxThroughput.txt");
        File file6 = new File("testdata/MinLatency.txt");
        File file7 = new File("testdata/AvgLatency.txt");
        File file8 = new File("testdata/MaxLatency.txt");
        File file9 = new File("testdata/testName.txt");


        file1.delete();
        file2.delete();
        file3.delete();
        file4.delete();
        file5.delete();
        file6.delete();
        file7.delete();
        file8.delete();
        file9.delete();
    }

    private static void generateThroughput(int nodeCount, int contentCount, TokenRingArrayBlockingQueue tokenRing, long time) {

        try (FileWriter minThroughput = new FileWriter("testdata/MinThroughput.txt", true);
             FileWriter avgThroughput = new FileWriter("testdata/AvgThroughput.txt", true);
             FileWriter maxThroughput = new FileWriter("testdata/MaxThroughput.txt", true);
        ) {

            List<Long> throughput = tokenRing.checkThroughput(time);

            Comparator comparator = Comparator.comparingLong(Long::longValue);

            System.out.println("Min throughput =" + throughput.stream().min(comparator).get().toString() + " pac/sec");

            minThroughput.append(throughput.stream().min(comparator).get().toString() + '\n');

            OptionalDouble average = throughput.stream().mapToLong(num -> num).average();
            if (average.isPresent()) {
                System.out.println("Average throughput =" + average.getAsDouble() + " pac/sec");
                avgThroughput.append(String.valueOf((int) average.getAsDouble()) + '\n');

            }
            System.out.println("Max throughput =" + throughput.stream().max(comparator).get().toString() + " pac/sec");
            maxThroughput.append(throughput.stream().max(comparator).get().toString() + '\n');


            System.out.println(
                    "STOP TEST node count = " + nodeCount + ",content count = " + contentCount + "\n" + "\n"
            );


            minThroughput.flush();
            maxThroughput.flush();
            avgThroughput.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void generateLatency(int nodeCount, int contentCount, TokenRingArrayBlockingQueue tokenRing) {
        try (FileWriter minLatency = new FileWriter("testdata/MinLatency.txt", true);
             FileWriter avgLatency = new FileWriter("testdata/AvgLatency.txt", true);
             FileWriter maxLatency = new FileWriter("testdata/MaxLatency.txt", true);
        ) {

            List<Long> contentPackageLatencyList = new ArrayList<>();
            Comparator comparator = Comparator.comparingLong(Long::longValue);
            for (ContentPackage contentPackage : tokenRing.contentPackages) {
                contentPackage.calculateLatency();
                OptionalDouble average = contentPackage.latency.stream().mapToLong(num -> num).average();
                if (average.isPresent()) {
                    System.out.println("Average for 1 latency =" + average.getAsDouble() + " * 10^-9 sec");
                    contentPackageLatencyList.add((long) average.getAsDouble());
                }
            }
            ///tokenRing.contentPackages.get(0).calculateLatency();

            System.out.println("Min latency =" + contentPackageLatencyList.stream().min(comparator) + " * 10^-9 sec");
            minLatency.append(contentPackageLatencyList.stream().min(comparator).get().toString() + '\n');

            OptionalDouble average = contentPackageLatencyList.stream().mapToLong(num -> num).average();
            if (average.isPresent()) {
                System.out.println("Average latency =" + average.getAsDouble() + " * 10^-9 sec");
                avgLatency.append(String.valueOf(average.getAsDouble()) + '\n');
            }
            System.out.println("Max latency =" + contentPackageLatencyList.stream().max(comparator).get().toString() + " * 10^-9 sec");
            maxLatency.append(contentPackageLatencyList.stream().max(comparator).get().toString() + '\n');


            System.out.println(
                    "STOP TEST node count = " + nodeCount + ",content count = " + contentCount + "\n" + "\n"
            );
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

//

}

//real min and max latency
//
/*    private static void generateLatency(int nodeCount, int contentCount, TokenRingArrayBlockingQueue tokenRing) {
        try (FileWriter minLatency = new FileWriter("testdata/MinLatency.txt", true);
             FileWriter avgLatency = new FileWriter("testdata/AvgLatency.txt", true);
             FileWriter maxLatency = new FileWriter("testdata/MaxLatency.txt", true);
        ) {

            List<Long> contentPackageLatencyList = new ArrayList<>();
            List<Long> contentPackageMinLatencyList = new ArrayList<>();
            List<Long> contentPackageMaxLatencyList = new ArrayList<>();

            Comparator comparator = Comparator.comparingLong(Long::longValue);
            for (ContentPackage contentPackage : tokenRing.contentPackages) {
                contentPackage.calculateLatency();
                OptionalLong min = contentPackage.latency.stream().mapToLong(num -> num).min();
                OptionalDouble average = contentPackage.latency.stream().mapToLong(num -> num).average();
                OptionalLong max = contentPackage.latency.stream().mapToLong(num -> num).max();
                if (average.isPresent()) {
                    System.out.println("Average for 1 latency =" + average.getAsDouble() + " * 10^-9 sec");
                    contentPackageLatencyList.add((long) average.getAsDouble());
                }
                if (min.isPresent()) {
                    contentPackageMinLatencyList.add(min.getAsLong());
                }
                if (max.isPresent()) {
                    contentPackageMaxLatencyList.add((long) max.getAsLong());
                }
            }
            ///tokenRing.contentPackages.get(0).calculateLatency();

            System.out.println("Min latency =" + contentPackageMinLatencyList.stream().min(comparator) + " * 10^-9 sec");
            minLatency.append(contentPackageMinLatencyList.stream().min(comparator).get().toString() + '\n');

            OptionalDouble average = contentPackageLatencyList.stream().mapToLong(num -> num).average();
            if (average.isPresent()) {
                System.out.println("Average latency =" + average.getAsDouble() + " * 10^-9 sec");
                avgLatency.append(String.valueOf(average.getAsDouble()) + '\n');
            }
            System.out.println("Max latency =" + contentPackageMaxLatencyList.stream().max(comparator).get().toString() + " * 10^-9 sec");
            maxLatency.append(contentPackageMaxLatencyList.stream().max(comparator).get().toString() + '\n');


            System.out.println(
                    "STOP TEST node count = " + nodeCount + ",content count = " + contentCount + "\n" + "\n"
            );
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }*/


///average max and min
/*    private static void generateLatency(int nodeCount, int contentCount, TokenRingArrayBlockingQueue tokenRing) {
        try (FileWriter minLatency = new FileWriter("testdata/MinLatency.txt", true);
             FileWriter avgLatency = new FileWriter("testdata/AvgLatency.txt", true);
             FileWriter maxLatency = new FileWriter("testdata/MaxLatency.txt", true);
        ) {

            List<Long> contentPackageLatencyList = new ArrayList<>();
            List<Long> contentPackageMinLatencyList = new ArrayList<>();
            List<Long> contentPackageMaxLatencyList = new ArrayList<>();

            Comparator comparator = Comparator.comparingLong(Long::longValue);
            for (ContentPackage contentPackage : tokenRing.contentPackages) {
                contentPackage.calculateLatency();
                OptionalLong min = contentPackage.latency.stream().mapToLong(num -> num).min();
                OptionalDouble average = contentPackage.latency.stream().mapToLong(num -> num).average();
                OptionalLong max = contentPackage.latency.stream().mapToLong(num -> num).max();
                if (average.isPresent()) {
                    System.out.println("Average for 1 latency =" + average.getAsDouble() + " * 10^-9 sec");
                    contentPackageLatencyList.add((long) average.getAsDouble());
                }
                if (min.isPresent()) {
                    contentPackageMinLatencyList.add(min.getAsLong());
                }
                if (max.isPresent()) {
                    contentPackageMaxLatencyList.add((long) max.getAsLong());
                }
            }
            ///tokenRing.contentPackages.get(0).calculateLatency();

            System.out.println("Min latency =" + contentPackageMinLatencyList.stream().min(comparator) + " * 10^-9 sec");
            //  minLatency.append(contentPackageMinLatencyList.stream().min(comparator).get().toString() + '\n');
            OptionalDouble minAverage = contentPackageMinLatencyList.stream().mapToLong(num -> num).average();
            minLatency.append(String.valueOf((long)minAverage.getAsDouble()) + '\n');

            OptionalDouble average = contentPackageLatencyList.stream().mapToLong(num -> num).average();
            if (average.isPresent()) {
                System.out.println("Average latency =" + average.getAsDouble() + " * 10^-9 sec");
                avgLatency.append(String.valueOf((long)average.getAsDouble()) + '\n');
            }
            OptionalDouble maxAverage = contentPackageMaxLatencyList.stream().mapToLong(num -> num).average();
            maxLatency.append(String.valueOf((long)maxAverage.getAsDouble()) + '\n');


            System.out.println(
                    "STOP TEST node count = " + nodeCount + ",content count = " + contentCount + "\n" + "\n"
            );
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }*/
