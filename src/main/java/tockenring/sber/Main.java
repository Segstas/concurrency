package tockenring.sber;

public class Main {
    public static void main(String[] args) {
        TokenNode node0 = new TokenNodeDummyVolatile(0);
        TokenNode node1 = new TokenNodeDummyVolatile(1, node0);
        TokenNode node2 = new TokenNodeDummyVolatile(2, node1);
        TokenNode node3 = new TokenNodeDummyVolatile(3, node2);
        TokenNode node4 = new TokenNodeDummyVolatile(4, node3);
        node0.setNext(node4);

        Thread node0Tread = new Thread(node0);
        Thread node1Tread = new Thread(node1);
        Thread node2Tread = new Thread(node2);
        Thread node3Tread = new Thread(node3);
        Thread node4Tread = new Thread(node4);


        ContentPackage contentPackage = new ContentPackage("0");
        ContentPackage contentPackage1 = new ContentPackage("1");
        ContentPackage contentPackage2 = new ContentPackage("2");
        ContentPackage contentPackage3 = new ContentPackage("3");
        ContentPackage contentPackage4 = new ContentPackage("4");

        node0.setContentPackage(contentPackage);
        node1.setContentPackage(contentPackage1);
        node2.setContentPackage(contentPackage2);
        node3.setContentPackage(contentPackage3);
     ////   node4.setContentPackage(contentPackage4);

        node0Tread.start();
        node1Tread.start();
        node2Tread.start();
        node3Tread.start();
        node4Tread.start();
    }
}
