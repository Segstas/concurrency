package tockenring.sber;

import java.util.concurrent.locks.ReentrantLock;

public class TokenNodeBufferSync implements TokenNode {
    int index;

    TokenNode next;

    private final ReentrantLock lock = new ReentrantLock();

    private  ContentPackage contentPackage;
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
        while (true) {
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
    }

    @Override
    public void sendContentPackage(ContentPackage outboxContentPackage) {
        System.out.println("Content package " + outboxContentPackage.toString() + " has been send from TokenNode #" + this.index);
        this.next.receiveContentPackage(outboxContentPackage);
    }

    @Override
    public void receiveContentPackage(ContentPackage inboxContentPackage) {
        System.out.println("Content package " + inboxContentPackage.toString() + " has been received in #" + this.index);
        synchronized (this){
        while (this.beforeContentPackage != null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }}
        this.beforeContentPackage = inboxContentPackage;
        System.out.println("Content package " + inboxContentPackage.toString() + " has been set in #" + this.index);
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