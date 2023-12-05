package course.concurrency.m6_tests;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RestrictedBlockingQueue<T> {
    private final int capacity;
    private final LinkedList<T> queue = new LinkedList<>();
    private int size = 0;
    private Object lock = new Object();

    public RestrictedBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void enqueue(T value) {
        synchronized (lock) {
            while (capacity == size) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            queue.add(value);
            size++;
            lock.notifyAll();
        }
    }

    public T dequeue() {
        synchronized (lock) {
            while (size == 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            T value = queue.removeFirst();
            size--;
            lock.notifyAll();
            return value;
        }
    }

    public int getSize() {
        synchronized (lock) {
            return size;
        }
    }

    public int getCapacity() {
        return capacity;
    }
}
