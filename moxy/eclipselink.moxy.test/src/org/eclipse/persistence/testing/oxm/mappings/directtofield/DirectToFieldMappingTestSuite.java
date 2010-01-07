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
package org.eclipse.persistence.testing.oxm.mappings.directtofield;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.OXTestCase.Platform;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.cdata.DirectToFieldCDATATestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.DefaultNullValueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.doubletest.DirectToFieldDoubleNanTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.doubletest.DirectToFieldDoubleNegativeInfinityTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.doubletest.DirectToFieldDoublePositiveInfinityTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.errortests.DirectToFieldErrorTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.floattest.DirectToFieldFloatNanTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.floattest.DirectToFieldFloatNegativeInfinityTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.floattest.DirectToFieldFloatPositiveInfinityTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.IdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbynamespace.IdentifiedByNamespaceTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyposition.IdentifiedByPositionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.leafelement.DirectWithLeafElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNodeNullPolicyFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNodeNullPolicyTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNullPolicyAttributeAbsentIsSetAbsentFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNullPolicyAttributeSetEmptyFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNullPolicyAttributeSetEmptyTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNullPolicyAttributeSetNonNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNullPolicyElementAbsentIsSetAbsentFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNullPolicyElementAbsentIsSetAbsentFalseWithParamsTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNullPolicyElementAbsentIsSetAbsentTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNullPolicyElementSetEmptyFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNullPolicyElementSetEmptyTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNullPolicyElementSetNillableIsSetFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNullPolicyElementSetNillableIsSetTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectIsSetNullPolicyElementSetNonNullTestCases;
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
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectNullPolicyAttributeAbsentIsSetAbsentFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectNullPolicyAttributeAbsentIsSetAbsentTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectNullPolicyAttributeSetEmptyFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectNullPolicyAttributeSetEmptyTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectNullPolicyAttributeSetNillableNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectNullPolicyAttributeSetNonNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectNullPolicyElementAbsentIsSetAbsentFalseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectNullPolicyElementAbsentIsSetAbsentTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectNullPolicyElementSetEmptyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectNullPolicyElementSetNillableTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectNullPolicyElementSetNonNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectOptionalNodeNullPolicyAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable.DirectOptionalNodeNullPolicyElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.schematype.SchemaTypeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.SingleAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.typeattribute.TypeAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.union.UnionTestCases;

public class DirectToFieldMappingTestSuite extends OXTestCase {
	public DirectToFieldMappingTestSuite(String name) {
		super(name);
	}
	
    public static Test suite() {
        TestSuite suite = new TestSuite("Direct to Field Mapping Test Suite");
        if(!System.getProperty(PLATFORM_KEY, PLATFORM_SAX).equals(PLATFORM_DOC_PRES)) { //if (!(platform == Platform.DOC_PRES)) { // platform is null here
        	// The following tests are commented out lower in this suite
            suite.addTestSuite(DirectToFieldCDATATestCases.class); // 1 docpres
        	suite.addTestSuite(DirectNullPolicyElementSetEmptyTestCases.class); // 3 docpres
        	suite.addTestSuite(DirectNullPolicyElementSetNillableTestCases.class); // 3 docpres
        	suite.addTestSuite(DirectIsSetNullPolicyElementSetEmptyTrueTestCases.class); // 3 docpres
        	suite.addTestSuite(DirectIsSetNullPolicyElementSetNillableIsSetTrueTestCases.class); // 3 docpres
        	suite.addTestSuite(DirectNullPolicyAttributeSetEmptyTrueTestCases.class); // TODO: verify UC5-4 convert "" to null - 3 docpres
        	suite.addTestSuite(DirectNullPolicyAttributeSetEmptyFalseTestCases.class); // 3 docpres 
        	suite.addTestSuite(DirectIsSetNullPolicyAttributeSetEmptyTrueTestCases.class); // 3 docpres
        	suite.addTestSuite(DirectNillableNodeNullPolicyTestCases.class);// 3 docpres
        	suite.addTestSuite(DirectIsSetNodeNullPolicyTrueTestCases.class);// 3 docpres
        	suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetDefaultNullTestCases.class); // 3 docpres
        	suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetOtherEmptyTestCases.class); // 3 docpres
        	suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetNullTestCases.class); // 3 docpres
        	suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetOtherEmptyTestCases.class); // 3 docpres
        }
		
        suite.addTest(SingleAttributeTestCases.suite());
        suite.addTest(IdentifiedByNameTestCases.suite());
        suite.addTest(IdentifiedByNamespaceTestCases.suite());
        suite.addTest(IdentifiedByPositionTestCases.suite());
        suite.addTest(TypeAttributeTestCases.suite());
        suite.addTest(SchemaTypeTestCases.suite());
        suite.addTestSuite(DirectToFieldErrorTestCases.class);
        suite.addTest(UnionTestCases.suite());
        suite.addTest(DefaultNullValueTestCases.suite());
        //suite.addTestSuite(DirectToFieldCDATATestCases.class);

		// Null Policy Version 1
        suite.addTestSuite(DirectOptionalNodeNullPolicyElementTestCases.class);
        suite.addTestSuite(DirectOptionalNodeNullPolicyAttributeTestCases.class);
        //suite.addTestSuite(DirectNillableNodeNullPolicyTestCases.class);// 3 docpres
        //suite.addTestSuite(DirectIsSetNodeNullPolicyTrueTestCases.class);// 3 docpres
        suite.addTestSuite(DirectIsSetNodeNullPolicyFalseTestCases.class);
          
        // Null Policy Version 2
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetNonNullTestCases.class);
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetNOPTestCases.class);
        //suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetDefaultNullTestCases.class); // 3 docpres
        //suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementNonDefaultSetOtherEmptyTestCases.class); // 3 docpres

        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetNonNullTestCases.class);
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetNOPTestCases.class);
        //suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetNullTestCases.class); // 3 docpres
        suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetDefaultTestCases.class);
        //suite.addTestSuite(DirectIsSetOptionalNodeNullPolicyNonNillableElementDefaultSetOtherEmptyTestCases.class); // 3 docpres

		// Null Policy Refactor 3: 200709
		suite.addTestSuite(DirectNullPolicyElementSetNonNullTestCases.class); // g
		//suite.addTestSuite(DirectNullPolicyElementSetEmptyTestCases.class); // 3 docpres
		suite.addTestSuite(DirectNullPolicyElementAbsentIsSetAbsentFalseTestCases.class); // g
		suite.addTestSuite(DirectNullPolicyElementAbsentIsSetAbsentTrueTestCases.class); // g
		//suite.addTestSuite(DirectNullPolicyElementSetNillableTestCases.class); // 3 docpres
		suite.addTestSuite(DirectIsSetNullPolicyElementSetNonNullTestCases.class); // g - isSet collectionClass
		suite.addTestSuite(DirectIsSetNullPolicyElementSetEmptyFalseTestCases.class); // g
		//suite.addTestSuite(DirectIsSetNullPolicyElementSetEmptyTrueTestCases.class); // 3 docpres
		suite.addTestSuite(DirectIsSetNullPolicyElementAbsentIsSetAbsentFalseTestCases.class); // g
		suite.addTestSuite(DirectIsSetNullPolicyElementAbsentIsSetAbsentTrueTestCases.class); // g
		suite.addTestSuite(DirectIsSetNullPolicyElementAbsentIsSetAbsentFalseWithParamsTestCases.class); // g		
		suite.addTestSuite(DirectIsSetNullPolicyElementSetNillableIsSetFalseTestCases.class); // no xmlns:xsi
		//suite.addTestSuite(DirectIsSetNullPolicyElementSetNillableIsSetTrueTestCases.class); // 3 docpres
		suite.addTestSuite(DirectNullPolicyAttributeSetNillableNullTestCases.class); // g
		suite.addTestSuite(DirectNullPolicyAttributeSetNonNullTestCases.class); // g
		//suite.addTestSuite(DirectNullPolicyAttributeSetEmptyFalseTestCases.class); // 3 docpres 
		//suite.addTestSuite(DirectNullPolicyAttributeSetEmptyTrueTestCases.class); // TODO: verify UC5-4 convert "" to null - 3 docpres
		suite.addTestSuite(DirectNullPolicyAttributeAbsentIsSetAbsentFalseTestCases.class); // g
		suite.addTestSuite(DirectNullPolicyAttributeAbsentIsSetAbsentTrueTestCases.class); // g
		suite.addTestSuite(DirectIsSetNullPolicyAttributeSetNonNullTestCases.class); // g
		suite.addTestSuite(DirectIsSetNullPolicyAttributeSetEmptyFalseTestCases.class); // g
		//suite.addTestSuite(DirectIsSetNullPolicyAttributeSetEmptyTrueTestCases.class); // 3 docpres
		suite.addTestSuite(DirectIsSetNullPolicyAttributeAbsentIsSetAbsentFalseTestCases.class); // g
		//suite.addTestSuite(DirectIsSetNullPolicyAttributeAbsentIsSetAbsentTrueTestCases.class);  // TODO: UC 5-9 Is not valid

		suite.addTestSuite(DirectWithLeafElementTestCases.class);
		
		suite.addTestSuite(DirectToFieldDoubleNanTestCases.class);
		suite.addTestSuite(DirectToFieldDoubleNegativeInfinityTestCases.class);
		suite.addTestSuite(DirectToFieldDoublePositiveInfinityTestCases.class);

		suite.addTestSuite(DirectToFieldFloatNanTestCases.class);
		suite.addTestSuite(DirectToFieldFloatNegativeInfinityTestCases.class);
		suite.addTestSuite(DirectToFieldFloatPositiveInfinityTestCases.class);
		return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directtofield.DirectToFieldMappingTestSuite" };
        //platform = Platform.DOM;
        //platform = Platform.DOC_PRES;
        //platform = Platform.SAX;
        junit.textui.TestRunner.main(arguments);
    }
}
