package tockenring.sber.buffervolatile;

import tockenring.sber.ContentPackage;
import tockenring.sber.TokenNode;
import tockenring.sber.metrics.ThroughputChecker;

import java.util.concurrent.locks.ReentrantLock;

public class TokenNodeBufferVolatile extends ThroughputChecker implements TokenNode {
    int index;

    public void setCycleController(boolean cycleController) {
        this.cycleController = cycleController;
    }

    private volatile boolean cycleController = true;
    TokenNode next;

    private ContentPackage contentPackage;
    private volatile ContentPackage beforeContentPackage;

    public TokenNodeBufferVolatile(int index) {
        this.index = index;
    }

    public TokenNodeBufferVolatile(int index, TokenNode next) {
        this.index = index;
        this.next = next;
    }

    @Override
    public void run() {
        while (cycleController) {
            if (this.beforeContentPackage != null && this.contentPackage == null) {
                this.contentPackage = this.beforeContentPackage;
                countIncrement();
                this.beforeContentPackage = null;
            }
                if (this.contentPackage != null) {
                    this.contentPackage.putTimeStamp();
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
       /// System.out.println("Content package " + outboxContentPackage.toString() + " has been send from TokenNode #" + this.index);
        this.next.receiveContentPackage(outboxContentPackage);
    }

    @Override
    public void receiveContentPackage(ContentPackage inboxContentPackage) {
     ///   System.out.println("Content package " + inboxContentPackage.toString() + " has been received in #" + this.index);
        while (this.beforeContentPackage != null) {
        }
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