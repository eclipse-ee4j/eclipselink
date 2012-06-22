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

/* $Header: XMLBinaryDataCollectionMappingTestSuite.java 11-may-2007.15:17:46 dmccann Exp $ */

/**
 *  @version $Header: XMLBinaryDataCollectionMappingTestSuite.java 11-may-2007.15:17:46 dmccann Exp $
 *  @author  mfobrien
 *  @since  11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement.BinaryDataCollectionByteObjectArrayTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement.BinaryDataCollectionDataHandlerTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement.BinaryDataCollectionForcedInlineBinaryTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement.BinaryDataCollectionWithGroupingElementIdentifiedByNameEmptyNSRTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement.BinaryDataCollectionWithGroupingElementIdentifiedByNameNullNSRTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement.BinaryDataCollectionWithGroupingElementIdentifiedByNameXOPonNSRTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.reuse.BinaryDataCollectionReuseTestCases;

public class XMLBinaryDataCollectionMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("XMLBinaryDataCollectionMapping Test Suite");
        suite.addTestSuite(BinaryDataCollectionWithGroupingElementIdentifiedByNameXOPonNSRTestCases.class);
        // expect: Exception Description: An error occurred trying to resolve the namespace URI for xop:Include. A namespace resolver was not specified on the descriptor.
        suite.addTestSuite(BinaryDataCollectionWithGroupingElementIdentifiedByNameNullNSRTestCases.class);
        // expect: Exception Description: A namespace for the prefix ns0:Include was not found in the namespace resolver.
        suite.addTestSuite(BinaryDataCollectionWithGroupingElementIdentifiedByNameEmptyNSRTestCases.class);
        suite.addTestSuite(BinaryDataCollectionForcedInlineBinaryTestCases.class);
        suite.addTestSuite(BinaryDataCollectionReuseTestCases.class);
        suite.addTestSuite(BinaryDataCollectionByteObjectArrayTestCases.class);
        suite.addTestSuite(BinaryDataCollectionDataHandlerTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.XMLBinaryDataCollectionMappingTestSuite" };
        //System.setProperty("useDeploymentXML", "true");
        //System.setProperty("useDocPres", "true");
        //System.setProperty("useLogging", "true");
        //System.setProperty("useSAXParsing", "true");
        junit.textui.TestRunner.main(arguments);
    }
}
