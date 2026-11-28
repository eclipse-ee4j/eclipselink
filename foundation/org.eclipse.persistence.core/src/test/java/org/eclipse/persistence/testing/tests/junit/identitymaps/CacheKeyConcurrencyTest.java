/*
 * Copyright (c) 2026 DruID. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.junit.identitymaps;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

import org.eclipse.persistence.annotations.CacheIsolationType;
import org.eclipse.persistence.config.MergeManagerOperationMode;
import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ConcurrencyManager;
import org.eclipse.persistence.internal.helper.ConcurrencyUtil;
import org.eclipse.persistence.internal.helper.DeferredLockManager;
import org.eclipse.persistence.internal.helper.WriteLockManager;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.FullIdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.IsolatedClientSession;
import org.eclipse.persistence.internal.sessions.IsolatedClientSessionIdentityMapAccessor;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkIdentityMapAccessor;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.server.ConnectionPolicy;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/** Tests the real cache wait paths without a database, JMS or timing-dependent load. */
@Timeout(value = 15, unit = TimeUnit.SECONDS)
public class CacheKeyConcurrencyTest {
    enum Route {
        CHANGE_SET, SESSION_ORIGIN, SESSION_WAITLOOP, UNIT_OF_WORK, ISOLATED_CLIENT;

        boolean isMerge() {
            return this == CHANGE_SET || this == SESSION_ORIGIN || this == SESSION_WAITLOOP;
        }
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(Route.class)
    public void returnsAnAlreadyConstructedObject(Route route) throws Exception {
        try (Fixture fixture = new Fixture(route)) {
            fixture.finishConstruction(true);
            Future<Object> read = fixture.start();
            fixture.assertResult(read.get(5, TimeUnit.SECONDS));
            fixture.assertReleased();
        }
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(Route.class)
    public void waitsForConstructionAndReturnsThePublishedObject(Route route) throws Exception {
        try (Fixture fixture = new Fixture(route)) {
            Future<Object> read = fixture.start();
            fixture.awaitWaitingOrCompletion();
            fixture.finishConstruction(true);
            fixture.assertResult(read.get(5, TimeUnit.SECONDS));
            fixture.assertReleased();
        }
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(Route.class)
    public void notificationWithoutAnObjectDoesNotFinishTheRead(Route route) throws Exception {
        try (Fixture fixture = new Fixture(route)) {
            Future<Object> read = fixture.start();
            fixture.awaitWaitingOrCompletion();
            fixture.referenceKey.getInstanceLock().lock();
            try {
                fixture.referenceKey.getInstanceLockCondition().signalAll();
            } finally {
                fixture.referenceKey.getInstanceLock().unlock();
            }
            assertThrows(TimeoutException.class, () -> read.get(100, TimeUnit.MILLISECONDS));
            fixture.finishConstruction(true);
            fixture.assertResult(read.get(5, TimeUnit.SECONDS));
            fixture.assertReleased();
        }
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(Route.class)
    public void returnsNullWhenConstructionFinishesWithoutAnObject(Route route) throws Exception {
        try (Fixture fixture = new Fixture(route)) {
            Future<Object> read = fixture.start();
            fixture.awaitWaitingOrCompletion();
            fixture.finishConstruction(false);
            assertNull(read.get(2, TimeUnit.SECONDS));
            fixture.assertReleased();
        }
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(Route.class)
    public void interruptionDoesNotLeakLocks(Route route) throws Exception {
        try (Fixture fixture = new Fixture(route)) {
            Future<Object> read = fixture.start();
            fixture.awaitWaitingOrCompletion();
            fixture.worker.get().interrupt();
            if (route == Route.SESSION_WAITLOOP) {
                fixture.awaitInterruptionHandling();
            }
            fixture.finishConstruction(true);
            // These legacy APIs swallow InterruptedException; preserve that contract.
            read.get(5, TimeUnit.SECONDS);
            fixture.assertReleased();
        }
    }

    static final class Fixture implements AutoCloseable {
        final Route route;
        final TestSession session = new TestSession();
        final ClassDescriptor descriptor = new ClassDescriptor();
        final Entity reference = new Entity(2);
        final ProbeCacheKey referenceKey;
        final AtomicReference<Thread> worker = new AtomicReference<>();
        final ExecutorService executor = Executors.newSingleThreadExecutor(task -> {
            Thread thread = new Thread(task, "cache-key-regression-reader");
            thread.setDaemon(true);
            worker.set(thread);
            return thread;
        });
        final String previousMode = ConcurrencyUtil.SINGLETON.getConcurrencyManagerAllowGetCacheKeyForMergeMode();
        volatile CacheKey mergeKey;
        volatile boolean acquireReferenceKeyBeforeRead;
        boolean readOnly;
        boolean appendLock;
        Future<Object> result;

        Fixture(Route route) {
            this.route = route;
            descriptor.setJavaClass(Entity.class);
            descriptor.setTableName("CACHE_LOCK_TEST");
            descriptor.addPrimaryKeyFieldName("ID");
            descriptor.addDirectMapping("id", "ID");
            descriptor.setIdentityMapClass(ProbeIdentityMap.class);
            if (route == Route.ISOLATED_CLIENT) {
                descriptor.getCachePolicy().setCacheIsolation(CacheIsolationType.PROTECTED);
            }
            session.addDescriptor(descriptor);
            session.initializeDescriptors(Collections.singletonList(descriptor), false);
            referenceKey = (ProbeCacheKey) session.getIdentityMapAccessorInstance()
                    .acquireLockNoWait(2, Entity.class, false, descriptor);
            assertNotNull(referenceKey);
            if (route == Route.SESSION_ORIGIN || route == Route.SESSION_WAITLOOP) {
                ConcurrencyUtil.SINGLETON.setConcurrencyManagerAllowGetCacheKeyForMergeMode(
                        route == Route.SESSION_ORIGIN ? MergeManagerOperationMode.ORIGIN : MergeManagerOperationMode.WAITLOOP);
            }
        }

        Future<Object> start() {
            result = executor.submit(() -> {
                assertNull(ConcurrencyManager.getDeferredLockManager(Thread.currentThread()));
                boolean ownsReferenceKey = false;
                try {
                    if (acquireReferenceKeyBeforeRead) {
                        assertTrue(referenceKey.acquireNoWait());
                        ownsReferenceKey = true;
                    }
                    if (!route.isMerge()) {
                        if (route == Route.UNIT_OF_WORK) {
                            UnitOfWorkImpl unit = new UnitOfWorkImpl(session, ReferenceMode.HARD);
                            if (readOnly) {
                                unit.addReadOnlyClass(Entity.class);
                            }
                            return new TestUnitOfWorkAccessor(unit).read(descriptor);
                        }
                        IsolatedClientSession client = new IsolatedClientSession(session,
                                new ConnectionPolicy(session.getDatasourceLogin()));
                        return new TestIsolatedAccessor(client).read(descriptor);
                    }
                    WriteLockManager locks = session.getIdentityMapAccessorInstance().getWriteLockManager();
                    MergeManager merge = new MergeManager(session);
                    merge.mergeIntoDistributedCache();
                    mergeKey = locks.appendLock(1, new Entity(1), descriptor, merge, session);
                    try {
                        if (appendLock) {
                            return locks.appendLock(2, reference, descriptor, merge, session).getObject();
                        }
                        if (route == Route.CHANGE_SET) {
                            ObjectChangeSet change = new ObjectChangeSet(2, descriptor, reference,
                                    new UnitOfWorkChangeSet(session), false);
                            return change.getTargetVersionOfSourceObject(merge, session, false);
                        }
                        CacheKey key = session.resolve(reference, descriptor, merge);
                        return key == null ? null : key.getObject();
                    } finally {
                        locks.releaseAllAcquiredLocks(merge);
                    }
                } finally {
                    if (ownsReferenceKey) {
                        referenceKey.release();
                    }
                }
            });
            return result;
        }

        void awaitWaitingOrCompletion() throws Exception {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (System.nanoTime() < deadline) {
                if (result.isDone()) {
                    return; // Propagate an early exception through Future.get(), after releasing the producer.
                }
                if (route == Route.SESSION_WAITLOOP && !referenceKey.deferredAcquired) {
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1));
                    continue;
                }
                Thread thread = worker.get();
                // WAITLOOP defaults to a zero-millisecond poll and may remain RUNNABLE.
                if (thread != null && (route == Route.SESSION_WAITLOOP || thread.getState() == Thread.State.WAITING
                        || thread.getState() == Thread.State.TIMED_WAITING)) {
                    for (StackTraceElement frame : thread.getStackTrace()) {
                        if (frame.getMethodName().equals("getObjectForMerge")
                                || frame.getMethodName().equals("getCacheKeyFromTargetSessionForMerge")
                                || frame.getMethodName().equals("getAndCloneCacheKeyFromParent")
                                || frame.getMethodName().equals("releaseDeferredLock")
                                || frame.getMethodName().equals("waitForObject")) {
                            return;
                        }
                    }
                }
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1));
            }
            fail("Reader did not reach an EclipseLink cache wait: " + route);
        }

        void awaitInterruptionHandling() {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (System.nanoTime() < deadline) {
                referenceKey.getInstanceLock().lock();
                try {
                    if (referenceKey.getInvalidationState() == CacheKey.CACHE_KEY_INVALID) {
                        return;
                    }
                } finally {
                    referenceKey.getInstanceLock().unlock();
                }
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1));
            }
            fail("WAITLOOP did not reach its InterruptedException handler");
        }

        void finishConstruction(boolean publish) {
            if (referenceKey.getActiveThread() == Thread.currentThread()) {
                if (publish) {
                    referenceKey.setObject(reference);
                }
                referenceKey.release();
            }
        }

        void assertResult(Object value) {
            assertNotNull(value, "The object published by the producer must be returned");
            assertEquals(2, ((Entity) value).id);
            if (route.isMerge() || readOnly) {
                assertSame(reference, value);
            } else {
                assertNotSame(reference, value, "A client/unit of work must receive its working clone");
            }
        }

        void assertReleased() throws Exception {
            assertNull(ConcurrencyManager.getDeferredLockManager(worker.get()),
                    "Deferred manager retained after normal cleanup");
            assertNull(ConcurrencyManager.getReadLockManager(worker.get()), "Read manager retained after normal cleanup");
            for (CacheKey key : new CacheKey[] {referenceKey, mergeKey}) {
                if (key != null) {
                    assertNull(key.getActiveThread(), "Logical cache lock owner retained");
                    assertEquals(0, key.getDepth());
                    assertEquals(0, key.getNumberOfReaders());
                    assertTrue(key.getInstanceLock().tryLock(1, TimeUnit.SECONDS), "State lock retained");
                    key.getInstanceLock().unlock();
                    // The producer thread differs from the reader and must be able to acquire the key.
                    assertTrue(key.acquireNoWait());
                    key.release();
                }
            }
            assertSame(referenceKey.getObject(), session.getIdentityMapAccessorInstance().getFromIdentityMap(2, Entity.class));
        }

        @Override
        public void close() throws Exception {
            finishConstruction(true);
            if (result != null && !result.isDone()) {
                referenceKey.getInstanceLock().lock();
                try {
                    referenceKey.setObject(reference); // Let a self-waiting regression baseline terminate.
                } finally {
                    referenceKey.getInstanceLock().unlock();
                }
                worker.get().interrupt(); // Also unblocks the original ORIGIN monitor wait on a failing baseline.
            }
            try {
                executor.submit((Callable<Void>) () -> {
                    DeferredLockManager remaining = ConcurrencyManager.removeDeferredLockManager(Thread.currentThread());
                    if (remaining != null) {
                        remaining.releaseActiveLocksOnThread();
                    }
                    while (referenceKey.getNumberOfReaders() > 0) {
                        referenceKey.releaseReadLock();
                    }
                    if (mergeKey != null && mergeKey.getActiveThread() == Thread.currentThread()) {
                        while (mergeKey.getDepth() > 0) {
                            mergeKey.release();
                        }
                    }
                    return null;
                }).get(5, TimeUnit.SECONDS);
            } finally {
                executor.shutdownNow();
                try {
                    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), "Reader executor must terminate");
                } finally {
                    ConcurrencyUtil.SINGLETON.setConcurrencyManagerAllowGetCacheKeyForMergeMode(previousMode);
                }
            }
        }
    }

    public static class Entity {
        public int id;

        public Entity() {
        }

        Entity(int id) {
            this.id = id;
        }
    }

    public static class ProbeIdentityMap extends FullIdentityMap {
        public ProbeIdentityMap(int size, ClassDescriptor descriptor, AbstractSession session, boolean isolated) {
            super(size, descriptor, session, isolated);
        }

        @Override
        public CacheKey createCacheKey(Object key, Object object, Object version, long readTime) {
            return new ProbeCacheKey(key, object, version, readTime, isIsolated);
        }
    }

    static class ProbeCacheKey extends CacheKey {
        volatile Runnable beforeDeferredAcquire;
        volatile RuntimeException failureAfterDeferredAcquire;
        volatile RuntimeException failureAfterReadAcquire;
        volatile RuntimeException failureDuringObjectWait;
        volatile boolean deferredAcquired;
        private Thread failingThread;

        ProbeCacheKey(Object key, Object object, Object version, long readTime, boolean isolated) {
            super(key, object, version, readTime, isolated);
        }

        @Override
        public void acquireDeferredLock() {
            if (beforeDeferredAcquire != null) {
                beforeDeferredAcquire.run();
            }
            super.acquireDeferredLock();
            if (failureAfterDeferredAcquire != null) {
                failingThread = Thread.currentThread();
            }
            deferredAcquired = true;
        }

        @Override
        public boolean acquireReadLockNoWait() {
            boolean acquired = super.acquireReadLockNoWait();
            if (acquired && failureAfterReadAcquire != null) {
                failingThread = Thread.currentThread();
            }
            return acquired;
        }

        @Override
        public Object waitForObject() {
            if (failureDuringObjectWait != null) {
                throw failureDuringObjectWait;
            }
            return super.waitForObject();
        }

        @Override
        public Object getObject() {
            if (Thread.currentThread() == failingThread) {
                failingThread = null;
                throw failureAfterReadAcquire != null ? failureAfterReadAcquire : failureAfterDeferredAcquire;
            }
            return super.getObject();
        }
    }

    static class TestSession extends ServerSession {
        TestSession() {
            super(new DatabaseLogin());
        }

        CacheKey resolve(Entity entity, ClassDescriptor descriptor, MergeManager merge) {
            return getCacheKeyFromTargetSessionForMerge(entity, descriptor.getObjectBuilder(), descriptor, merge);
        }
    }

    static class TestUnitOfWorkAccessor extends UnitOfWorkIdentityMapAccessor {
        TestUnitOfWorkAccessor(UnitOfWorkImpl unit) {
            super(unit, unit.getIdentityMapAccessorInstance().getIdentityMapManager());
        }

        Object read(ClassDescriptor descriptor) {
            return getAndCloneCacheKeyFromParent(2, null, Entity.class, true, descriptor);
        }
    }

    static class TestIsolatedAccessor extends IsolatedClientSessionIdentityMapAccessor {
        TestIsolatedAccessor(IsolatedClientSession session) {
            super(session);
        }

        Object read(ClassDescriptor descriptor) {
            return getAndCloneCacheKeyFromParent(2, null, Entity.class, true, descriptor);
        }
    }
}
