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
import commonj.sdo.Type;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;

public class MimeTypeDefineTestCases extends CustomerDefineTestCases {
    public MimeTypeDefineTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.define.MimeTypeDefineTestCases" };
        TestRunner.main(arguments);
    }

    protected DataObject getDataObject() {
        DataObject customerType = super.getDataObject();

        //((SDOType)customerType.getType()).setOpen(true);
        Type bytesType = typeHelper.getType("commonj.sdo", "Bytes");

        // create a base64 property
        DataObject base64Property = customerType.createDataObject("property");
        SDOProperty prop = (SDOProperty)base64Property.getType().getProperty("name");
        base64Property.set(prop, "base64Name");

        base64Property.set("containment", Boolean.TRUE);

        prop = (SDOProperty)base64Property.getType().getProperty("type");
        base64Property.set(prop, bytesType);

        prop = (SDOProperty)typeHelper.getOpenContentProperty("org.eclipse.persistence.sdo", "mimeType");

        base64Property.set(prop, "Image");

        return customerType;
    }

    protected void verify(Type definedType) {
        super.verify(definedType);
        Type bytesType = typeHelper.getType("commonj.sdo", "Bytes");
        SDOProperty mimeTypeProp = (SDOProperty)typeHelper.getOpenContentProperty("org.eclipse.persistence.sdo", "mimeType");

        SDOProperty prop = (SDOProperty)definedType.getProperty("firstName");
        Object value = prop.get(mimeTypeProp);
        assertNull(value);
        value = prop.getMimeTypePolicy();
        assertNull(value);

        prop = (SDOProperty)definedType.getProperty("base64Name");
        assertNotNull(prop);
        assertEquals(bytesType, prop.getType());

        assertEquals(1, prop.getInstanceProperties().size());

        Object theMimeTypeValue = prop.get(mimeTypeProp);
        assertEquals("Image", theMimeTypeValue);

        assertEquals("Image", prop.getMimeTypePolicy().getMimeType(null));
    }

    public void testUseDefinedType() throws Exception {
        Type definedType = typeHelper.define(getDataObject());

        DataObject customer = dataFactory.create(definedType);
        customer.set("firstName", "Sally");
        customer.set("lastName", "Jones");
        byte[] bytes = null;
        customer.set("base64Name", bytes);

        SDOProperty base64nameProp = (SDOProperty)customer.getType().getProperty("base64Name");
        assertTrue(base64nameProp.getXmlMapping() instanceof XMLBinaryDataMapping);
        assertTrue(((XMLBinaryDataMapping)base64nameProp.getXmlMapping()).getMimeTypePolicy() instanceof org.eclipse.persistence.oxm.mappings.FixedMimeTypePolicy);
        assertEquals("Image", ((XMLBinaryDataMapping)base64nameProp.getXmlMapping()).getMimeTypePolicy().getMimeType(customer));

    }

    protected int getPropsSize() {
        return 4;
    }
}
