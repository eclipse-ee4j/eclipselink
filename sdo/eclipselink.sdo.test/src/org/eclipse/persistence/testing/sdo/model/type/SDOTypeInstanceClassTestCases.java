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
package org.eclipse.persistence.testing.sdo.model.type;

import commonj.sdo.DataObject;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDOTypeInstanceClassTestCases extends SDOTestCase {
    public SDOTypeInstanceClassTestCases(String name) {
        super(name);
    }

    public void testInstanceClassNameWithNullInstanceClass() {
        SDOType type = new SDOType("my.uri", "myType");
        type.setInstanceClass(null);
        type.setInstanceClassName("org.eclipse.persistence.testing.sdo.model.type.USAddress");

        Class theClass = type.getInstanceClass();
        assertNotNull(theClass);
    }

    public void testInstanceClassInvlaidNameWithNullInstanceClass() {
        SDOType type = new SDOType("my.uri", "myType");

        type.setInstanceClass(null);
        type.setInstanceClassName("org.eclipse.persistence.testing.sdo.myInvalidClass");

        Class theClass = type.getInstanceClass();
        assertNull(theClass);
    }

    public void testIsInstance() {
        SDOType type = new SDOType(aHelperContext);
        type.setInstanceClass(USAddress.class);

        USAddressImpl address = new USAddressImpl();
        boolean isInstance = type.isInstance(address);
        assertTrue(isInstance);
    }

    public void testIsInstanceNullInstanceClass() {
        SDOType type = new SDOType(aHelperContext);
        type.setInstanceClass(null);

        USAddressImpl address = new USAddressImpl();
        boolean isInstance = type.isInstance(address);
        assertFalse(isInstance);
    }

    public void testIsInstanceNullInstanceClassWithType() {
        SDOType type = new SDOType("http://testing", "USAddress");
        type.setAbstract(false);
        type.setInstanceClass(null);
        SDOProperty prop = new SDOProperty(aHelperContext);
        prop.setName("name");
        prop.setType(SDOConstants.SDO_STRING);
        prop.setContainingType(type);
        type.addDeclaredProperty(prop);

        SDOProperty prop2 = new SDOProperty(aHelperContext);
        prop2.setName("street");
        prop2.setType(SDOConstants.SDO_STRING);
        prop2.setContainingType(type);
        type.addDeclaredProperty(prop2);

        ((SDOTypeHelper)typeHelper).addType(type);

        DataObject address = (DataObject)dataFactory.create(type);
        boolean isInstance = type.isInstance(address);
        assertTrue(isInstance);
    }
}
