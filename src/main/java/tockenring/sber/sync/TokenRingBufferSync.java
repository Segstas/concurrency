package tockenring.sber.sync;

import tockenring.sber.ContentPackage;
import tockenring.sber.TokenNode;
import tockenring.sber.TokenRing;
import tockenring.sber.queue.TokenNodeQueue;

import java.util.ArrayList;
import java.util.List;

public class TokenRingBufferSync implements TokenRing {
    int count = 0;
    public List<TokenNodeBufferSync> tokenNodes = new ArrayList<>();
    public List<Thread> threadNodes = new ArrayList<>();
    public List<ContentPackage> contentPackages = new ArrayList<>();
    public List<List<Long>> allLatency = new ArrayList<>();

    public TokenRingBufferSync(int count) {
        this.count = count;
        fillTokenRing(count);
        fillThreadNodes(count);
    }

    public void fillTokenRing(int count) {
        TokenNodeBufferSync firstNode = new TokenNodeBufferSync(0);
        tokenNodes.add(firstNode);
        TokenNodeBufferSync lastNode = firstNode;
        for (int i = 1; i < count; i++) {
            TokenNodeBufferSync node = new TokenNodeBufferSync(i, lastNode);
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
            tokenNodes.get(i).setContentPackage(contentPackage);
        }
    }

    public void startThreads() {
        for (Thread thread : threadNodes) {
            thread.start();
        }
    }

    @Override
    public long checkThroughput(long time) {
        return tokenNodes.get(0).checkThroughput(time);
    }

    @Override
    public List<List<Long>>  getLatency(int count) {
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
        for (TokenNodeBufferSync tokenNode : tokenNodes) {
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
