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
package org.eclipse.persistence.testing.oxm.documentpreservation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *  @version $Header: DocumentPreservationTestSuite.java 06-jun-2005.13:43:48 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class DocumentPreservationTestSuite extends TestCase {
    public DocumentPreservationTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.documentpreservation.DocumentPreservationTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Document Preservation Test Suite");
        suite.addTestSuite(BasicDocumentPreservationTestCases.class);
        suite.addTestSuite(CompositeCollectionTestCases.class);
        suite.addTestSuite(CompositeObjectTestCases.class);
        suite.addTestSuite(DocumentPreservationFragmentTestCases.class);
        return suite;
    }
}
