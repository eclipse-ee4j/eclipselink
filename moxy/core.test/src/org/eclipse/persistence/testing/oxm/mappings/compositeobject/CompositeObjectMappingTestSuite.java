/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositeobject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.errortests.CompositeObjectErrorTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname.CompositeObjectIdentifiedByNameNoRefClassTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname.CompositeObjectIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbynamespace.CompositeObjectIdentifiedByNamespaceTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbynamespace.CompositeObjectNoReferenceClassTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyposition.CompositeObjectIdentifiedByPositionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nested.CompositeObjectNestedTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectIsSetNodeNullPolicyFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectIsSetNodeNullPolicyTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectNillableNodeNullPolicyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectOptionalNodeNullPolicyElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nulltests.CompositeObjectNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.AttributeListOnTargetTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.AttributeOnTargetTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.AttributesOnTargetTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.CompositeObjectSelfTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.singleelement.CompositeObjectSingleElementTestCases;

public class CompositeObjectMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Composite Object Mapping Test Suite");

        suite.addTestSuite(CompositeObjectIdentifiedByNameTestCases.class);
        suite.addTestSuite(CompositeObjectIdentifiedByNameNoRefClassTestCases.class);
        suite.addTestSuite(CompositeObjectIdentifiedByNamespaceTestCases.class);
        suite.addTestSuite(CompositeObjectNoReferenceClassTestCases.class);
        suite.addTestSuite(CompositeObjectIdentifiedByPositionTestCases.class);
        suite.addTestSuite(CompositeObjectNestedTestCases.class);
        suite.addTestSuite(CompositeObjectSingleElementTestCases.class);
        suite.addTest(CompositeObjectNullTestCases.suite());
        suite.addTestSuite(CompositeObjectErrorTestCases.class);
        suite.addTestSuite(CompositeObjectSelfTestCases.class);
        suite.addTestSuite(AttributeOnTargetTestCases.class);
        suite.addTestSuite(AttributesOnTargetTestCases.class);
        suite.addTestSuite(AttributeListOnTargetTestCases.class);

        // NodeNullPolicy test cases see: TopLinkOXM-Nillable4_blaise.doc
        // see: TopLink_OX_Nillable_TestDesignSpec_v20061026.doc
        // UC2a
                               
        if(!Boolean.getBoolean("useDeploymentXML")) {
          suite.addTestSuite(CompositeObjectOptionalNodeNullPolicyElementTestCases.class);// 6 of 6 pass by default
          // UC2b - not applicable
          //suite.addTestSuite(CompositeObjectOptionalNodeNullPolicyAttributeTestCases.class);// 6 of 6 pass by default
          // UC3
          suite.addTestSuite(CompositeObjectNillableNodeNullPolicyTestCases.class);// 3 of 6 pass until implementation set
          // UC4-true
          suite.addTestSuite(CompositeObjectIsSetNodeNullPolicyTrueTestCases.class);// 0 of 6 pass until implementation set
          // UC4-false
          suite.addTestSuite(CompositeObjectIsSetNodeNullPolicyFalseTestCases.class);// 6 of 6 pass by default
        }

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositeobject.CompositeObjectMappingTestSuite" };
        //System.setProperty("useDeploymentXML", "true");
        //System.setProperty("useDocPres", "true");
        //System.setProperty("useLogging", "true");
        //System.setProperty("useSAXParsing", "true");
        junit.textui.TestRunner.main(arguments);
    }

    public String getName() {
        String nameSoFar = super.getName();
        return nameSoFar + Boolean.getBoolean("useDeploymentXML");
    }
}