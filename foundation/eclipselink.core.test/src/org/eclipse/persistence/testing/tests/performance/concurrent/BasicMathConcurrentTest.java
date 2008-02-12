/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.performance.concurrent;

import org.eclipse.persistence.testing.framework.*;

/**
 * This tests the concurrency of the machine through testing math operations with multiple threads.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public class BasicMathConcurrentTest extends ConcurrentPerformanceComparisonTest {
    public BasicMathConcurrentTest() {
        setName("BasicMathConcurrentTest");
        setDescription("This tests the concurrency of the machine through testing math operations with multiple threads.");
    }

    /**
     * Perform 100 cos.
     */
    public void runTask() throws Exception {
        for (int index = 0; index < 100; index++) {
            double d = Math.acos(4.5);
            d = d + 100;
            d = Math.cos(d);
        }
    }
}