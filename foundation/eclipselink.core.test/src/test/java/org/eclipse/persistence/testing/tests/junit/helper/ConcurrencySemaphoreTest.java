/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.junit.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.persistence.internal.helper.ConcurrencyUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ConcurrencySemaphoreTest {

    private static final int INITIAL_NO_OF_THREADS = 100;
    private static final long MAX_TIME_PERMIT = 500L;
    private static final long TIMEOUT_BETWEEN_LOG_MESSAGES = 20000L;

    @Before
    public void setup() {
        //This kind of setup is for test purpose only. Standard way is via persistence.xml properties or system properties.
        ConcurrencyUtil.SINGLETON.setConcurrencySemaphoreMaxTimePermit(MAX_TIME_PERMIT);
        ConcurrencyUtil.SINGLETON.setConcurrencySemaphoreLogTimeout(TIMEOUT_BETWEEN_LOG_MESSAGES);
    }

    @Test
    public void testConcurrencySemaphore() throws Exception {
        //Verify setup
        assertEquals(MAX_TIME_PERMIT, ConcurrencyUtil.SINGLETON.getConcurrencySemaphoreMaxTimePermit());
        assertEquals(TIMEOUT_BETWEEN_LOG_MESSAGES, ConcurrencyUtil.SINGLETON.getConcurrencySemaphoreLogTimeout());
        //Prepare executor and start threads
        ExecutorService executorService = Executors.newFixedThreadPool(INITIAL_NO_OF_THREADS);
        for (int i = 1; i <= INITIAL_NO_OF_THREADS; i++) {
            Runnable thread = new ConcurrencySemaphoreThread();
            executorService.execute(thread);
        }
        executorService.shutdown();
        // Wait for everything to finish.
        while (!executorService.awaitTermination(20, TimeUnit.SECONDS)) {
            System.out.println("Awaiting completion of threads.");
        }
    }
}
