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

import commonj.sdo.DataObject;

public class ChangeSummaryInitalizedInCreatingDataObjectTests extends ChangeSummaryTestCases {
    public ChangeSummaryInitalizedInCreatingDataObjectTests(String name) {
        super(name);
    }

    public void testInitalizeChangedDataObjectListAfterLogging() {
        changeSummary.beginLogging();
        this.assertTrue(changeSummary.getChangedDataObjects().isEmpty());
    }

    public void testChangedDataObjectsAfterModifyLogging() {
        changeSummary.beginLogging();
        root.set(rootProperty, null);
        this.assertFalse(changeSummary.getChangedDataObjects().isEmpty());      
        this.assertEquals(2, changeSummary.getChangedDataObjects().size());
        this.assertTrue(changeSummary.getChangedDataObjects().contains(containedDataObject));
        this.assertTrue(changeSummary.getChangedDataObjects().contains(root));
    }

    public void testChangedDataObjectsAfterCreatNewdataObjectLogging() {
        changeSummary.beginLogging();
        this.assertTrue(changeSummary.getChangedDataObjects().isEmpty());
        DataObject o = root.createDataObject(rootProperty);
        this.assertFalse(changeSummary.getChangedDataObjects().isEmpty());
        this.assertTrue(changeSummary.getChangedDataObjects().contains(o));
    }

    public void testCahngedDataObjectAfterDeleteLogging() {
        changeSummary.beginLogging();
        this.assertTrue(changeSummary.getChangedDataObjects().isEmpty());
        containedDataObject.delete();
        this.assertFalse(changeSummary.getChangedDataObjects().isEmpty());
        this.assertTrue(changeSummary.getChangedDataObjects().contains(containedDataObject));
    }

    public void testChangedDataObjectAfterDetachLogging() {
        changeSummary.beginLogging();
        this.assertTrue(changeSummary.getChangedDataObjects().isEmpty());
        DataObject oldContainer = containedDataObject.getContainer();
        containedDataObject.detach();
        this.assertFalse(changeSummary.getChangedDataObjects().isEmpty());
        this.assertEquals(2, changeSummary.getChangedDataObjects().size());
        this.assertTrue(changeSummary.getChangedDataObjects().contains(containedDataObject));
        this.assertTrue(changeSummary.getChangedDataObjects().contains(oldContainer));
    }

    public void testChangedDataObjectBeforeLogging() {
        this.assertTrue(changeSummary.getChangedDataObjects().isEmpty());
    }
}
