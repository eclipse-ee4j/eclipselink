/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;

public class ChangeSummaryGetOldContainerContainmentPropertyTest extends ChangeSummaryTestCases {
    public ChangeSummaryGetOldContainerContainmentPropertyTest(String name) {
        super(name);
    }

     public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryGetOldContainerContainmentPropertyTest" };
        TestRunner.main(arguments);
    }

    // purpose: test if containedObject's container is changed, changesummary can still
    // return the former container
    public void testContainerValueAfterDataObjectChangeContainer() {
        SDODataObject container = containedDataObject.getContainer();
        changeSummary.beginLogging();
        SDODataObject o = null;
        root.set(rootProperty, o);
        assertNull(containedDataObject.getContainer());
        assertEquals(root, changeSummary.getOldContainer(containedDataObject));
    }

    // purpose: test null value as parameter
    public void testContainerValueByPassingNull() {
        assertNull(changeSummary.getOldContainer(null));
    }

    // purpose: test pass in dataobjec not in the tree as parameter
    public void testContainerValueByPassingNotExistedDataObject() {
        SDODataObject obj = new SDODataObject();
        assertNull(changeSummary.getOldContainer(obj));
    }

    // purpose: test if containedObject's containment property is changed, changesummary can still
    // return the former value back
    public void testContainmentPropertyValueAfterDataObjectChangeValue() {
        SDOProperty containmentProperty = containedDataObject.getContainmentProperty();
        changeSummary.beginLogging();
        assertTrue(rootProperty.equals(containedDataObject.getContainmentProperty()));// it is the same before modified
        Object o = null;
        root.set(rootProperty, o);
        assertFalse(rootProperty.equals(containedDataObject.getContainmentProperty()));// current container in root's child should be changed
        assertEquals(containmentProperty, changeSummary.getOldContainmentProperty(containedDataObject));
    }

    // purpose: test null value as parameter
    public void testContainmentPropertyByPassingNull() {
        SDODataObject o = null;
        assertNull(changeSummary.getOldContainmentProperty(o));
    }

    // purpose: test pass in dataobjec not in the tree as parameter
    public void testContainmentPropertyByPassingNotExistedDataObject() {
        SDODataObject obj = new SDODataObject();
        assertNull(changeSummary.getOldContainmentProperty(obj));
    }
}
