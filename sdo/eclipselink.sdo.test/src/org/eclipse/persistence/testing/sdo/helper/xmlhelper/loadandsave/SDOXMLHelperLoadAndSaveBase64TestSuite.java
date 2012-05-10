/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOXMLHelperLoadAndSaveBase64TestSuite {
    public SDOXMLHelperLoadAndSaveBase64TestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("All XMLHelper Tests");

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
        
        // substitution groups
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.substitutiongroups.SingleValueBaseTypeTestCases.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.substitutiongroups.SingleValueNonBaseTypeTestCases.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.substitutiongroups.CollectionValueTestCases.class));
        suite.addTest(new TestSuite(LoadAndSaveImportsWithInheritanceTestCases.class));
        
        
        return suite;
    }    
}