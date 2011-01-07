/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import commonj.sdo.DataObject;

public class ChangeSummaryGetChangedDataObjectsTest extends ChangeSummaryTestCases {
    public ChangeSummaryGetChangedDataObjectsTest(String name) {
        super(name);
    }

    public void testPublicResetChanges() {
        changeSummary.beginLogging();
        assertEquals(0, changeSummary.getChangedDataObjects().size());
        assertEquals(0, changeSummary.getOldValues(root).size());
        assertNull(changeSummary.getOldContainer(root));
        assertNotNull(changeSummary.getOldContainer(containedDataObject));
    }

    public void testInitalizeChangedDataObjectListAfterLogging() {
        changeSummary.beginLogging();
        assertTrue(changeSummary.getChangedDataObjects().isEmpty());
        // 20060710 following added after removal of getOld*() from DataObject
        assertEquals(0, changeSummary.getOldValues(root).size());
        assertNull(changeSummary.getOldContainer(root));
//Dec 18 changed from Null to NotNull because oldcontainers are populated recursivly as soon as logging is turned on.
        assertNotNull(changeSummary.getOldContainer(containedDataObject));
    }

    public void testChangedDataObjectsAfterModifyLogging() {
        changeSummary.beginLogging();
        root.set(rootProperty, null);
        assertFalse(changeSummary.getChangedDataObjects().isEmpty());
        assertEquals(2, changeSummary.getChangedDataObjects().size());
        assertTrue(changeSummary.isDeleted(containedDataObject));
        assertTrue(changeSummary.isModified(root));
    }

    public void testChangedDataObjectsAfterCreateNewdataObjectLogging() {
        changeSummary.beginLogging();
        assertTrue(changeSummary.getChangedDataObjects().isEmpty());
        DataObject o = root.createDataObject(rootProperty);
        assertFalse(changeSummary.getChangedDataObjects().isEmpty());
        assertTrue(changeSummary.isCreated(o));
    }

    public void testChangedDataObjectAfterDeleteLogging() {
        changeSummary.beginLogging();
        assertTrue(changeSummary.getChangedDataObjects().isEmpty());
        containedDataObject.delete();
        assertFalse(changeSummary.getChangedDataObjects().isEmpty());
        assertTrue(changeSummary.isDeleted(containedDataObject));
    }

    public void testChangedDataObjectAfterDetachLogging() {
        changeSummary.beginLogging();
        assertTrue(changeSummary.getChangedDataObjects().isEmpty());
        DataObject oldContainer = containedDataObject.getContainer();
        containedDataObject.detach();
        assertFalse(changeSummary.getChangedDataObjects().isEmpty());
        assertEquals(2, changeSummary.getChangedDataObjects().size());
        // TODO: this test fails for now until SDODataObject.removeContainment() is updated
        assertTrue(changeSummary.isDeleted(containedDataObject));
        assertTrue(changeSummary.isModified(oldContainer));
    }

    public void testChangedDataObjectBeforeLogging() {
        assertTrue(changeSummary.getChangedDataObjects().isEmpty());
    }
}
