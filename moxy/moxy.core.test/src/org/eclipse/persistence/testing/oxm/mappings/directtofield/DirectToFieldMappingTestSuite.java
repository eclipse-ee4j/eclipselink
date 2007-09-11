/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.directtofield;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.cdata.DirectToFieldCDATATestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.DefaultNullValueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.errortests.DirectToFieldErrorTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.IdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbynamespace.IdentifiedByNamespaceTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyposition.IdentifiedByPositionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNodeNullPolicyFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNodeNullPolicyTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetDefaultTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetNOPTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetNonNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetOtherEmptyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetDefaultNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetNOPTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetNonNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetOtherEmptyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectNillableNodeNullPolicyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectOptionalNodeNullPolicyAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectOptionalNodeNullPolicyElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.schematype.SchemaTypeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.SingleAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.typeattribute.TypeAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.union.UnionTestCases;

public class DirectToFieldMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Direct to Field Mapping Test Suite");
        suite.addTest(SingleAttributeTestCases.suite());
        suite.addTest(IdentifiedByNameTestCases.suite());
        suite.addTest(IdentifiedByNamespaceTestCases.suite());
        suite.addTest(IdentifiedByPositionTestCases.suite());
        suite.addTest(TypeAttributeTestCases.suite());
        suite.addTest(SchemaTypeTestCases.suite());
        suite.addTestSuite(DirectToFieldErrorTestCases.class);
        suite.addTest(UnionTestCases.suite());
        suite.addTest(DefaultNullValueTestCases.suite());
        suite.addTestSuite(DirectToFieldCDATATestCases.class);
        if(!Boolean.getBoolean("useDeploymentXML") && (!Boolean.getBoolean("useDocPres"))){
        
          // NodeNullPolicy test cases see: TopLinkOXM-Nillable4_blaise.doc
          // see: TopLink_OX_Nillable_TestDesignSpec_v20061026.doc
          // UC2a
          suite.addTestSuite(DirectOptionalNodeNullPolicyElementTestCases.class);// 6 of 6 pass by default
          // UC2b
          suite.addTestSuite(DirectOptionalNodeNullPolicyAttributeTestCases.class);// 6 of 6 pass by default
          // UC3
          suite.addTestSuite(DirectNillableNodeNullPolicyTestCases.class);// 3 of 6 pass until implementation set
          // UC4-true
          suite.addTestSuite(DirectIsSetNodeNullPolicyTrueTestCases.class);// 3 of 6 pass until implementation set
          // UC4-false
          suite.addTestSuite(DirectIsSetNodeNullPolicyFalseTestCases.class);// 6 of 6 pass by default
          
        
        
  
        // UC1
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetNonNullTestCases.class);
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetNOPTestCases.class);
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetDefaultNullTestCases.class);
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetOtherEmptyTestCases.class);

        // UC3
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetNonNullTestCases.class);
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetNOPTestCases.class);
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetNullTestCases.class);
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetDefaultTestCases.class);
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetOtherEmptyTestCases.class);
        }
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directtofield.DirectToFieldMappingTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}