/*******************************************************************************
 * Copyright (c) 2015, 2016  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Tomas Kraus - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.logging;

import org.eclipse.persistence.testing.framework.TestSuite;

/**
 * Logging test suite.
 */
public class LoggingTestSuite extends TestSuite {

    /**
     * Creates an instance of logger tests.
     */
    public LoggingTestSuite() {
        setDescription("This suite tests all of the functionality of the logging framework.");
    }

    /**
     * Creates an instance of logger tests.
     * @param isSRG Indicates whether the test runs as SRG.
     */
    public LoggingTestSuite(final boolean isSRG) {
        super(isSRG);
        setDescription("This suite tests all of the functionality of the logging framework.");
    }

    /**
     * Add tests into this test suite.
     */
    public void addTests() {
        addSRGTests();
        // EclipseLink logging categories enumeration tests.
        LogCategoryTests.addTests(this);
        // EclipseLink log levels enumeration tests.
        LogLevelTests.addTests(this);
        // SLF4J logger tests.
        SLF4JLoggerTests.addTests(this);
    }

    // SRG test set is maintained by QA only, do NOT add any new test cases into it.
    /**
     * Add SRG tests into this test suite.
     */
    public void addSRGTests() {
    }

}
