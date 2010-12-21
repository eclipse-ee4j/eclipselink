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
package org.eclipse.persistence.testing.sdo.helper.typehelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;

public class MimeTypeOnOtherPropertyDefineTestCases extends CustomerDefineTestCases {
    public MimeTypeOnOtherPropertyDefineTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.define.MimeTypeOnOtherPropertyDefineTestCases" };
        TestRunner.main(arguments);
    }

    protected DataObject getDataObject() {
        DataObject customerType = super.getDataObject();
        Type bytesType = typeHelper.getType("commonj.sdo", "Bytes");
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        // create a base64 property
        DataObject base64Property = customerType.createDataObject("property");
        SDOProperty prop = (SDOProperty)base64Property.getType().getProperty("name");
        base64Property.set(prop, "base64Name");

        prop = (SDOProperty)base64Property.getType().getProperty("type");
        base64Property.set(prop, bytesType);

        base64Property.set("containment", Boolean.TRUE);

        prop = (SDOProperty)typeHelper.getOpenContentProperty("org.eclipse.persistence.sdo", "mimeTypeProperty");
        base64Property.set(prop, "testMimeType");

        DataObject testMimeTypeProperty = customerType.createDataObject("property");
        prop = (SDOProperty)testMimeTypeProperty.getType().getProperty("name");
        testMimeTypeProperty.set(prop, "testMimeType");

        prop = (SDOProperty)testMimeTypeProperty.getType().getProperty("type");
        testMimeTypeProperty.set(prop, stringType);

        return customerType;
    }

    protected void verify(Type definedType) {
        super.verify(definedType);
        Type bytesType = typeHelper.getType("commonj.sdo", "Bytes");

        SDOProperty prop = (SDOProperty)definedType.getProperty("base64Name");
        assertNotNull(prop);
        assertEquals(bytesType, prop.getType());

        assertEquals(1, prop.getInstanceProperties().size());

        Property mimeTypePropProperty = typeHelper.getOpenContentProperty("org.eclipse.persistence.sdo", "mimeTypeProperty");
        Object value = prop.get(SDOConstants.MIME_TYPE_PROPERTY_PROPERTY);
        assertEquals("testMimeType", value);

    }

    protected int getPropsSize() {
        return 5;
    }

    public void testUseDefinedType() throws Exception {
        Type definedType = typeHelper.define(getDataObject());

        //  ((SDOType)definedType).setOpen(true);
        DataObject customer = dataFactory.create(definedType);
        customer.set("firstName", "Sally");
        customer.set("lastName", "Jones");
        byte[] bytes = null;
        customer.set("base64Name", bytes);

        Property prop = (SDOProperty)typeHelper.getOpenContentProperty("org.eclipse.persistence.sdo", "mimeTypeProperty");

        //customer.set(prop, "testMimeType");//needs type to be open
        customer.set("testMimeType", "Image");

        SDOProperty base64nameProp = (SDOProperty)customer.getType().getProperty("base64Name");
        base64nameProp.setInstanceProperty(prop, "testMimeType");
        assertTrue(base64nameProp.getXmlMapping() instanceof XMLBinaryDataMapping);
        assertTrue(((XMLBinaryDataMapping)base64nameProp.getXmlMapping()).getMimeTypePolicy() instanceof org.eclipse.persistence.sdo.helper.AttributeMimeTypePolicy);
        assertEquals("Image", ((XMLBinaryDataMapping)base64nameProp.getXmlMapping()).getMimeTypePolicy().getMimeType(customer));

    }
}
