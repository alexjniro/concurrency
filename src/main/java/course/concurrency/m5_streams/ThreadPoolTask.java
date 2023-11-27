package course.concurrency.m5_streams;

import java.util.concurrent.*;

public class ThreadPoolTask {

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {
        return new ThreadPoolExecutor(
                1,
                4,
                0L,
                TimeUnit.MILLISECONDS,
                new LifoOBlockingDeque<>()
        );
    }

    // Task #2
    public ThreadPoolExecutor getRejectExecutor() {
        return new ThreadPoolExecutor(
                8,
                8,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(8),
                new ThreadPoolExecutor.DiscardPolicy()
        );
    }

    private class LifoOBlockingDeque<T> extends LinkedBlockingDeque<T> {
        @Override
        public boolean offer(T t) {
            return super.offerFirst(t);
        }

        @Override
        public T remove() {
            return super.removeFirst();
        }
    }
}
