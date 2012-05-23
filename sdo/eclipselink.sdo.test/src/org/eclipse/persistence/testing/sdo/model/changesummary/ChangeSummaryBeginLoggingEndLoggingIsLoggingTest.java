/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import commonj.sdo.ChangeSummary;
import commonj.sdo.Property;

public class ChangeSummaryBeginLoggingEndLoggingIsLoggingTest extends ChangeSummaryTestCases {
    public ChangeSummaryBeginLoggingEndLoggingIsLoggingTest(String name) {
        super(name);
    }

    // purpose: test if beginLogging initalize logging status to true and changedDataObject
    // list to empty.
    public void testBeginLoggingIsLogging() {
        assertFalse(changeSummary.isLogging());
        changeSummary.beginLogging();
        assertTrue(changeSummary.isLogging());//set status
        assertTrue(changeSummary.getChangedDataObjects().isEmpty());// clear list
        testOldContainerContainmentPropertyOldSettingListAfterBeginLogging(root);// set old container and old containment property       
    }

    /**
    * recursively check if old containers and old containment properties are set properly;
    * @param o    the DataObject to be checked
    */
    private void testOldContainerContainmentPropertyOldSettingListAfterBeginLogging(SDODataObject o) {

        /*
         * 20060710
         * This testcase has been modified because the getOld* functions have moved to their
         * changeSummary counterparts and may not directly return the actual instance variable
         * when not in deleted | modified state
         */
        SDODataObject anOldContainer = (SDODataObject)((SDOChangeSummary)o.getChangeSummary()).getOldContainer(o);
        SDODataObject aContainer = (SDODataObject)o.getContainer();
        if (o.getChangeSummary().isModified(o) || o.getChangeSummary().isDeleted(o)) {
        
            assertEquals(anOldContainer, aContainer);
            if (aContainer != null) {
                Property pp = o.getContainer().getInstanceProperty(o._getContainmentPropertyName());
                assertEquals(((SDOChangeSummary)o.getChangeSummary()).getOldContainmentProperty(o), pp);
            }
        }

        assertNotNull(((SDOChangeSummary)o.getChangeSummary()).getOldValues(o));
        assertTrue(((SDOChangeSummary)o.getChangeSummary()).getOldValues(o).isEmpty());
        List properties = o.getInstanceProperties();
        Iterator iterProperties = properties.iterator();
        while (iterProperties.hasNext()) {
            Property p = (Property)iterProperties.next();
            Object v = o.get(p);
            if (v instanceof SDODataObject) {
                testOldContainerContainmentPropertyOldSettingListAfterBeginLogging(((SDODataObject)v));
            }
        }
    }

    /*
        // purpose: test id endLogging set logging status to false.
        public void testEndLoggingIsLogging() {
            changeSum.beginLogging();
            this.assertTrue(changeSum.isLogging());
            changeSum.endLogging();
            this.assertFalse(changeSum.isLogging());

        }*/

    //purpose: logging twice, oldsetting list should not be affected.    
    public void testBeginLoggingTwice() {
        root.set(rootProperty1, "test");
        changeSummary.beginLogging();
        root.set(rootProperty1, "test1");
        ChangeSummary.Setting setting0 = changeSummary.getOldValue(root, rootProperty1);
        changeSummary.beginLogging();
        ChangeSummary.Setting setting1 = changeSummary.getOldValue(root, rootProperty1);
        assertEquals(setting0.getValue(), setting1.getValue());
    }
}
