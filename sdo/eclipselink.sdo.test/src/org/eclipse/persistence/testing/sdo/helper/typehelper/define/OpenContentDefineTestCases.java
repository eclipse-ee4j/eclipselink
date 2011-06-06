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
package org.eclipse.persistence.testing.sdo.helper.typehelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.eclipse.persistence.exceptions.SDOException;

/*
 * testing JIRA-108 resolution
 */
public class OpenContentDefineTestCases extends SDOTestCase {
    private Type rootType;

    public OpenContentDefineTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();

        SDODataObject rootDataObjectType = (SDODataObject)defineType("my.uri", "myRoot");
        addProperty(rootDataObjectType, "firstName", typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.STRING));
        addProperty(rootDataObjectType, "lastName", typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.STRING));

        rootType = typeHelper.define(rootDataObjectType);
        ((SDOType)rootType).setOpen(true);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.define.OpenContentDefineTestCases" };
        TestRunner.main(arguments);
    }

    public void testDefineOpenContentProperty() {
        assertEquals(2, rootType.getDeclaredProperties().size());

        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject newProperty = dataFactory.create(propertyType);
        SDOProperty prop = (SDOProperty)newProperty.getType().getProperty("name");
        newProperty.set(prop, "myOpenProp");
        prop = (SDOProperty)newProperty.getType().getProperty("type");
        newProperty.set(prop, typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.STRING));

        Property openProp = typeHelper.defineOpenContentProperty("my.uri", newProperty);

        Property lookedUp = typeHelper.getOpenContentProperty("my.uri", "myOpenProp");
        assertNotNull(lookedUp);
        // verify both properties are the same object
        assertTrue(openProp == lookedUp);
        assertTrue(lookedUp.isOpenContent());

        DataObject rootDataObject = dataFactory.create(rootType);
        assertEquals(2, rootDataObject.getInstanceProperties().size());

        //set should add the open content property to instance properties
        rootDataObject.set(lookedUp, "testing");
        assertEquals(3, rootDataObject.getInstanceProperties().size());
        assertNull(lookedUp.getContainingType());

        //unset should remove the open content property
        rootDataObject.unset(lookedUp);
        assertEquals(2, rootDataObject.getInstanceProperties().size());
    }

    public void testDataTypeTrueFails() throws Exception {
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
        DataObject newTypeDO = dataFactory.create(typeType);
        SDOProperty prop = (SDOProperty)newTypeDO.getType().getProperty("name");
        newTypeDO.set(prop, "myOpenType");
        newTypeDO.set("uri", "my.uri");
        newTypeDO.set("dataType", true);
        newTypeDO.set("open", true);
        try {
            Type newType = typeHelper.define(newTypeDO);
        } catch (SDOException e) {
            assertEquals(SDOException.TYPE_CANNOT_BE_OPEN_AND_DATATYPE, e.getErrorCode());
            return;
        }
        fail("An IllegalArgumentException should have occurred");
    }

    public void testDataTypeTrueFails2() throws Exception {
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
        DataObject newTypeDO = dataFactory.create(typeType);
        SDOProperty prop = (SDOProperty)newTypeDO.getType().getProperty("name");
        newTypeDO.set(prop, "myOpenType");
        newTypeDO.set("uri", "my.uri");
        newTypeDO.set("open", true);
        newTypeDO.set("dataType", true);
        try {
            Type newType = typeHelper.define(newTypeDO);
        } catch (SDOException e) {
            assertEquals(SDOException.TYPE_CANNOT_BE_OPEN_AND_DATATYPE, e.getErrorCode());
            return;
        }
    }

    public void testDataTypeTrueFails3() throws Exception {
        try {
            SDOConstants.SDO_STRING.setOpen(true);
        } catch (SDOException e) {
            assertEquals(SDOException.TYPE_CANNOT_BE_OPEN_AND_DATATYPE, e.getErrorCode());
            return;
        }
    }

    public void testDefineOpenContentPropertyViaGenericDefineP34ofSpec() {
        // Create a new Type and with an open content property set
        DataObject myDataType = dataFactory.create("commonj.sdo", "Type");
        myDataType.set("name", "MyType");
        Property openContentProperty = null;

        // SDOTypeHelperDelegate.openContentProperties Map is still null at this point
        //Property openContentProperty = typeHelper.getOpenContentProperty(
        //		"commonj.sdo", "someProperty");
        //assertNull(openContentProperty);
        // NPE here
        //myDataType.set(openContentProperty, "test");
        // Define the Type
        Type definedType = typeHelper.define(myDataType);

        //		Property openContentProperty = typeHelper.getOpenContentProperty(
        //				"commonj.sdo", "someProperty");
        //		assertNotNull(openContentProperty);
        // Retrieve the open content property
        Object retrievedValue = definedType.get(openContentProperty);

    }
}
