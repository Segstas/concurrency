package tockenring.sber;

public class TokenNodeDummy implements TokenNode {
    int index;

    TokenNode next;

    private  ContentPackage contentPackage;

    public TokenNodeDummy(int index) {
        this.index = index;
    }

    public TokenNodeDummy(int index, TokenNode next) {
        this.index = index;
        this.next = next;
    }

    @Override
    public void run() {
        while (true) {
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
        while (this.contentPackage != null) {
        }
        this.contentPackage = inboxContentPackage;
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