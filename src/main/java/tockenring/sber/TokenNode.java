package tockenring.sber;

public interface TokenNode extends Runnable{
    void sendContentPackage(ContentPackage outboxContentPackage);

    void receiveContentPackage(ContentPackage inboxContentPackage);

    ContentPackage getContentPackage();

    void setContentPackage(ContentPackage contentPackage);

    TokenNode getNext();

    void setNext(TokenNode next);
}
