package tockenring.sber.queues.concurrentlinkedqueue;

import tockenring.sber.metrics.TimeChecker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

public class TestRunnerConcurrentLinkedQueue {

    public static void main(String[] args) {

        //deleteFiles();

        System.out.println(
                "Test queue START"
        );
        for (int i = 2; i <= 512; i = i * 2) {///nodes
            for (int j = 10000; j <= 10000; j = j + 5) {
                runTest(i, j);
            }
        }
        
/*        for (int i = 256; i <= 1056; i = i + 100) {///nodes
            for (int j = 10000; j <= 10000; j = j + 5) {
                runTest(i, j);
            }
        }*/
        System.out.println(
                "Test queue STOP"
        );
        System.exit(200);
    }

    private static void runTest(int nodeCount, int contentCount) {


        try (FileWriter testNameFile = new FileWriter("testdata/testName.txt", true);
        ) {
            testNameFile.append("ConcurrentLinkedQueue" + '\n');
            testNameFile.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println(
                "START TEST node count = " + nodeCount + ", content count = " + contentCount
        );

        TokenRingConcurrentLinkedQueue tokenRing = new TokenRingConcurrentLinkedQueue(nodeCount);
        tokenRing.fillContent(contentCount);
        tokenRing.startThreads();


        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tokenRing = new TokenRingConcurrentLinkedQueue(nodeCount);
        tokenRing.fillContent(contentCount);
        tokenRing.startThreads();
        TimeChecker.startTimeChecking();

        try {
            Thread.sleep(5000);
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
        File file10 = new File("testdata/AllLatency.txt");
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

    private static void generateThroughput(int nodeCount, int contentCount, TokenRingConcurrentLinkedQueue tokenRing, long time) {

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

    private static void generateLatency(int nodeCount, int contentCount, TokenRingConcurrentLinkedQueue tokenRing) {
        try (FileWriter minLatency = new FileWriter("testdata/MinLatency.txt", true);
             FileWriter avgLatency = new FileWriter("testdata/AvgLatency.txt", true);
             FileWriter maxLatency = new FileWriter("testdata/MaxLatency.txt", true);
             FileWriter allLatency = new FileWriter("testdata/AllLatency.txt", true);

        ) {

            List<Long> contentPackageLatencyList;
            List<Long> contentPackageLatencyListDevided = new ArrayList<>();
            Comparator comparator = Comparator.comparingLong(Long::longValue);
            TokenNodeConcurrentLinkedQueueTimeStampChecker timeStampChecker = (TokenNodeConcurrentLinkedQueueTimeStampChecker)
                    tokenRing.tokenNodes.get(0);
            timeStampChecker.calculateLatency();
            contentPackageLatencyList = timeStampChecker.latency;
            ///tokenRing.contentPackages.get(0).calculateLatency();

            for (Long aLong : contentPackageLatencyList) {
                double d = (Double.valueOf(aLong) / nodeCount);
                contentPackageLatencyListDevided.add((long) d);
                allLatency.append(String.valueOf( (long)d) + '\n');
            }


            System.out.println("Min latency =" + contentPackageLatencyListDevided.stream().min(comparator).get().toString() + " * 10^-9 sec");
            minLatency.append(contentPackageLatencyListDevided.stream().min(comparator).get().toString() + '\n');

            OptionalDouble average = contentPackageLatencyListDevided.stream().mapToLong(num -> num).average();
            if (average.isPresent()) {
                System.out.println("Average latency =" + average.getAsDouble() + " * 10^-9 sec");
                avgLatency.append(String.valueOf((long) average.getAsDouble()) + '\n');
            }
            System.out.println("Max latency =" + contentPackageLatencyListDevided.stream().max(comparator).get().toString() + " * 10^-9 sec");
            maxLatency.append(contentPackageLatencyListDevided.stream().max(comparator).get().toString() + '\n');


            System.out.println(
                    "STOP TEST node count = " + nodeCount + ",content count = " + contentCount + "\n" + "\n"
            );
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}