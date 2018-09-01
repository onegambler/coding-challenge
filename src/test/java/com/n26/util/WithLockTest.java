package com.n26.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.locks.Lock;

@RunWith(MockitoJUnitRunner.class)
public class WithLockTest {

    @Mock
    private Lock lock;

    private WithLock withLock;

    @Before
    public void setUp() {
        withLock = new WithLock(lock);
    }

    @Test
    public void whenWithLockIsInvokedThenLockIsLockedAndReleasedCorrectly() {
        withLock.withLock(() -> {
        });
        verify(lock).lock();
        verify(lock).unlock();
        verifyNoMoreInteractions(lock);
    }

    @Test
    public void whenInvokedBlockThrowsExceptionThenLockIsCorrectlyReleased() {

        assertThatThrownBy(() -> withLock.withLock(() -> {
            throw new RuntimeException("something happened");
        })).isInstanceOf(RuntimeException.class);

        verify(lock).lock();
        verify(lock).unlock();
        verifyNoMoreInteractions(lock);
    }
}