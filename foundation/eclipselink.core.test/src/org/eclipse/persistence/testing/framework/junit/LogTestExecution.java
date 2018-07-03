/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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
//      Tomas Kraus - Initial implementation
package org.eclipse.persistence.testing.framework.junit;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Log the test being currently executed.
 * <p>
 * Sample usage:<ul>
 * <li>{@code @Rule public LogTestExecution logTestName = new LogTestExecution();}</li></ul>
 * See also:<ul>
 * <li>{@link org.junit.Rule}</li>
 * <li>{@link org.junit.rules.TestWatcher}</li></ul>
 */
public class LogTestExecution extends TestWatcher {

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /**
     * Log test being currently executed.
     * @param description Test descriptor.
     */
    @Override
    protected void starting(Description description) {
        LOG.log(SessionLog.INFO, String.format(
                "Running test %s.%s", description.getTestClass().getSimpleName(), description.getMethodName()));
    }

}
