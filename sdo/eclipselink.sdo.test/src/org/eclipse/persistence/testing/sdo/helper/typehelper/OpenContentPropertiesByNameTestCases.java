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
package org.eclipse.persistence.testing.sdo.helper.typehelper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class OpenContentPropertiesByNameTestCases extends SDOTestCase {
    public OpenContentPropertiesByNameTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.OpenContentPropertiesByNameTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();
        try {
            InputStream is = new FileInputStream("./org/eclipse/persistence/testing/sdo/schemas/GlobalElementsByName.xsd");
            List types = xsdHelper.define(is, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the xsd");
        }
    }

    public void testLookupMethodsElements() {
        //<xsd:element name="addressType" type="AddressType" sdoXML:name="addressTypeSDO"/>
        Property typeProp = typeHelper.getOpenContentProperty("my.uri", "addressType");
        assertNull(typeProp);
        Property xsdProp = xsdHelper.getGlobalProperty("my.uri", "addressType", true);
        assertNotNull(xsdProp);
        xsdProp = xsdHelper.getGlobalProperty("my.uri", "addressType", false);
        assertNull(xsdProp);

        //<xsd:element name="addressType" type="AddressType" sdoXML:name="addressTypeSDO"/>
        typeProp = typeHelper.getOpenContentProperty("my.uri", "addressTypeSDO");
        assertNotNull(typeProp);
        xsdProp = xsdHelper.getGlobalProperty("my.uri", "addressTypeSDO", true);
        assertNull(xsdProp);
        xsdProp = xsdHelper.getGlobalProperty("my.uri", "addressTypeSDO", false);
        assertNull(xsdProp);

        //<xsd:element name="customerType" type="CustomerType"/>
        typeProp = typeHelper.getOpenContentProperty("my.uri", "customerType");
        assertNotNull(typeProp);
        xsdProp = xsdHelper.getGlobalProperty("my.uri", "customerType", true);
        assertNotNull(xsdProp);
        assertEquals(typeProp, xsdProp);
        xsdProp = xsdHelper.getGlobalProperty("my.uri", "customerType", false);
        assertNull(xsdProp);

    }
    
    
    public void testLookupMethodsAttributes() {
        //<xsd:attribute name="addressTypeAttr" type="AddressType" sdoXML:name="addressTypeAttrSDO"/>
        Property typeProp = typeHelper.getOpenContentProperty("my.uri", "addressTypeAttr");
        assertNull(typeProp);
        Property xsdProp = xsdHelper.getGlobalProperty("my.uri", "addressTypeAttr", false);
        assertNotNull(xsdProp);
        xsdProp = xsdHelper.getGlobalProperty("my.uri", "addressTypeAttr", true);
        assertNull(xsdProp);

        //<xsd:attribute name="addressTypeAttr" type="AddressType" sdoXML:name="addressTypeAttrSDO"/>
        typeProp = typeHelper.getOpenContentProperty("my.uri", "addressTypeAttrSDO");
        assertNotNull(typeProp);
        xsdProp = xsdHelper.getGlobalProperty("my.uri", "addressTypeAttrSDO", true);
        assertNull(xsdProp);
        xsdProp = xsdHelper.getGlobalProperty("my.uri", "addressTypeAttrSDO", false);
        assertNull(xsdProp);

        //<xsd:attribute name="customerTypeAttr" type="CustomerType"/>
        typeProp = typeHelper.getOpenContentProperty("my.uri", "customerTypeAttr");
        assertNotNull(typeProp);
        xsdProp = xsdHelper.getGlobalProperty("my.uri", "customerTypeAttr", false);
        assertNotNull(xsdProp);
        assertEquals(typeProp, xsdProp);
        xsdProp = xsdHelper.getGlobalProperty("my.uri", "customerTypeAttr", true);
        assertNull(xsdProp);

    }

    public void testDefineOpenContentProperty() {
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", "testProp");
        propDO.set("type", SDOConstants.SDO_STRING);

        Property definedProp = typeHelper.defineOpenContentProperty("my.uri", propDO);

        Property typeProp = typeHelper.getOpenContentProperty("my.uri", "testProp");
        assertNotNull(typeProp);
        assertEquals(definedProp, typeProp);

        Property xsdProp = xsdHelper.getGlobalProperty("my.uri", "testProp", true);
        assertNull(xsdProp);

        xsdProp = xsdHelper.getGlobalProperty("my.uri", "testProp", false);
        assertNotNull(xsdProp);
    }

    public void testDefineOpenContentPropertyNullUri() {
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", "testProp");
        propDO.set("type", SDOConstants.SDO_STRING);
        typeHelper.defineOpenContentProperty(null, propDO);

        Property typeProp = typeHelper.getOpenContentProperty("my.uri", "testProp");
        assertNull(typeProp);

        Property xsdProp = xsdHelper.getGlobalProperty("my.uri", "testProp", true);
        assertNull(xsdProp);

        xsdProp = xsdHelper.getGlobalProperty("my.uri", "testProp", false);
        assertNull(xsdProp);
    }
}
