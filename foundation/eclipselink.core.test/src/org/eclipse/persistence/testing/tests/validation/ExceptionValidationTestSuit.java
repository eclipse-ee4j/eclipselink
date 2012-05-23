/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.validation;

/**
 * This testsuite was added to allow test to be run to validate that the appropriate
 * exceptions are thrown when required
 */
public class ExceptionValidationTestSuit extends org.eclipse.persistence.testing.framework.TestSuite {
    /**
     * ExceptionValidationTestSuit constructor comment.
     */
    public ExceptionValidationTestSuit() {
        super();
        setDescription("This test suite monitors and verifies that the appropriate exceptions are thrown");
        setName("ExceptionValidationTestSuit");
    }

    public void addTests() {
        addTest(new BatchCommitTransactionExceptionTest());
        addTest(new PrintStackTraceTest());
        /// Added Oct 19, 2000 JED
        addTest(new InvalidQueryKeyTest());

        // Added Oct 20, 2000 JED
        addTest(new ReportQueryWithNoAttributesTest());

        addTest(new ChainedExceptionTestCase());

        addTest(new ConversionExceptionFromMappingTest());

        //Bug#3440544 Check if logged in already to stop the attempt to login more than once
        addTest(new DatabaseSessionAttemptLoginTwiceTest());
        addTest(new DatabaseSessionLogoutThenLoginTest());

        addTest(new NestedUOWWithUpdateAllQueryTest());
        addTest(new ExceptionSerializationTestCase());
        
        //Bug6119707
        addTest(new QueryParameterForOneToOneValidationTest(false));
        addTest(new QueryParameterForOneToOneValidationTest(true));
    }
}
