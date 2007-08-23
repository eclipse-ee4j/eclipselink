/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
/*
   DESCRIPTION
     Test the pluggable implementations for the properties Map on SDODataObject

   PRIVATE CLASSES

   NOTES

   MODIFIED    (MM/DD/YY)
    mfobrien    07/28/06 - Creation
 */

/**
 *  @version $Header: PluggableTestSuite.java 17-aug-2006.11:15:40 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.0
 */
package org.eclipse.persistence.testing.sdo.helper.pluggable;

import junit.framework.Test;
import junit.framework.TestSuite;

public class PluggableTestSuite {
    public PluggableTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
    *  Inherited suite method for generating all test cases.
    */
    public static Test suite() {
        TestSuite suite = new TestSuite("All Pluggable Tests");
        suite.addTest(new TestSuite(PluggableTest.class));
        return suite;
    }
}