/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
