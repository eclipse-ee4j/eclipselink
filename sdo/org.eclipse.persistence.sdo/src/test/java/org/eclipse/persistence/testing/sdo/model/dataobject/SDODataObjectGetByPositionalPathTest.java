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
package org.eclipse.persistence.testing.sdo.model.dataobject;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;

public class SDODataObjectGetByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetByPositionalPathTest(String name) {
        super(name);
    }

     public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectGetByPositionalPathTest" };
        TestRunner.main(arguments);
    }

    // normal test: a/b.0/c as path
    public void testGetByPositionalPathString() {
        dataObject_c.getInstanceProperty(PROPERTY_NAME_C).setType(SDOConstants.SDO_STRING);

        dataObject_c.set(property_c, "test");

        assertEquals("test", dataObject_a.get(property));

    }

    // normal test: a/b.0/c as path
    public void testGetByPositionalPathStringObj() {
        assertEquals(dataObject_c, dataObject_a.get("PName-a/PName-b.0"));

    }

    // normal test: a/b.0/c as path
    public void testGetByPositionalPathStringNameWithDot() {

        /*property_c = new SDOProperty();
        property_c.setName(PROPERTY_NAME_C+".");
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c.setType(type_c);
        */
        dataObject_a.set(property4, "test");

        assertEquals("test", dataObject_a.get(property4));

    }

    // purpose: test a nonexisted dataobject in the path
    public void testGetByPositionalPathStringWithDataObjectNotInPosition() {
        dataObject_c.getInstanceProperty(PROPERTY_NAME_C).setType(SDOConstants.SDO_STRING);

        dataObject_c.set(property_c, "test");

        //Changed test since out of bounds now throws an exception SDO Jira 81
        //this.assertNull(dataObject_a.get("PName-a/PName-b.1/PName-c"));
        try {
            Object value = dataObject_a.get("PName-a/PName-b.1/PName-c");
        } catch (IndexOutOfBoundsException e) {
            // get() should not throw exception (SDO 2.1 Spec)
            fail("An IndexOutOfBoundsException occurred but was not expected.");
        }
    }

    // purpose: test one of properties is not existed in path
    public void testGetByPositionalPathStringWithDataObjectNotExistedProperty() {
        dataObject_c.getInstanceProperty(PROPERTY_NAME_C).setType(SDOConstants.SDO_STRING);

        dataObject_c.set(property_c, "test");

        assertNull(dataObject_a.get("PName-a/PName-f.0/PName-c"));
    }

    // purpose: test one of properties is not existed in path
    public void testGetByPositionalPathStringWithPropertyNameContainingDot() {
        dataObject_c.getInstanceProperty(PROPERTY_NAME_C).setType(SDOConstants.SDO_STRING);
        dataObject_c.set(property_c, "test");

        assertNull(dataObject_a.get(UNDEFINED_PATH));
        //this.assertNull(dataObject_a.get(property));

        /*try{
          dataObject_a.get(property);
          fail("IllegalArgumentException should be thrown");
        }catch(IllegalArgumentException e){}*/
    }

    // purpose: test path as ".."
    public void testGetByPositionalPathStringWithContainerPath() {
        dataObject_c.getInstanceProperty(PROPERTY_NAME_C).setType(SDOConstants.SDO_STRING);

        dataObject_c.set(property_c, "test");

        assertTrue(dataObject_a == dataObject_b.get(".."));

    }

    // purpose: test path as "/"
    public void testGetByPositionalPathStringWithRootPath() {
        dataObject_c.getInstanceProperty(PROPERTY_NAME_C).setType(SDOConstants.SDO_STRING);

        dataObject_c.set(property_c, "test");

        assertTrue(dataObject_a == dataObject_c.get("/"));

    }
}
