/**
 * ****************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p/>
 * Contributors:
 *      Marcel Valovy - initial API and implementation
 * ****************************************************************************
 */
package org.eclipse.persistence.testing.tests.cache;


import org.eclipse.persistence.internal.cache.AdvancedProcessor;
import org.eclipse.persistence.internal.cache.ComputableTask;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * @author Marcel Valovy - marcelv3612@gmail.com
 */
public class AdvancedProcessorTestCase extends TestCase {

    private AdvancedProcessor processor;
    private MutableComputableTask<Integer, Integer> computableTask;

    public void testCompute() throws Exception {
        assertEquals(processor.compute(computableTask.setArg(5), 5), (Integer) 10);
    }

    public void testExpire() {
        assertEquals(processor.compute(computableTask.setArg(5), 5), (Integer) 10);
        processor.clear();
        assertEquals(processor.compute(computableTask.setArg(7), 5), (Integer) 12);
    }

    public void setUp() {
        processor = new AdvancedProcessor();
        computableTask = new Task<>();
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
    }

    private interface MutableComputableTask<A, V> extends ComputableTask<A, V> {

        MutableComputableTask<A, V> setArg(A arg);
    }

}
