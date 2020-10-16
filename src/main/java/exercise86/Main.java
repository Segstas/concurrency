package exercise86;

public class Main {
    private static final Integer N = 1000;
    private static final Integer M = 10;

    public static void main(String[] args) throws InterruptedException {
        Barrier firstBarrierSync = new FirstBarrierSync(N);
        Barrier secondBarrierSync = new SecondBarrierSync(N);

        Barrier firstBarrier = new FirstBarrier(M);
        Barrier secondBarrier = new SecondBarrier(M);

        secondBarrierTry(firstBarrier);
        Thread.sleep(1000);
        secondBarrierTry(secondBarrier);
        Thread.sleep(1000);
        firstBarrierTry(firstBarrierSync);
        Thread.sleep(1000);
        firstBarrierTry(secondBarrierSync);
    }

    private static void firstBarrierTry(Barrier barrier) {
        for (int i = 0; i < N; ++i) {
            new Thread(() -> {
                foo();
                barrier.await();
                bar();
            }).start();
        }
    }

    private static void secondBarrierTry(Barrier barrier) {
        for (int i = 0; i < M; ++i) {
            new Thread(() -> {
                foo();
                barrier.await();
                bar();
            }).start();
        }
    }

    public static void foo() {
        System.out.println("Foo ->");
    }
    public static void bar() {
        System.out.println("<- Bar");
    }
}