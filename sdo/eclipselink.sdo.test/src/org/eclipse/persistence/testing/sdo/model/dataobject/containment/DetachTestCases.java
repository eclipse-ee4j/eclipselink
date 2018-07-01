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
