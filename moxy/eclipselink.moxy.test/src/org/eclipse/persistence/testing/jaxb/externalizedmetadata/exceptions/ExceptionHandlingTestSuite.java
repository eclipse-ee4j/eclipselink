/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - March 25/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.exceptions;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.exceptions.contextfactory.ExceptionHandlingTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite for testing externalized metadata exception handling.
 *
 */
public class ExceptionHandlingTestSuite extends TestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite("Exception Handling Test Suite");
        suite.addTestSuite(ExceptionHandlingTestCases.class);
        return suite;
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.externalizedmetadata.exceptions.ExceptionHandlingTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

}