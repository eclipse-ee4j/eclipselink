/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.model.dataobject.containment;

public class DetachTestCases extends ContainmentTestCases {
    public DetachTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        rootDataObject.set("name", "mySampleRootName");

        firstChildDataObject.set("name", "myChildName");

        rootDataObject.set("child", firstChildDataObject);
    }

    public void testDetachChildDataObject() {
        firstChildDataObject.detach();

        assertTrue(firstChildDataObject.isSet("name"));

        assertNull(firstChildDataObject.getContainer());
        assertNull(firstChildDataObject.getContainmentProperty());

        assertNull(rootDataObject.get("child"));
    }

    public void testDetachParentDataObject() {
        rootDataObject.detach();

        assertTrue(rootDataObject.isSet("name"));

        assertNull(rootDataObject.getContainer());
        assertNull(rootDataObject.getContainmentProperty());
    }
}