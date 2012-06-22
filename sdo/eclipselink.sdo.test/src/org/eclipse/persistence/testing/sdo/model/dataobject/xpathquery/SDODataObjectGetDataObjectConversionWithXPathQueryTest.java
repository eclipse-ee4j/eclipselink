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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class SDODataObjectGetDataObjectConversionWithXPathQueryTest extends SDODataObjectGetByXPathQueryTestCases {
    public SDODataObjectGetDataObjectConversionWithXPathQueryTest(String name) {
        super(name);
    }

    // purpose: pass a/b, to get dataObjcet c
    public void testGetDataObjectConversionFromDefinedProperty() {
        this.assertEquals(dataObject_d0, dataObject_a.getDataObject("PName-a/PName-b.0/PName-c[number='123']"));
    }

    // purpose: opencontent properties
    public void testGetDataObjectConversionFromDefinedPropertyWithPath1() {
        DataObject base = dataFactory.create(baseType);
        SDODataObject b = new SDODataObject();

        base.set("baseProperty2", b);
        assertEquals(b, base.getDataObject("baseProperty2"));

    }

    // purpose: opencontent properties
    public void testGetDataObjectConversionFromDefinedPropertyWithPath() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        SDOProperty property_c1_object = ((SDOProperty)dataObject_c1.getInstanceProperty("PName-c1"));
        property_c1_object.setType(dataObjectType);

        //type_c0.addDeclaredProperty(property_c1_object);
        List objects = new ArrayList();
        SDODataObject b = new SDODataObject();
        SDODataObject bb = new SDODataObject();
        objects.add(b);
        objects.add(bb);

        dataObject_c1.set(property_c1_object, objects);// add it to instance list
        //dataObject_c1.setDataObject(property_c1_object, bb);
        this.assertEquals(bb, dataObject_a.getDataObject("PName-a0/PName-b0[number='1']/PName-c1.1"));
    }

    //2. purpose: getDataObject with property value is not dataobject
    public void testGetDataObjectConversionFromUndefinedProperty() {
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);

        DataObject testDataObject = dataFactory.create(typeType);
        testDataObject.set("name", "theName");
        testDataObject.set("uri", "theUri");
        addProperty(testDataObject, "testProp", SDOConstants.SDO_BOOLEAN, false, false, true);
        Type theType = typeHelper.define(testDataObject);
        
        DataObject sampleDataObject = dataFactory.create(theType);
        Property prop = sampleDataObject.getInstanceProperty("testProp");
        sampleDataObject.set(prop,new Boolean(true));
    
        try {
          
          sampleDataObject.getDataObject(prop);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getDataObject with property set to boolean value
    public void testGetDataObjectConversionFromProperty() {
        //try {
        this.assertNull(dataObject_a.getDataObject("PName-a/notExistedTest"));

        //fail("IllegalArgumentException should be thrown.");
        //} catch (IllegalArgumentException e) {
        //}
    }

    //purpose: getDataObject with nul value
    public void testGetDataObjectConversionWithNullArgument() {
        String p = null;
        this.assertNull(dataObject_a.getDataObject(p));
    }

    public void testSetGetDataObjectWithQueryPath() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        SDOProperty property_c1_object = new SDOProperty(aHelperContext);
        property_c1_object.setName("PName-c1");
        property_c1_object.setContainment(true);
        property_c1_object.setMany(true);
        property_c1_object.setType(dataObjectType);

        type_c0.addDeclaredProperty(property_c1_object);

        SDODataObject b = new SDODataObject();

        dataObject_a.set("PName-a0/PName-b0[number='1']/PName-c1.0", b);

        this.assertEquals(b, dataObject_a.get("PName-a0/PName-b0[number='1']/PName-c1.0"));
    }
}
