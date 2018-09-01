package com.n26.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WithLock {

    private final Lock lock;

    public WithLock() {
        this(new ReentrantLock(true));
    }

    public WithLock(Lock lock) {
        this.lock = lock;
    }

    public void withLock(Lockable block) {
        lock.lock();
        try {
            block.execute();
        } finally {
            lock.unlock();
        }
    }

    @FunctionalInterface
    public interface Lockable {
        void execute();
    }
}
