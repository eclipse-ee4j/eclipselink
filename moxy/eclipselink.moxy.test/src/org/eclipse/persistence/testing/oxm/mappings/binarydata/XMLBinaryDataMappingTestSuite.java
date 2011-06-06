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
package org.eclipse.persistence.testing.oxm.mappings.binarydata;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.binarydata.identifiedbyname.BinaryDataByteObjectArrayTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydata.identifiedbyname.BinaryDataIdentifiedByNameEmptyNSRTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydata.identifiedbyname.BinaryDataIdentifiedByNameNullNSRTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydata.identifiedbyname.BinaryDataIdentifiedByNameXOPonNSRTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydata.identifiedbyname.BinaryDataIdentifiedByNameNullTestCases;
public class XMLBinaryDataMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("XMLBinaryDataMapping Test Suite");
        suite.addTestSuite(BinaryDataIdentifiedByNameXOPonNSRTestCases.class);
        // expect: Exception Description: An error occurred trying to resolve the namespace URI for xop:Include. A namespace resolver was not specified on the descriptor.
        suite.addTestSuite(BinaryDataIdentifiedByNameNullNSRTestCases.class);
        // expect: Exception Description: A namespace for the prefix ns0:Include was not found in the namespace resolver.
        suite.addTestSuite(BinaryDataIdentifiedByNameEmptyNSRTestCases.class);
        suite.addTestSuite(BinaryDataIdentifiedByNameNullTestCases.class);
        suite.addTestSuite(BinaryDataEmptyElementTestCases.class);
        suite.addTestSuite(BinaryDataSelfTestCases.class);
        suite.addTestSuite(BinaryDataSelfDataHandlerTestCases.class);
        suite.addTestSuite(BinaryDataByteObjectArrayTestCases.class);
        suite.addTestSuite(BinaryDataCompositeSelfTestCases.class);
        suite.addTestSuite(BinaryDataInlineTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.binarydata.XMLBinaryDataMappingTestSuite" };
        //System.setProperty("useDeploymentXML", "true");
        //System.setProperty("useDocPres", "true");
        //System.setProperty("useLogging", "true");
        //System.setProperty("useSAXParsing", "true");
        junit.textui.TestRunner.main(arguments);
    }
}
