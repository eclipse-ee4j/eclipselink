/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.model.dataobject.containment;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

public class DeleteTestCases extends ContainmentTestCases {
    public DeleteTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        rootDataObject.set("name", "mySampleRootName");

        firstChildDataObject.set("name", "myChildAddress");
        rootDataObject.set("child", firstChildDataObject);
    }

    public void testDeleteChildDataObject() {
        firstChildDataObject.delete();
        for (int i = 0; i < firstChildDataObject.getInstanceProperties().size(); i++) {
            Property nextProp = (Property)firstChildDataObject.getInstanceProperties().get(i);
            assertFalse(firstChildDataObject.isSet(nextProp));
        }
        assertNull(firstChildDataObject.getContainer());
        assertNull(firstChildDataObject.getContainmentProperty());

        assertNull(rootDataObject.get("child"));
    }

    public void testDeleteParentDataObject() {
        rootDataObject.delete();
        assertNull(rootDataObject.getContainer());
        assertNull(rootDataObject.getContainmentProperty());
        verifyAllUnset(rootDataObject);

    }

    public void testDeleteDataObjectWithNestedChildren() {
        rootDataObject.set("name", "myRoot");
        rootDataObject.set("child", firstChildDataObject);
        firstChildDataObject.set("child", secondChildDataObject);
        rootDataObject.delete();
        assertNull(rootDataObject.getContainer());
        assertNull(rootDataObject.getContainmentProperty());
        verifyAllUnset(rootDataObject);
        verifyAllUnset(firstChildDataObject);
        verifyAllUnset(secondChildDataObject);
    }

    public void verifyAllUnset(DataObject dataObject) {
        for (int i = 0; i < dataObject.getInstanceProperties().size(); i++) {
            Property nextProp = (Property)dataObject.getInstanceProperties().get(i);
            assertFalse(dataObject.isSet(nextProp));
        }
    }
}
