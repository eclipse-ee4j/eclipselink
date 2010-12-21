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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


/**
 *  Test to ensure that our use of the JDK 1.4 chained exception feature works.
 *  Exception cause should be the same as our internal exception.
 *  In this test we construct several exceptions and ensure that the internal exception
 *  matches the exception cause.
 */
public class ChainedExceptionTestCase extends AutoVerifyTestCase {

    private Exception internalException = null;
    private IllegalAccessException internalIllegalAccessException = null;

    private DatabaseException databaseException = null;
    private DescriptorException descriptorException = null;
    private QueryException queryException = null;
    private ValidationException validationException = null;

    public ChainedExceptionTestCase() {
        setDescription("This test ensures toplink uses the correct API for JDK 1.4 chained exceptions.");
    }

    public void setup() {
        internalException = new Exception("Test Exception");
        internalIllegalAccessException = new IllegalAccessException("Test Exception");
    }

    public void test() {
        databaseException = DatabaseException.configurationErrorNewInstanceIllegalAccessException(internalIllegalAccessException, java.lang.Object.class);
        descriptorException = DescriptorException.couldNotInstantiateIndirectContainerClass(java.lang.Object.class, internalException);
        queryException = QueryException.cannotAddElement(new Object(), new Object(), internalException);
        validationException = ValidationException.ejbInvalidProjectClass("projectClassName", "projectName", internalException);
    }

    public void verify() {

        if ((databaseException.getInternalException() != databaseException.getCause()) || (databaseException.getCause() != internalIllegalAccessException)) {
            throw new TestErrorException("JDK 1.4 Exception Chaining does not work correctly for DatabaseException.");
        }

        if ((descriptorException.getInternalException() != descriptorException.getCause()) || (descriptorException.getCause() != internalException)) {
            throw new TestErrorException("JDK 1.4 Exception Chaining does not work correctly for DescriptorException.");
        }

        if ((queryException.getInternalException() != queryException.getCause()) || (queryException.getCause() != internalException)) {
            throw new TestErrorException("JDK 1.4 Exception Chaining does not work correctly for QueryException.");
        }

        if ((validationException.getInternalException() != validationException.getCause()) || (validationException.getCause() != internalException)) {
            throw new TestErrorException("JDK 1.4 Exception Chaining does not work correctly for ValidationException.");
        }
    }
}
