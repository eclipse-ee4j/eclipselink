/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.typehelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class CustomerDefineTestCases extends SDOTestCase {
    public CustomerDefineTestCases(String name) {
        super(name);
    }

    public void testDefineTypes() throws Exception {
        List dataObjects = new ArrayList();
        dataObjects.add(getDataObject());
        List definedTypes = typeHelper.define(dataObjects);
        assertNotNull(definedTypes);
        assertEquals(1, definedTypes.size());
        verify((Type)definedTypes.get(0));
    }

    public void testDefineType() throws Exception {
        Type definedType = typeHelper.define(getDataObject());
        verify(definedType);

    }

    protected DataObject getDataObject() {
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        // create a new Type for Customers
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");

        SDOProperty prop = (SDOProperty)customerType.getType().getProperty("uri");
        customerType.set(prop, "http://example.com/customer");

        prop = (SDOProperty)customerType.getType().getProperty("name");
        customerType.set(prop, "Customer");

        // create a customer number property
        DataObject custNumProperty = customerType.createDataObject("property");
        prop = (SDOProperty)custNumProperty.getType().getProperty("name");
        custNumProperty.set(prop, "custNum");

        prop = (SDOProperty)custNumProperty.getType().getProperty("type");
        custNumProperty.set(prop, intType);
        // create a first name property
        DataObject firstNameProperty = customerType.createDataObject("property");
        prop = (SDOProperty)firstNameProperty.getType().getProperty("name");
        firstNameProperty.set(prop, "firstName");

        prop = (SDOProperty)firstNameProperty.getType().getProperty("type");
        firstNameProperty.set(prop, stringType);

        // create a last name property
        DataObject lastNameProperty = customerType.createDataObject("property");
        prop = (SDOProperty)lastNameProperty.getType().getProperty("name");
        lastNameProperty.set(prop, "lastName");

        prop = (SDOProperty)lastNameProperty.getType().getProperty("type");
        lastNameProperty.set(prop, stringType);

        return customerType;
    }

    protected void verify(Type definedType) {
        assertEquals(definedType.getName(), "Customer");

        assertEquals(definedType.getURI(), "http://example.com/customer");

        assertEquals(getPropsSize(), definedType.getDeclaredProperties().size());
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        Property prop = definedType.getProperty("custNum");
        assertNotNull(prop);
        assertEquals(intType, prop.getType());

        prop = definedType.getProperty("firstName");
        assertNotNull(prop);
        assertEquals(stringType, prop.getType());

        prop = definedType.getProperty("lastName");
        assertNotNull(prop);
        assertEquals(stringType, prop.getType());
    }

    protected int getPropsSize() {
        return 3;
    }
}
