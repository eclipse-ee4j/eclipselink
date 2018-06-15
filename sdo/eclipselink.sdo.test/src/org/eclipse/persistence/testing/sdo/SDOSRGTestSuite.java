/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dtwelves - Sept 2008 Create SDO SRG test suite
package org.eclipse.persistence.testing.sdo;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SDOSRGTestSuite {
    public SDOSRGTestSuite() {
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All SDO SRG Tests");
        suite.addTest((new org.eclipse.persistence.testing.sdo.helper.datafactory.SDODataFactoryTestSuite()).suite());
        suite.addTest((new org.eclipse.persistence.testing.sdo.helper.xsdhelper.define.attributes.SDOAttributeXSDTestSuite()).suite());
        suite.addTest((new org.eclipse.persistence.testing.sdo.model.dataobject.containment.ContainmentTestSuite()).suite());
        suite.addTest((new org.eclipse.persistence.testing.sdo.model.type.SDOTypeTestSuite()).suite());

        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectSetGetWithPropertyTest.class));
/*        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectSetGetWithIndexTest.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetPathTest.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectListWrapperTest.class)); */

        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryGetOldValueTest.class));
/*        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryIsCreatedIsDeletedIsModifiedTest.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryGetChangedDataObjectsTest.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryCopyTestCases.class));
        suite.addTest(new TestSuite(org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryBeginLoggingEndLoggingCombiningTests.class)); */



        return suite;
    }
}
