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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.errortests.CompositeObjectErrorTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname.CompositeObjectIdentifiedByNameNoRefClassTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname.CompositeObjectIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname.CompositeObjectIdentifiedByNameTextChildKeepElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname.CompositeObjectIdentifiedByNameTextChildTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbynamespace.CompositeObjectIdentifiedByNamespaceTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbynamespace.CompositeObjectNoReferenceClassTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyposition.CompositeObjectIdentifiedByPositionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.keepaselement.CompositeObjectKeepUnknownAsElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nested.CompositeObjectNestedTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectIsSetNodeNullPolicyFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectIsSetNodeNullPolicyTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectIsSetNullPolicyAbsentIsSetAbsentFalseIsSetFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectIsSetNullPolicyAbsentIsSetAbsentFalseIsSetTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectIsSetNullPolicySetEmptyFalseIsSetFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectIsSetNullPolicySetEmptyFalseIsSetTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectIsSetNullPolicySetEmptyTrueIsSetFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectIsSetNullPolicySetEmptyTrueIsSetTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectIsSetNullPolicySetNonNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectIsSetNullPolicySetNonNullTextNodeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectNillableNodeNullPolicyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectNullPolicyAbsentIsSetAbsentFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectNullPolicyAbsentIsSetAbsentTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectNullPolicySetEmptyFalseIsSetFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectNullPolicySetEmptyFalseIsSetTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectNullPolicySetEmptyTrueIsSetFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectNullPolicySetEmptyTrueIsSetTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectNullPolicySetNonNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable.CompositeObjectOptionalNodeNullPolicyElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.norefclass.DefaultNSTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.nulltests.CompositeObjectNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.AttributeListOnTargetTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.AttributeOnTargetTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.AttributesOnTargetTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.CompositeObjectSelfTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.SelfWithOtherCompositeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.defaultnamespace.SelfMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass.CompositeObjectSelfComplexXsiTypeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass.CompositeObjectSelfSimpleXsiTypeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass.SelfNoRefClassKeepAsElementNSTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass.SelfNoRefClassKeepAsElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel.PLSQLCallModelTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.singleelement.CompositeObjectSingleElementTestCases;

public class CompositeObjectMappingTestSuite extends TestCase {
    public static Test suite() {
        String metadataStr = System.getProperty(OXTestCase.METADATA_KEY, OXTestCase.METADATA_JAVA);
        boolean deploymentXML = !(metadataStr.equals(OXTestCase.METADATA_JAVA)); 

        TestSuite suite = new TestSuite("Composite Object Mapping Test Suite");

        suite.addTestSuite(CompositeObjectIdentifiedByNameTestCases.class);
        suite.addTestSuite(CompositeObjectIdentifiedByNameNoRefClassTestCases.class);
        suite.addTestSuite(CompositeObjectIdentifiedByNameTextChildTestCases.class);
        suite.addTestSuite(CompositeObjectIdentifiedByNameTextChildKeepElementTestCases.class);
        suite.addTestSuite(CompositeObjectIdentifiedByNamespaceTestCases.class);
        suite.addTestSuite(CompositeObjectNoReferenceClassTestCases.class);
        suite.addTestSuite(CompositeObjectIdentifiedByPositionTestCases.class);
        suite.addTestSuite(CompositeObjectNestedTestCases.class);
        suite.addTestSuite(CompositeObjectSingleElementTestCases.class);
        suite.addTest(CompositeObjectNullTestCases.suite());
        suite.addTestSuite(CompositeObjectErrorTestCases.class);
        suite.addTestSuite(AttributeOnTargetTestCases.class);
        suite.addTestSuite(AttributesOnTargetTestCases.class);
        suite.addTestSuite(AttributeListOnTargetTestCases.class);
        // Self mapping tests
        suite.addTestSuite(CompositeObjectSelfTestCases.class);
        suite.addTestSuite(SelfMappingTestCases.class);
        // The following self tests are not meant to run with deployment XML
        if (!deploymentXML) {
            suite.addTestSuite(PLSQLCallModelTestCases.class);
        }
        // Null Policy refactor 3
       	suite.addTestSuite(CompositeObjectNullPolicySetEmptyFalseIsSetFalseTestCases.class); 
       	suite.addTestSuite(CompositeObjectNullPolicySetEmptyFalseIsSetTrueTestCases.class); 
       	// UC 9-1 to 9-2 and 11.2
       	suite.addTestSuite(CompositeObjectIsSetNullPolicySetEmptyFalseIsSetFalseTestCases.class); 
       	// UC 9-1 to 9-2 and 11.5
       	suite.addTestSuite(CompositeObjectIsSetNullPolicySetEmptyFalseIsSetTrueTestCases.class);
       	// UC 9-3 to 9-4 and 11.2 - no round trip for unmarshal isset=false
       	suite.addTestSuite(CompositeObjectIsSetNullPolicySetEmptyTrueIsSetFalseTestCases.class);
       	// UC 9-3 to 9-4 and 11.5
       	suite.addTestSuite(CompositeObjectIsSetNullPolicySetEmptyTrueIsSetTrueTestCases.class); 
       	suite.addTestSuite(CompositeObjectNullPolicySetEmptyTrueIsSetFalseTestCases.class); 
       	suite.addTestSuite(CompositeObjectNullPolicySetEmptyTrueIsSetTrueTestCases.class);
       	suite.addTestSuite(CompositeObjectNullPolicySetNonNullTestCases.class);
       	suite.addTestSuite(CompositeObjectNullPolicyAbsentIsSetAbsentFalseTestCases.class);
       	suite.addTestSuite(CompositeObjectNullPolicyAbsentIsSetAbsentTrueTestCases.class);
       	suite.addTestSuite(CompositeObjectIsSetNullPolicySetNonNullTestCases.class);
        suite.addTestSuite(CompositeObjectIsSetNullPolicySetNonNullTextNodeTestCases.class);       	
       	suite.addTestSuite(CompositeObjectIsSetNullPolicyAbsentIsSetAbsentFalseIsSetFalseTestCases.class);
       	suite.addTestSuite(CompositeObjectIsSetNullPolicyAbsentIsSetAbsentFalseIsSetTrueTestCases.class); // check rountrip
       	//suite.addTestSuite(CompositeObjectIsSetNullPolicyAbsentIsSetAbsentTrueTestCases.class); // invalid use case ISPFAN is always false

       	// pre-nillable refactor 3
        suite.addTestSuite(CompositeObjectOptionalNodeNullPolicyElementTestCases.class);
        suite.addTestSuite(CompositeObjectNillableNodeNullPolicyTestCases.class);
        suite.addTestSuite(CompositeObjectIsSetNodeNullPolicyTrueTestCases.class);
        suite.addTestSuite(CompositeObjectIsSetNodeNullPolicyFalseTestCases.class);

        suite.addTestSuite(CompositeObjectKeepUnknownAsElementTestCases.class);
        
        suite.addTestSuite(SelfNoRefClassKeepAsElementTestCases.class);
        suite.addTestSuite(SelfNoRefClassKeepAsElementNSTestCases.class);
        
        suite.addTestSuite(CompositeObjectSelfComplexXsiTypeTestCases.class);
        suite.addTestSuite(CompositeObjectSelfSimpleXsiTypeTestCases.class);
        suite.addTestSuite(SelfWithOtherCompositeTestCases.class);
        suite.addTestSuite(DefaultNSTestCases.class);
        
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
