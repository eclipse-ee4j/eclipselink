/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.1.3 - initial implementation
package org.eclipse.persistence.testing.sdo.model.property;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;

import junit.framework.TestCase;

public class XmlElementPropertyTestCases extends TestCase {

    private DataFactory dataFactory;
    private TypeHelper typeHelper;

    @Override
    protected void setUp() throws Exception {
        HelperContext helperContext = new SDOHelperContext();
        dataFactory = helperContext.getDataFactory();
        typeHelper = helperContext.getTypeHelper();
    }

    public void testPropertyFromDataObjectWithXmlElementPropertySetToTrue() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        DataObject propertyDO = dataFactory.create("commonj.sdo", "Property");
        propertyDO.set("name", "foo");
        propertyDO.set("type", stringType);
        propertyDO.set(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        Property property = typeHelper.defineOpenContentProperty("urn:example", propertyDO);

        assertTrue((Boolean) property.get(SDOConstants.XMLELEMENT_PROPERTY));
        assertEquals(1, property.getInstanceProperties().size());
    }

    public void testPropertyFromDataObjectWithXmlElementPropertySetToFalse() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        DataObject propertyDO = dataFactory.create("commonj.sdo", "Property");
        propertyDO.set("name", "foo");
        propertyDO.set("type", stringType);
        propertyDO.set(SDOConstants.XMLELEMENT_PROPERTY, Boolean.FALSE);
        Property property = typeHelper.defineOpenContentProperty("urn:example", propertyDO);

        assertFalse((Boolean) property.get(SDOConstants.XMLELEMENT_PROPERTY));
        assertEquals(1, property.getInstanceProperties().size());
    }

    public void testPropertyFromDataObjectWithXmlElementPropertySetToNull() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        DataObject propertyDO = dataFactory.create("commonj.sdo", "Property");
        propertyDO.set("name", "foo");
        propertyDO.set("type", stringType);
        propertyDO.set(SDOConstants.XMLELEMENT_PROPERTY, null);
        Property property = typeHelper.defineOpenContentProperty("urn:example", propertyDO);

        assertNull(property.get(SDOConstants.XMLELEMENT_PROPERTY));
        assertEquals(0, property.getInstanceProperties().size());
    }

    public void testPropertyFromDataObjectWithXmlElementPropertyNotSet() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        DataObject propertyDO = dataFactory.create("commonj.sdo", "Property");
        propertyDO.set("name", "foo");
        propertyDO.set("type", stringType);
        Property property = typeHelper.defineOpenContentProperty("urn:example", propertyDO);

        assertNull(property.get(SDOConstants.XMLELEMENT_PROPERTY));
        assertEquals(0, property.getInstanceProperties().size());
    }

    public void testPropertyFromDataObjectWithOpenContentPropertySet() {
        String controlProperty = "CONTROL_PROPERTY";
        String controlValue = "CONTROL_VALUE";

        Type stringType = typeHelper.getType("commonj.sdo", "String");

        DataObject propertyDO = dataFactory.create("commonj.sdo", "Property");
        propertyDO.set("name", "foo");
        propertyDO.set("type", stringType);
        propertyDO.set(controlProperty, controlValue);
        Property property = typeHelper.defineOpenContentProperty("urn:example", propertyDO);

        assertEquals(1, property.getInstanceProperties().size());
    }

    public void testPropertyFromDataObjectWithXmlElementPropertySetToTrueAndOpenContentPropertySet() {
        String controlProperty = "CONTROL_PROPERTY";
        String controlValue = "CONTROL_VALUE";

        Type stringType = typeHelper.getType("commonj.sdo", "String");

        DataObject propertyDO = dataFactory.create("commonj.sdo", "Property");
        propertyDO.set("name", "foo");
        propertyDO.set("type", stringType);
        propertyDO.set(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        propertyDO.set(controlProperty, controlValue);
        Property property = typeHelper.defineOpenContentProperty("urn:example", propertyDO);

        assertTrue((Boolean) property.get(SDOConstants.XMLELEMENT_PROPERTY));
        assertEquals(2 , property.getInstanceProperties().size());

    }

    public void testPropertyFromDataObjectWithOpenContentPropertySetAndXmlElementPropertySetToTrue() {
        String controlProperty = "CONTROL_PROPERTY";
        String controlValue = "CONTROL_VALUE";

        Type stringType = typeHelper.getType("commonj.sdo", "String");

        DataObject propertyDO = dataFactory.create("commonj.sdo", "Property");
        propertyDO.set("name", "foo");
        propertyDO.set("type", stringType);
        propertyDO.set(controlProperty, controlValue);
        propertyDO.set(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        Property property = typeHelper.defineOpenContentProperty("urn:example", propertyDO);

        assertTrue((Boolean) property.get(SDOConstants.XMLELEMENT_PROPERTY));
        assertEquals(2  , property.getInstanceProperties().size());
    }

}
