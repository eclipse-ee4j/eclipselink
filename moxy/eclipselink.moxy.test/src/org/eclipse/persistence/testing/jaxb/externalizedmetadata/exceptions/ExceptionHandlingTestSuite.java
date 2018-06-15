/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - March 25/2010 - 2.1 - Initial implementation
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
