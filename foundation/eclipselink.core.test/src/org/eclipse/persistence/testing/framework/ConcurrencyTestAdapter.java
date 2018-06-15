/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     James Sutherland - initial impl
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
