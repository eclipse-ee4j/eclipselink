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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import junit.framework.Test;
import junit.framework.TestSuite;

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
        suite.addTest(new TestSuite(LoadAndSaveImportsElementOrderTestCases.class));
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
        suite.addTest(new TestSuite(LoadAndSaveBase64AttachmentTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveDataHandlerTestCases.class));
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
        suite.addTest(new TestSuite(LoadAndSaveNillableOptionalNodeNullPolicyTestCases.class));        
        suite.addTest(new TestSuite(LoadAndSaveNillableIsSetNodeNullPolicyTrueTestCases.class));        
        suite.addTest(new TestSuite(LoadAndSaveNillableIsSetNodeNullPolicyFalseTestCases.class));        

        // substitution groups
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.substitutiongroups.SingleValueBaseTypeTestCases.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.substitutiongroups.SingleValueNonBaseTypeTestCases.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.substitutiongroups.CollectionValueTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveImportsWithInheritanceTestCases.class));
        
        //read-only
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveWithReadOnlyTestCases.class));

        // nillable
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.nillable.ListPropertyNillableElementTestCases.class));

        // mixed text
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.mixed.LoadAndSaveMixedContentTestCases.class));

        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.staticclasses.LoadAndSaveStaticClassesTestCases.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveIncludeWithExtensionTestCases.class));
        suite.addTestSuite(GlobalAttributeTestCases.class);
        return suite;
    }
}
