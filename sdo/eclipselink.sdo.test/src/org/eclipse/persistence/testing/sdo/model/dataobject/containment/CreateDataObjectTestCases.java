/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.model.dataobject.containment;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

public class CreateDataObjectTestCases extends ContainmentTestCases {
    public CreateDataObjectTestCases(String name) {
        super(name);
    }

    public void testCreateDataObjectForPropertyWithNullType() {
        DataObject created = rootDataObject.createDataObject("nullTypeProp");
        assertNull(created);
        //TODO:Or should we expect an exception?
    }

    public void testCreateDataObjectForProperty() {
        DataObject created = rootDataObject.createDataObject("child");

        assertNotNull(created);
        assertNotNull(created.getType());
        assertEquals(firstChildType.getName(), created.getType().getName());
        assertEquals(rootDataObject, created.getContainer());
        assertTrue(rootDataObject.isSet("child"));
        assertEquals("child", created.getContainmentProperty().getName());

    }

    public void testCreateDataObjectForPropertyWith3Args() {
        DataObject created = rootDataObject.createDataObject("child", firstChildType.getURI(), firstChildType.getName());

        assertNotNull(created);
        assertNotNull(created.getType());
        assertEquals(firstChildType.getName(), created.getType().getName());
        assertEquals(rootDataObject, created.getContainer());
        assertTrue(rootDataObject.isSet("child"));
        assertEquals("child", created.getContainmentProperty().getName());

    }

    public void testCreateDataObjectForPropertyByIndex() {
        int index = -1;
        for (int i = 0; i < rootDataObject.getInstanceProperties().size(); i++) {
            Property nextProperty = (Property)rootDataObject.getInstanceProperties().get(i);
            if (nextProperty.getName().equals("child")) {
                index = i;
            }
        }
        DataObject created = rootDataObject.createDataObject(index);

        assertNotNull(created);
        assertNotNull(created.getType());
        assertEquals(firstChildType.getName(), created.getType().getName());
        assertEquals(rootDataObject, created.getContainer());
        assertTrue(rootDataObject.isSet("child"));
        assertEquals("child", created.getContainmentProperty().getName());

    }

    public void testCreateDataObjectForPropertyByIndexWith3Args() {
        int index = -1;
        for (int i = 0; i < rootDataObject.getInstanceProperties().size(); i++) {
            Property nextProperty = (Property)rootDataObject.getInstanceProperties().get(i);
            if (nextProperty.getName().equals("child")) {
                index = i;
            }
        }
        DataObject created = rootDataObject.createDataObject(index, firstChildType.getURI(), firstChildType.getName());

        assertNotNull(created);
        assertNotNull(created.getType());
        assertEquals(firstChildType.getName(), created.getType().getName());
        assertEquals(rootDataObject, created.getContainer());
        assertTrue(rootDataObject.isSet("child"));
        assertEquals("child", created.getContainmentProperty().getName());

    }
}
