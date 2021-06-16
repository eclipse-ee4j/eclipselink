/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
