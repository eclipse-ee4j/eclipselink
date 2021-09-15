/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.framework;

import java.io.*;
import org.eclipse.persistence.sessions.Session;

/**
 * This Interface is used by Test Framework.
 */
public interface TestEntity extends junit.framework.Test, java.io.Serializable {
    int INITIAL_VALUE = -1;

    /**
     * Append test summaries of the collection of tests.
     */
    void appendTestResult(TestResultsSummary summary);

    /**
     * Computes the level for indentation.
     */
    void computeNestedLevel();

    /**
     * The session is initialized to the default login from the Persistent System
     * if no explicit login is done for testing. This method must be overridden in
     * the subclasses if different login is required.
     */
    Session defaultLogin();

    /**
     * Executes the test entity in the collection.
     */
    void execute(TestExecutor executor) throws Throwable;

    /**
     * Return the test collection which contains this test
     */
    TestEntity getContainer();

    /**
     * Return the name of the test
     */
    String getName();

    int getNestedCounter();

    /**
     * Return the test result.
     */
    ResultInterface getReport();


    /**
     * Set the test result.
     */
    void setReport(ResultInterface testResult);

    /**
     * Increment the nested counter
     */
    void incrementNestedCounter();

    /**
     * Logs the test results to the print stream.
     * This mothed is added to migrate tests to Ora*Tst
     */
    void logRegressionResult(Writer log);

    /**
     * Logs the test results to the print stream.
     */
    void logResult(Writer log);

    /**
     * Logs the test results to the print stream.
     */
    void logResult(Writer log, boolean shouldLogOnlyErrors);

    boolean requiresDatabase();

    void resetEntity();

    void resetNestedCounter();

    /**
     * Set the test collection which contains this test
     */
    void setContainer(TestEntity testEntity);

    void setNestedCounter(int level);
}
