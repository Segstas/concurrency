package tockenring.sber.sync;

import tockenring.sber.ContentPackage;
import tockenring.sber.TokenNode;
import tockenring.sber.metrics.ThroughputChecker;

public class TokenNodeBufferSync extends ThroughputChecker implements TokenNode {
    int index;

    TokenNode next;

    public void setCycleController(boolean cycleController) {
        this.cycleController = cycleController;
    }

    private volatile boolean cycleController = true;

    private ContentPackage contentPackage;
    private  ContentPackage beforeContentPackage;

    public TokenNodeBufferSync(int index) {
        this.index = index;
    }

    public TokenNodeBufferSync(int index, TokenNode next) {
        this.index = index;
        this.next = next;
    }

    @Override
    public void run() {
        while (cycleController) {
            synchronized (this) {
            if (this.beforeContentPackage != null && this.contentPackage == null) {
                this.contentPackage = this.beforeContentPackage;
                this.beforeContentPackage = null;
                this.notifyAll();
            }}
                if (this.contentPackage != null) {
                    sendContentPackage(this.contentPackage);
                    this.contentPackage = null;
                }
        }
        if (this.beforeContentPackage != null) {
            this.contentPackage = this.beforeContentPackage;
            this.beforeContentPackage = null;
        }
    }

    @Override
    public void sendContentPackage(ContentPackage outboxContentPackage) {
    ///    System.out.println("Content package " + outboxContentPackage.toString() + " has been send from TokenNode #" + this.index);
        this.next.receiveContentPackage(outboxContentPackage);
    }

    @Override
    public void receiveContentPackage(ContentPackage inboxContentPackage) {
     ///   System.out.println("Content package " + inboxContentPackage.toString() + " has been received in #" + this.index);
        synchronized (this){
            countIncrement();
            inboxContentPackage.putTimeStamp();

            while (this.beforeContentPackage != null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }}
        this.beforeContentPackage = inboxContentPackage;
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