/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.model.changesummary;

import commonj.sdo.DataObject;
import junit.framework.TestCase;

public class ChangeSummaryInitalizedInCreatingDataObjectTests extends ChangeSummaryTestCases {
    public ChangeSummaryInitalizedInCreatingDataObjectTests(String name) {
        super(name);
    }

    public void testInitalizeChangedDataObjectListAfterLogging() {
        changeSummary.beginLogging();
        assertTrue(changeSummary.getChangedDataObjects().isEmpty());
    }

    public void testChangedDataObjectsAfterModifyLogging() {
        changeSummary.beginLogging();
        root.set(rootProperty, null);
        assertFalse(changeSummary.getChangedDataObjects().isEmpty());
        assertEquals(2, changeSummary.getChangedDataObjects().size());
        assertTrue(changeSummary.getChangedDataObjects().contains(containedDataObject));
        assertTrue(changeSummary.getChangedDataObjects().contains(root));
    }

    public void testChangedDataObjectsAfterCreatNewdataObjectLogging() {
        changeSummary.beginLogging();
        assertTrue(changeSummary.getChangedDataObjects().isEmpty());
        DataObject o = root.createDataObject(rootProperty);
        assertFalse(changeSummary.getChangedDataObjects().isEmpty());
        assertTrue(changeSummary.getChangedDataObjects().contains(o));
    }

    public void testCahngedDataObjectAfterDeleteLogging() {
        changeSummary.beginLogging();
        assertTrue(changeSummary.getChangedDataObjects().isEmpty());
        containedDataObject.delete();
        assertFalse(changeSummary.getChangedDataObjects().isEmpty());
        assertTrue(changeSummary.getChangedDataObjects().contains(containedDataObject));
    }

    public void testChangedDataObjectAfterDetachLogging() {
        changeSummary.beginLogging();
        assertTrue(changeSummary.getChangedDataObjects().isEmpty());
        DataObject oldContainer = containedDataObject.getContainer();
        containedDataObject.detach();
        assertFalse(changeSummary.getChangedDataObjects().isEmpty());
        assertEquals(2, changeSummary.getChangedDataObjects().size());
        assertTrue(changeSummary.getChangedDataObjects().contains(containedDataObject));
        assertTrue(changeSummary.getChangedDataObjects().contains(oldContainer));
    }

    public void testChangedDataObjectBeforeLogging() {
        assertTrue(changeSummary.getChangedDataObjects().isEmpty());
    }
}
