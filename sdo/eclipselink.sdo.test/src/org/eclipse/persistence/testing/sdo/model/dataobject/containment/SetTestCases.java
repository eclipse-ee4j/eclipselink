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
package org.eclipse.persistence.testing.sdo.model.dataobject.containment;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

public class SetTestCases extends ContainmentTestCases {
    public SetTestCases(String name) {
        super(name);
    }

    public void testSetContainmentProperty() {
        rootDataObject.set("name", "myRoot");

        rootDataObject.set("child", firstChildDataObject);

        assertNotNull(firstChildDataObject);

        assertEquals(rootDataObject, firstChildDataObject.getContainer());
        assertEquals("child", firstChildDataObject.getContainmentProperty().getName());

        Object value = rootDataObject.get("child");
        assertEquals(firstChildDataObject, value);
    }

    public void testSetContainmentPropertyTwice() {
        rootDataObject.set("name", "myRoot");
        DataObject container = firstChildDataObject.getContainer();
        Property containmentProperty = firstChildDataObject.getContainmentProperty();
        assertNull(container);
        assertNull(containmentProperty);

        rootDataObject.set("child", firstChildDataObject);
        container = firstChildDataObject.getContainer();
        containmentProperty = firstChildDataObject.getContainmentProperty();
        assertNotNull(container);
        assertEquals("child", containmentProperty.getName());
        Object value = rootDataObject.get("child");
        assertNotNull(value);
        value = rootDataObject.get("child2");
        assertNull(value);

        rootDataObject.set("child2", firstChildDataObject);
        container = firstChildDataObject.getContainer();
        containmentProperty = firstChildDataObject.getContainmentProperty();
        assertNotNull(container);
        assertEquals("child2", containmentProperty.getName());
        value = rootDataObject.get("child2");
        assertNotNull(value);
        value = rootDataObject.get("child");
        assertNull(value);

    }

    // TODO: 20060906 bidirectional/reference    
    public void testSetCircularReference() {
        rootDataObject.set("name", "myRoot");

        rootDataObject.set("child", firstChildDataObject);

        firstChildDataObject.set("child", secondChildDataObject);
        try {
            secondChildDataObject.set("child", rootDataObject);
            fail("An IllegalArgumentException due to circular reference should have been thrown");
        } catch (IllegalArgumentException e) {
        }
    }  

    public void testSetNestedContainmentProperty() {
        rootDataObject.set("name", "myRoot");

        rootDataObject.set("child", firstChildDataObject);

        assertNotNull(firstChildDataObject);

        assertEquals(rootDataObject, firstChildDataObject.getContainer());
        assertEquals("child", firstChildDataObject.getContainmentProperty().getName());

        Object value = rootDataObject.get("child");
        assertEquals(firstChildDataObject, value);

        firstChildDataObject.set("child", secondChildDataObject);

        DataObject root = secondChildDataObject.getRootObject();
        assertEquals(rootDataObject, root);

        root = firstChildDataObject.getRootObject();
        assertEquals(rootDataObject, root);

        root = rootDataObject.getRootObject();
        assertEquals(rootDataObject, root);
    }
}
