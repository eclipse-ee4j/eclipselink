/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Marcel Valovy - initial API and implementation
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package org.eclipse.persistence.testing.tests.junit.cache;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.internal.cache.AdvancedProcessor;
import org.eclipse.persistence.internal.cache.ComputableTask;
import org.eclipse.persistence.internal.cache.Memoizer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Marcel Valovy - marcelv3612@gmail.com
 */
public class AdvancedProcessorTest {

    private AdvancedProcessor processor;
    private MutableComputableTask<Integer, Integer> computableTask;

    @Before
    public void setUp() {
        processor = new AdvancedProcessor();
        computableTask = new Task<>();
    }

    @Test
    public void testCompute() throws Exception {
        Assert.assertEquals(processor.compute(computableTask.setArg(5), 5), (Integer) 10);

        Field fieldOnProcessor = AdvancedProcessor.class.getDeclaredField("memoizer");
        fieldOnProcessor.setAccessible(true);
        //noinspection unchecked
        Memoizer<Object, Object> memoizer = (Memoizer<Object, Object>) fieldOnProcessor.get(processor);
        Field fieldOnMemoizer = Memoizer.class.getDeclaredField("cache");
        fieldOnMemoizer.setAccessible(true);
        Map memoizerCache = (Map) fieldOnMemoizer.get(memoizer);
        Object futureAfterFirstComputation = memoizerCache.values().iterator().next();

        processor.compute(computableTask.setArg(5), 5);

        Object futureAfterSecondComputation = memoizerCache.values().iterator().next();

        Assert.assertTrue(memoizerCache.values().size() == 1);
        Assert.assertTrue(futureAfterFirstComputation == futureAfterSecondComputation);
        fieldOnProcessor.setAccessible(false);
        fieldOnMemoizer.setAccessible(false);
    }

    public void testExpire() throws Exception {

        Assert.assertEquals(processor.compute(computableTask.setArg(5), 5), (Integer) 10);

        Field fieldOnProcessor = AdvancedProcessor.class.getDeclaredField("memoizer");
        fieldOnProcessor.setAccessible(true);
        //noinspection unchecked
        Memoizer<Object, Object> memoizer = (Memoizer<Object, Object>) fieldOnProcessor.get(processor);
        Field fieldOnMemoizer = Memoizer.class.getDeclaredField("cache");
        fieldOnMemoizer.setAccessible(true);
        Map memoizerCache = (Map) fieldOnMemoizer.get(memoizer);
        Object futureAfterFirstComputation = memoizerCache.values().iterator().next();

        processor.clear();

        processor.compute(computableTask.setArg(5), 5);

        final Iterator iteratorAfterSecondComputation = memoizerCache.values().iterator();
        // we still have just one element in cache, because we called processor.clear()
        Object futureAfterSecondComputation = iteratorAfterSecondComputation.next();

        Assert.assertFalse(futureAfterFirstComputation == futureAfterSecondComputation);
        fieldOnProcessor.setAccessible(false);
        fieldOnMemoizer.setAccessible(false);

        Assert.assertEquals(processor.compute(computableTask.setArg(7), 5), (Integer) 12);
    }

    static class Task<A, V> implements MutableComputableTask<A, V> {
        private A a;

        @Override
        public V compute(A arg) throws InterruptedException {
            //noinspection unchecked
            return (V)(Object)((Integer) a + (Integer) arg);
        }

        @Override
        public MutableComputableTask<A, V> setArg(A arg) {
            a = arg;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Task task = (Task) o;

            if (a != null ? !a.equals(task.a) : task.a != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return a != null ? a.hashCode() : 0;
        }
    }

    private interface MutableComputableTask<A, V> extends ComputableTask<A, V> {

        MutableComputableTask<A, V> setArg(A arg);
    }

}
