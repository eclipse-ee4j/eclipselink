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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;

import java.io.*;
import org.eclipse.persistence.sessions.Session;

/**
 * This Interface is used by Test Framework.
 */
public interface TestEntity extends junit.framework.Test, java.io.Serializable {
    public static final int INITIAL_VALUE = -1;

    /**
     * Append test summaries of the collection of tests.
     */
    public void appendTestResult(TestResultsSummary summary);

    /**
     * Computes the level for indentation.
     */
    public void computeNestedLevel();

    /**
     * The session is initialized to the default login from the Persistent System
     * if no explicit login is done for testing. This method must be overridden in
     * the subclasses if different login is required.
     */
    public Session defaultLogin();

    /**
     * Executes the test entity in the collection.
     */
    public void execute(TestExecutor executor) throws Throwable;

    /**
     * Return the test collection which contains this test
     */
    public TestEntity getContainer();

    /**
     * Return the name of the test
     */
    public String getName();

    public int getNestedCounter();

    /**
     * Return the test result.
     */
    public ResultInterface getReport();


    /**
     * Set the test result.
     */
    public void setReport(ResultInterface testResult);

    /**
     * Increment the nested counter
     */
    public void incrementNestedCounter();

    /**
     * Logs the test results to the print stream.
     * This mothed is added to migrate tests to Ora*Tst
     */
    public void logRegressionResult(Writer log);

    /**
     * Logs the test results to the print stream.
     */
    public void logResult(Writer log);
    
    /**
     * Logs the test results to the print stream.
     */
    public void logResult(Writer log, boolean shouldLogOnlyErrors);

    public boolean requiresDatabase();

    public void resetEntity();

    public void resetNestedCounter();

    /**
     * Set the test collection which contains this test
     */
    public void setContainer(TestEntity testEntity);

    public void setNestedCounter(int level);
}
