package tockenring.sber.queues.arrayblockingqueue;

import tockenring.sber.ContentPackage;
import tockenring.sber.TokenNode;
import tockenring.sber.TokenRing;

import java.util.ArrayList;
import java.util.List;

public class TokenRingArrayBlockingQueue implements TokenRing {
    int count = 0;

    public List<TokenNodeArrayBlockingQueue> tokenNodes = new ArrayList<>();
    public List<Thread> threadNodes = new ArrayList<>();
    public List<ContentPackage> contentPackages = new ArrayList<>();
    public List<List<Long>> allLatency = new ArrayList<>();


/*    public void setLatencyToZero() {
        for (ContentPackage contentPackage : contentPackages) {
            contentPackage.setTimestampsToZero();
        }
    }*/

    public void setCountToZero(int node){
        for (TokenNodeArrayBlockingQueue tokenNode : tokenNodes) {
            tokenNode.setCountToZero();
        }
    }

    public TokenRingArrayBlockingQueue(int count) {
        this.count = count;
        fillTokenRing(count);
        fillThreadNodes(count);
    }

    public void fillTokenRing(int count) {
        TokenNodeArrayBlockingQueueTimeStampChecker firstNode = new TokenNodeArrayBlockingQueueTimeStampChecker(0);
        tokenNodes.add(firstNode);
        TokenNodeArrayBlockingQueue lastNode = firstNode;
        for (int i = 1; i < count; i++) {
            TokenNodeArrayBlockingQueue node = new TokenNodeArrayBlockingQueue(i, lastNode);
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
        TokenNodeArrayBlockingQueueTimeStampChecker timeStampChecker = (TokenNodeArrayBlockingQueueTimeStampChecker) tokenNodes.get(0);

        int size = threadNodes.size();
        int contentCountPerNode = contentCount/size;
        int additionalContentCountPerNode = contentCount%tokenNodes.size();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < contentCountPerNode; j++) {
                ContentPackage contentPackage = new ContentPackage(Integer.toString(j));
                contentPackages.add(contentPackage);
                tokenNodes.get(i).setContentPackage(contentPackage);
          //      timeStampChecker.timeStampHashMap.put(Integer.toString(j), new ArrayList<>());
            }
            if (additionalContentCountPerNode > 0) {
                ContentPackage contentPackage = new ContentPackage("Add" + additionalContentCountPerNode);
                contentPackages.add(contentPackage);
                tokenNodes.get(i).setContentPackage(contentPackage);
          //      timeStampChecker.timeStampHashMap.put(("Add" + additionalContentCountPerNode), new ArrayList<>());
                additionalContentCountPerNode--;
            }
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
        throughputList.add(tokenNodes.get(0).checkThroughput(time));
        return throughputList;
    }

    @Override
    public List<Long> getLatency(int count) {
        TokenNodeArrayBlockingQueueTimeStampChecker tokenNode = (TokenNodeArrayBlockingQueueTimeStampChecker)tokenNodes.get(0);
        tokenNode.calculateLatency();
        return tokenNode.latency;
    }

    public void stopThreads() {
        for (TokenNodeArrayBlockingQueue tokenNode : tokenNodes) {
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
