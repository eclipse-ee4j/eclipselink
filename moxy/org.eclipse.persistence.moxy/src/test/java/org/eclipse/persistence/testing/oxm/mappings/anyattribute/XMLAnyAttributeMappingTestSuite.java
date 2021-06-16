/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
