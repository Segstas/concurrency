package tockenring.sber.queues.linkedbockingqueue;

import tockenring.sber.ContentPackage;
import tockenring.sber.TokenNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class TokenNodeLinkedBlockingQueueTimeStampChecker extends TokenNodeLinkedBlockingQueue implements TokenNode {

    int index;

    TokenNode next;

    // public HashMap <String, List<Long>> timeStampHashMap = new HashMap<>();


    public List<Long> timestamps = new ArrayList();
    public List<Long> latency = new ArrayList();

    public void putTimeStamp (String string) {
        //     timeStampHashMap.get(string).add(System.nanoTime());
        if (string.equals("0")) {
            timestamps.add(System.nanoTime());
        }
    }

    public void setTimestampsToZero () {
        timestamps = new ArrayList();
    }

    public void calculateLatency () {

        for (int i = 1; i < timestamps.size(); i++) {
            if ((timestamps.get(i) != null) && (timestamps.get(i) != null)) {
                latency.add(timestamps.get(i) - timestamps.get(i - 1));
            }
        }
    }

    public void setCycleController(boolean cycleController) {
        this.cycleController = cycleController;
    }

    private volatile boolean cycleController = true;

    private final Queue<ContentPackage> contentPackageQueue = new LinkedBlockingQueue<ContentPackage>() {
    };

    public TokenNodeLinkedBlockingQueueTimeStampChecker(int index) {
        super(index);
    }

    public TokenNodeLinkedBlockingQueueTimeStampChecker(int index, TokenNode next) {
        super(index,next);
    }

    @Override
    public void run() {
        while (cycleController) {
            if (!contentPackageQueue.isEmpty()) {
                sendContentPackage(this.contentPackageQueue.poll());
            }
        }
    }

    @Override
    public void sendContentPackage(ContentPackage outboxContentPackage) {
        ///      System.out.println("Content package " + outboxContentPackage.toString() + " has been send from TokenNode #" + this.index);

        putTimeStamp(outboxContentPackage.content);
        this.next.receiveContentPackage(outboxContentPackage);
        countIncrement();
    }

    @Override
    public void receiveContentPackage(ContentPackage inboxContentPackage) {
        ///    System.out.println("Content package " + inboxContentPackage.toString() + " has been received in #" + this.index);

        this.contentPackageQueue.add(inboxContentPackage);
        ///   System.out.println("Content package " + inboxContentPackage.toString() + " has been set in #" + this.index);
    }

    public ContentPackage getContentPackage() {
        return this.contentPackageQueue.peek();
    }

    public void setContentPackage(ContentPackage contentPackage) {
        this.contentPackageQueue.add(contentPackage);
    }

    @Override
    public TokenNode getNext() {
        return next;
    }

    public void setNext(TokenNode next) {
        this.next = next;
    }
}