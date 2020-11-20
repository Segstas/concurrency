package tockenring.sber.dummyvolatile;

import tockenring.sber.ContentPackage;
import tockenring.sber.TokenNode;
import tockenring.sber.metrics.ThroughputChecker;

public class TokenNodeDummyVolatile extends ThroughputChecker implements TokenNode {
    int index;

    public void setCycleController(boolean cycleController) {
        this.cycleController = cycleController;
    }

    private volatile boolean cycleController = true;
    TokenNode next;

    private volatile ContentPackage contentPackage;

    public TokenNodeDummyVolatile(int index) {
        this.index = index;
    }

    public TokenNodeDummyVolatile(int index, TokenNode next) {
        this.index = index;
        this.next = next;
    }

    @Override
    public void run() {
        while (cycleController) {
            if (this.contentPackage != null) {
                this.contentPackage.putTimeStamp();
                sendContentPackage(this.contentPackage);
                countIncrement();
                this.contentPackage = null;
            }
        }
    }

    @Override
    public void sendContentPackage(ContentPackage outboxContentPackage) {
      ///  System.out.println("Content package " + outboxContentPackage.toString() + " has been send from TokenNode #" + this.index);
        this.next.receiveContentPackage(outboxContentPackage);
    }

    @Override
    public void receiveContentPackage(ContentPackage inboxContentPackage) {
     ///   System.out.println("Content package " + inboxContentPackage.toString() + " has been received in #" + this.index);
        while (this.contentPackage != null) {
        }
        this.contentPackage = inboxContentPackage;
    ///    System.out.println("Content package " + inboxContentPackage.toString() + " has been set in #" + this.index);
    }

    public ContentPackage getContentPackage() {
        return this.contentPackage;
    }

    public void setContentPackage(ContentPackage contentPackage) {
        this.contentPackage = contentPackage;
    }

    @Override
    public TokenNode getNext() {
        return next;
    }

    public void setNext(TokenNode next) {
        this.next = next;
    }
}