package tockenring.sber.queues.arrayblockingqueue;

import tockenring.sber.ContentPackage;
import tockenring.sber.TokenNode;
import tockenring.sber.metrics.ThroughputChecker;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class TokenNodeArrayBlockingQueue extends ThroughputChecker implements TokenNode {

    int index;

    TokenNode next;

    public void setCycleController(boolean cycleController) {
        this.cycleController = cycleController;
    }

    private volatile boolean cycleController = true;

    private final Queue<ContentPackage> contentPackageQueue = new ArrayBlockingQueue<ContentPackage>(30000) {
    };

    public TokenNodeArrayBlockingQueue(int index) {
        this.index = index;
    }

    public TokenNodeArrayBlockingQueue(int index, TokenNode next) {
        this.index = index;
        this.next = next;
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
        outboxContentPackage.putTimeStamp();
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