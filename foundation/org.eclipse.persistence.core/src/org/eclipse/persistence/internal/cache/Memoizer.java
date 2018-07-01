/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//      Marcel Valovy - initial API and implementation
package org.eclipse.persistence.internal.cache;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Memoizer for computing resource expensive tasks asynchronously & concurrently.
 *
 * Inspired by D. Lea. <i>JCiP</i>, 2nd edition. Addison-Wesley, 2006. pp.109
 *
 * @author Marcel Valovy - marcelv3612@gmail.com
 * @since 2.6
 */
public class Memoizer<A, V> implements LowLevelProcessor<A, V> {

    private final ConcurrentMap<MemoizerKey, Future<V>> cache = new ConcurrentHashMap<>();
    private final KeyStorage keyStorage = new KeyStorage();

    @Override
    public V compute(final ComputableTask<A, V> c, final A taskArg) throws InterruptedException {
        final MemoizerKey key = keyStorage.get(c, taskArg);
        while (true) {
            Future<V> f = cache.get(key);
            if (f == null) {
                Callable<V> evaluation = new Callable<V>() {
                    public V call() throws InterruptedException {
                        return c.compute(taskArg);
                    }
                };
                FutureTask<V> ft = new FutureTask<>(evaluation);
                f = cache.putIfAbsent(key, ft);
                if (f == null) {
                    f = ft;
                    ft.run();
                }
            }
            try {
                return f.get();
            } catch (CancellationException e) {
                /*
                 *  "Caching a Future instead of a value creates the possibility of cache pollution: if a computation
                 *  is cancelled or fails, future attempts to compute the results will also indicate cancellation or
                 *  failure. To avoid this, Memoizer removes the Future from the cache if it detects that the
                 *  computation was cancelled."
                 */
                cache.remove(key, f);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Forgets result of the specified task.
     * This allows to manually control size of internal cache.
     *
     * @param task computable task, forming part of result key
     * @param key argument of computation
     */
    public void forget(ComputableTask<A, V> task, A key) {
        cache.remove(this.new MemoizerKey(task, key));
    }

    /**
     * Forgets all cached results.
     */
    public void forgetAll() {
        cache.clear();
    }

    /**
     * Composite-key map.
     */
    private class KeyStorage {

        Map<MemoizerKey, MemoizerKey> map = new ConcurrentHashMap<>();

        public MemoizerKey get(final ComputableTask<A, V> c, final A taskArg) {
            MemoizerKey certificate = new MemoizerKey(c, taskArg);
            MemoizerKey key = map.putIfAbsent(certificate, certificate);
            return (key == null) ? certificate : key;
        }
    }

    /**
     * Key for composite-key map.
     */
    private class MemoizerKey {

        private final ComputableTask<A, V> task;
        private final A key;

        private MemoizerKey(ComputableTask<A, V> task, A key) {
            this.task = task;
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            //noinspection unchecked
            MemoizerKey cacheKey = (MemoizerKey) o;

            //noinspection SimplifiableIfStatement
            if (key != null ? !key.equals(cacheKey.key) : cacheKey.key != null) return false;
            return !(task != null ? !task.equals(cacheKey.task) : cacheKey.task != null);

        }

        @Override
        public int hashCode() {
            int result = task != null ? task.hashCode() : 0;
            result = 31 * result + (key != null ? key.hashCode() : 0);
            return result;
        }
    }
}
