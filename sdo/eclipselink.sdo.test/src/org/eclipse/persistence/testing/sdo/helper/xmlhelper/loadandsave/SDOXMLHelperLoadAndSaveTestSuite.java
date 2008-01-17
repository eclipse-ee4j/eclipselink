/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetNillableOptionalWithDefaultSetNullTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetNillableWithDefaultSetDefaultTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetNillableWithDefaultSetNOPTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetNillableWithDefaultSetNonNullTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetNillableWithDefaultSetNullTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetNillableWithoutDefaultSetNOPTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetNillableWithoutDefaultSetNonNullTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetNillableWithoutDefaultSetNullTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetOptionalAttributeWithoutDefaultSetNOPTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetOptionalWithDefaultSetDefaultTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetOptionalWithDefaultSetNOPTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetOptionalWithDefaultSetNonNullTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetOptionalWithDefaultSetNullTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetOptionalWithoutDefaultSetNOPNumericPrimsTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetOptionalWithoutDefaultSetNOPTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetOptionalWithoutDefaultSetNonNullTestCases;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nodenullpolicy.IsSetOptionalWithoutDefaultSetNullTestCases;

public class SDOXMLHelperLoadAndSaveTestSuite {
    public SDOXMLHelperLoadAndSaveTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All XMLHelper Tests");
        suite.addTest(new TestSuite(LoadAndSavePurchaseOrderWChangeSummaryTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveImportsDefaultNamespaceTestCases.class));
        // one expected failure 
        suite.addTest(new TestSuite(LoadAndSaveWithImportsTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveIDRefTestCases.class));
        suite.addTest(new TestSuite(LoadAndSavePurchaseOrderComplexTestCases.class));
        suite.addTest(new TestSuite(LoadAndSavePurchaseOrderComplexDefaultNSTestCases.class));
        suite.addTest(new TestSuite(LoadAndSavePurchaseOrderTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveSimpleAttributeTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveSimpleElementTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveSchemaTypesTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveOpenContentTestCases.class));
        // TODO: fix position(state) dependent error on testLoadFromDomSourceWithURIAndOptionsSaveDataObjectToStreamResult Failure expected:<...tru...> but was:<...fals...> 
        //suite.addTest(new TestSuite(LoadAndSavePurchaseOrderWChangeSummaryTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveMimeTypeOnXSDTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveMimeTypeOnPropertyTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveMimeTypeOnXSDManyTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveMimeTypeOnPropertyManyTestCases.class));
        suite.addTest(new TestSuite(LoadAndSavePurchaseOrderWithAnnotations.class));
        suite.addTest(new TestSuite(LoadAndSaveOrderBookingTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveXMLEncodingAndVersionTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveWithDataObjectDataTypeTestCases.class));


        suite.addTest(new TestSuite(LoadAndSaveValuePropTestCases.class));        
        suite.addTest(new TestSuite(LoadAndSaveWithDefaultsTestCases.class));        
        suite.addTest(new TestSuite(LoadAndSaveInheritanceBug6043501TestCases.class));        
        suite.addTest(new TestSuite(LoadAndSaveNamespacesBugTestCases.class));  
        suite.addTest(new TestSuite(LoadAndSaveBug6130541TestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveAttributeGroupTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveGroupTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveWithTypeBug6522867TestCases.class));

        // test DirectMapping        
        suite.addTest(new TestSuite(IsSetNillableOptionalWithDefaultSetNullTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalAttributeWithoutDefaultSetNOPTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveNillableOptionalNodeNullPolicyTestCases.class));        
        // test CompositeObjectMapping
        suite.addTest(new TestSuite(LoadAndSaveNillableIsSetNodeNullPolicyTrueTestCases.class));        
        suite.addTest(new TestSuite(LoadAndSaveNillableIsSetNodeNullPolicyFalseTestCases.class));        

        // UC 1        
        suite.addTest(new TestSuite(IsSetOptionalWithoutDefaultSetNonNullTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithoutDefaultSetNOPTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithoutDefaultSetNullTestCases.class));        
        // UC 2
        suite.addTest(new TestSuite(IsSetNillableWithoutDefaultSetNonNullTestCases.class));
        suite.addTest(new TestSuite(IsSetNillableWithoutDefaultSetNOPTestCases.class));
        suite.addTest(new TestSuite(IsSetNillableWithoutDefaultSetNullTestCases.class));
        // UC 3
        suite.addTest(new TestSuite(IsSetOptionalWithDefaultSetNonNullTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithDefaultSetNOPTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithDefaultSetNullTestCases.class));
        suite.addTest(new TestSuite(IsSetOptionalWithDefaultSetDefaultTestCases.class));
        // UC 4
        suite.addTest(new TestSuite(IsSetNillableWithDefaultSetNonNullTestCases.class));
        suite.addTest(new TestSuite(IsSetNillableWithDefaultSetNOPTestCases.class));
        suite.addTest(new TestSuite(IsSetNillableWithDefaultSetNullTestCases.class));
        suite.addTest(new TestSuite(IsSetNillableWithDefaultSetDefaultTestCases.class));
        // all numeric primitive defaults
        suite.addTest(new TestSuite(IsSetOptionalWithoutDefaultSetNOPNumericPrimsTestCases.class));
        
        // substitution groups
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.substitutiongroups.SingleValueBaseTypeTestCases.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.substitutiongroups.SingleValueNonBaseTypeTestCases.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.substitutiongroups.CollectionValueTestCases.class));
        return suite;
    }
}
