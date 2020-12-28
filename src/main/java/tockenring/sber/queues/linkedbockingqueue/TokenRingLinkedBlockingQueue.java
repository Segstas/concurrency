package tockenring.sber.queues.linkedbockingqueue;

import tockenring.sber.ContentPackage;
import tockenring.sber.TokenNode;
import tockenring.sber.TokenRing;
import tockenring.sber.queues.arrayblockingqueue.TokenNodeArrayBlockingQueue;

import java.util.ArrayList;
import java.util.List;

public class TokenRingLinkedBlockingQueue implements TokenRing {
    int count = 0;
    public List<TokenNodeLinkedBlockingQueue> tokenNodes = new ArrayList<>();
    public List<Thread> threadNodes = new ArrayList<>();
    public List<ContentPackage> contentPackages = new ArrayList<>();
    public List<List<Long>> allLatency = new ArrayList<>();


    public void setLatencyToZero() {
        for (ContentPackage contentPackage : contentPackages) {
            contentPackage.setTimestampsToZero();
        }
    }

    public void setCountToZero(int node){
        for (TokenNodeLinkedBlockingQueue tokenNode : tokenNodes) {
            tokenNode.setCountToZero();
        }
    }
    public TokenRingLinkedBlockingQueue(int count) {
        this.count = count;
        fillTokenRing(count);
        fillThreadNodes(count);
    }

    public void fillTokenRing(int count) {
        TokenNodeLinkedBlockingQueue firstNode = new TokenNodeLinkedBlockingQueue(0);
        tokenNodes.add(firstNode);
        TokenNodeLinkedBlockingQueue lastNode = firstNode;
        for (int i = 1; i < count; i++) {
            TokenNodeLinkedBlockingQueue node = new TokenNodeLinkedBlockingQueue(i, lastNode);
            tokenNodes.add(node);
            lastNode = node;
        }
        firstNode.setNext(lastNode);
    }

    public void fillThreadNodes(int count) {
        for (TokenNode tokenNode : tokenNodes) {
            threadNodes.add(new Thread(tokenNode));
        }
    }

    public void fillContent(int contentCount) {
        for (int i = 0; i < contentCount; i++) {
            ContentPackage contentPackage = new ContentPackage(Integer.toString(i));
            contentPackages.add(contentPackage);
            tokenNodes.get(0).setContentPackage(contentPackage);
        }
    }

    public void startThreads() {
        for (Thread thread : threadNodes) {
            thread.start();
        }
    }

    @Override
    public List<Long> checkThroughput(long time) {
        List <Long> throughputList = new ArrayList<>();
        for (TokenNodeLinkedBlockingQueue tokenNode : tokenNodes) {
            throughputList.add(tokenNode.checkThroughput(time));
        }
        return throughputList;
    }

    @Override
    public List<List<Long>> getLatency(int count) {
        for (int i = 0; i < count; i++) {
            TokenNode tokenNode = tokenNodes.get(i);
            if (tokenNode.getContentPackage() != null) {
                ContentPackage contentPackage = tokenNode.getContentPackage();
                contentPackage.calculateLatency();
                allLatency.add(contentPackage.latency);
            }
        }
        return allLatency;
    }

    public void stopThreads() {
        for (TokenNodeLinkedBlockingQueue tokenNode : tokenNodes) {
            tokenNode.setCycleController(false);
        }
        for (Thread thread : threadNodes) {
            try {
                thread.join(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}