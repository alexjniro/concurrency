package course.concurrency.m3_shared;

public class PingPong {
    private static volatile boolean isPing = true;

    public static void ping() {
        for (int i = 0; i < 10; i++) {
            synchronized (PingPong.class) {
                while (!isPing) {
                    try {
                        PingPong.class.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Ping");
                isPing = false;
                PingPong.class.notifyAll();
            }
        }
    }

    /**
     *
     */
    public static void pong() {
        for (int i = 0; i < 10; i++) {
            synchronized (PingPong.class) {
                while (isPing) {
                    try {
                        PingPong.class.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Pong");
                isPing = true;
                PingPong.class.notifyAll();
            }
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> ping());
        Thread t2 = new Thread(() -> pong());
        t1.start();
        t2.start();
    }
}
