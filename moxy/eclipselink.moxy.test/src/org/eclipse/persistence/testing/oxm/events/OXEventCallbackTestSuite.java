/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.events;

import org.eclipse.persistence.testing.oxm.events.descriptor.PostBuildEventTestCases;

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
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.events.OXEventCallbackTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Document Preservation Test Suite");
        suite.addTestSuite(RootWithCompositeObjectTestCases.class);
        suite.addTestSuite(RootWithCompositeCollectionTestCases.class);
        suite.addTestSuite(RootWithAnyObjectTestCases.class);
        suite.addTestSuite(RootWithAnyCollectionTestCases.class);
        suite.addTestSuite(PostBuildEventTestCases.class);
        return suite;
    }
}
