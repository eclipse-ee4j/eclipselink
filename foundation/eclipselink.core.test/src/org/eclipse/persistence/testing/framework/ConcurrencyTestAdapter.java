/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial impl
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;

/**
 * This adapts a normal test or performance test to run as a concurrency test.
 */
public class ConcurrencyTestAdapter extends ConcurrentPerformanceComparisonTest {
    protected TestCase test;

    public ConcurrencyTestAdapter(TestCase test) {
        this.test = test;
        setName("Concurrent" + test.getName());
        setDescription("Concurrent test for:" + test.getDescription());
    }

    public void setup() {
        super.setup();
        this.test.setExecutor(getExecutor());
        try {
            this.test.setup();
        } catch (Throwable error) {
            throw new Error(error);
        }
    }

    public void reset() {
        super.reset();
        try {
            this.test.reset();
        } catch (Throwable error) {
            throw new Error(error);
        }
    }

    public void runTask() throws Exception {
        try {
            this.test.test();
        } catch (Throwable error) {
            throw new Error(error);
        }
    }
}
