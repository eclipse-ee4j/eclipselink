/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.events;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class OXEventCallbackTestSuite extends TestCase {
    public OXEventCallbackTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.events.OXEventCallbackTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Document Preservation Test Suite");
        suite.addTestSuite(RootWithCompositeObjectTestCases.class);
        suite.addTestSuite(RootWithCompositeCollectionTestCases.class);
        suite.addTestSuite(RootWithAnyObjectTestCases.class);
        suite.addTestSuite(RootWithAnyCollectionTestCases.class);
        return suite;
    }
}