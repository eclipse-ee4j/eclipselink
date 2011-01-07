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
package org.eclipse.persistence.testing.oxm.mappings.anyattribute;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.AnyAttributeNSonRootTestCases;
import org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.AnyAttributeNotInNRTestCases;

public class XMLAnyAttributeMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("XMLAnyAttributeMapping Test Suite");

        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.AnyAttributeMultipleAttributesTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.AnyAttributeNoAttributesTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement.AnyAttributeMultipleAttributesTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement.AnyAttributeNoAttributesTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.AnyAttributeMultipleAttributesNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.AnyAttributeMultipleAttributesNSExcludeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.AnyAttributeNoAttributesNSTestCases.class);
        suite.addTestSuite(AnyAttributeNotInNRTestCases.class);
        suite.addTestSuite(AnyAttributeNSonRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement.AnyAttributeMultipleAttributesNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement.AnyAttributeMultipleAttributesNSExcludeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement.AnyAttributeNoAttributesNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyattribute.usemaptests.AnyAttributeUsingMapTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyattribute.reuse.AnyAttributeReuseTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anyattribute.XMLAnyAttributeMappingTestSuite" };
        //System.setProperty("useDeploymentXML", "true");
        //System.setProperty("useDocPres", "true");
        //System.setProperty("useLogging", "true");
        //System.setProperty("useSAXParsing", "true");
        junit.textui.TestRunner.main(arguments);
    }
}
