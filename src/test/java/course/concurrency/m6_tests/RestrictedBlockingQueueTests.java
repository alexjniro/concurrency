package course.concurrency.m6_tests;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RestrictedBlockingQueueTests {

    @Test
    public void executionSingleThread() {
        int capacity = 7;
        RestrictedBlockingQueue<Integer> queue = new RestrictedBlockingQueue<>(capacity);

        for (int i = 0; i < capacity; i++) {
            queue.enqueue(i);
        }

        for (int i = 0; i < capacity; i++) {
            Integer value = queue.dequeue();
            assertEquals(i, value);
        }
    }

    @Test
    public void getAllMultiThread() throws InterruptedException {
        int capacity = 100;
        RestrictedBlockingQueue<Integer> queue = new RestrictedBlockingQueue<>(capacity);

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(capacity*3);

        for (int i = 0; i < capacity; i++) {
            final int element = i;
            executorService.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                queue.enqueue(element);
            });
        }

        ConcurrentLinkedQueue resultQueue = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < capacity; i++) {
            executorService.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Integer value = queue.dequeue();
                resultQueue.add(value);
            });
        }

        latch.countDown();
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        assertEquals(capacity, resultQueue.size());
        for (int i = 0; i < capacity; i++) {
            assertTrue(resultQueue.contains(i));
        }
    }

    @Test
    public void checkFullQueueBlocking() throws InterruptedException {
        int capacity = 2;
        int count = 100;
        RestrictedBlockingQueue<Integer> queue = new RestrictedBlockingQueue<>(capacity);

        int poolSize = capacity*3;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);

        for (int i = 0; i < count; i++) {
            final int element = i;
            executor.submit(() -> queue.enqueue(element));
        }

        assertEquals(capacity, queue.getCapacity());
        assertEquals(capacity, queue.getSize());
        assertEquals(count, executor.getTaskCount());
        // only capacity tasks are down, others are blocked
        assertEquals(capacity, executor.getCompletedTaskCount());

        ConcurrentLinkedQueue resultQueue = new ConcurrentLinkedQueue();
        for (int i = 0; i < count; i++) {
            Integer value = queue.dequeue();
            resultQueue.add(value);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        assertEquals(count, resultQueue.size());
        for (int i = 0; i < count; i++) {
            assertTrue(resultQueue.contains(i));
        }
    }

    @Test
    public void isBlockingIfEmpty() throws InterruptedException {
        int count = 100;
        int capacity = 2;
        RestrictedBlockingQueue<Integer> queue = new RestrictedBlockingQueue<>(capacity);

        int poolSize = capacity*3;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);

        ConcurrentLinkedQueue resultQueue = new ConcurrentLinkedQueue();
        for (int i = 0; i < count; i++) {
            executor.submit(() -> {
                Integer value = queue.dequeue();
                resultQueue.add(value);
            });
        }

        assertEquals(capacity, queue.getCapacity());
        assertEquals(0, queue.getSize());
        assertEquals(0, executor.getCompletedTaskCount());
        assertEquals(count, executor.getTaskCount());

        for (int i = 0; i < count; i++) {
            final int element = i;
            queue.enqueue(element);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        assertEquals(count, resultQueue.size());
        for (int i = 0; i < count; i++) {
            assertTrue(resultQueue.contains(i));
        }
    }
}
