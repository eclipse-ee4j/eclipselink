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
package org.eclipse.persistence.testing.oxm.mappings.xmlfragmentcollection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class XMLFragmentCollectionMappingTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("XMLFragment Collection Mapping Test Suite");
        suite.addTestSuite(XMLFragmentCollectionNamespacesTestCases.class);
        suite.addTestSuite(XMLFragmentCollectionNamespaces2TestCases.class);
        suite.addTestSuite(XMLFragmentCollectionNamespaces3TestCases.class);
        
        
        // suite.addTestSuite(XMLFragmentCollectionNSTestCases.class);
        suite.addTestSuite(XMLFragmentCollectionElementTestCases.class);
        suite.addTestSuite(XMLFragmentCollectionElementDiffPrefixTestCases.class);
        suite.addTestSuite(XMLFragmentCollectionElementDiffURITestCases.class);        
        
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.xmlfragmentcollection.XMLFragmentCollectionMappingTestSuite" };
        //System.setProperty("useDeploymentXML", "true");
        //System.setProperty("useDocPres", "true");
        //System.setProperty("useLogging", "true");
        //System.setProperty("useSAXParsing", "true");
        junit.textui.TestRunner.main(arguments);
    }
}
