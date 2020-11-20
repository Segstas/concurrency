package tockenring.sber.buffervolatile;

import tockenring.sber.ContentPackage;
import tockenring.sber.TokenNode;
import tockenring.sber.TokenRing;

import java.util.ArrayList;
import java.util.List;

public class TokenRingBufferVolatile implements TokenRing {
    int count = 0;
    public List<TokenNodeBufferVolatile> tokenNodes = new ArrayList<>();
    public List<Thread> threadNodes = new ArrayList<>();
    public List<List<Long>> allLatency = new ArrayList<>();


    public TokenRingBufferVolatile(int count) {
        this.count = count;
        fillTokenRing(count);
        fillThreadNodes(count);
    }

    @Override
    public void fillTokenRing(int count) {
        TokenNodeBufferVolatile firstNode = new TokenNodeBufferVolatile(0);
        tokenNodes.add(firstNode);
        TokenNodeBufferVolatile lastNode = firstNode;
        for (int i = 1; i < count; i++) {
            TokenNodeBufferVolatile node = new TokenNodeBufferVolatile(i, lastNode);
            tokenNodes.add(node);
            lastNode = node;
        }
        firstNode.setNext(lastNode);
    }

    @Override
    public void fillThreadNodes(int count) {
        for (TokenNode tokenNode : tokenNodes) {
            threadNodes.add(new Thread(tokenNode));
        }
    }

    @Override
    public void fillContent(int contentCount) {
        for (int i = 0; i < contentCount; i++) {
            ContentPackage contentPackage = new ContentPackage(Integer.toString(i));
            tokenNodes.get(i).setContentPackage(contentPackage);
        }
    }

    @Override
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
        for (TokenNodeBufferVolatile tokenNode : tokenNodes) {
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
