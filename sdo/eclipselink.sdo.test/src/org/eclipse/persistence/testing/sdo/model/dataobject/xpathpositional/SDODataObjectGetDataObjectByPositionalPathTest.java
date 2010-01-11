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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathpositional;

import commonj.sdo.DataObject;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDODataFactory;

public class SDODataObjectGetDataObjectByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetDataObjectByPositionalPathTest(String name) {
        super(name);
    }

    // purpose: pass a/b, to get dataObjcet c
    public void testGetDataObjectConversionFromDefinedProperty() {
        this.assertEquals(dataObject_c, dataObject_a.getDataObject("PName-a/PName-b.0"));
        this.assertEquals(dataObject_c, dataObject_a.getDataObject("PName-a/PName-b[1]"));
    }

    // purpose: opencontent properties
    public void testGetDataObjectConversionFromDefinedPropertyWithPath() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        // dataObject's type add boolean property
        type_b.setOpen(true);
        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName("openTest");
        property.setMany(true);
        property.setType(dataObjectType);

        List objects = new ArrayList();

        SDOType t = new SDOType("uri", "test");
        SDOProperty p = new SDOProperty(aHelperContext);
        p.setType(SDOConstants.SDO_STRING);
        p.setMany(false);
        p.setName("number");
        t.addDeclaredProperty(p);

        SDODataObject b = new SDODataObject();
        b._setType(t);
        b.set(p, "1");

        dataObject_b.set(property, objects);// add it to instance list
        dataObject_a.setDataObject("PName-a/openTest[1]", b);

        this.assertEquals(b, dataObject_a.getDataObject("PName-a/openTest[number=1]"));
    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetDataObjectnWithPathFromDefinedBooleanPropertyBracketPositionalSet() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(dataObjectType);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        SDODataObject bb = new SDODataObject();
        List b = new ArrayList();

        //b.add(bb);
        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setDataObject(property3, bb);

        this.assertEquals(bb, dataObject_a.getDataObject(property3));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetDataObjectConversionWithPathFromDefinedBooleanPropertyDotPositionalSet() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        // dataObject's type add boolean property
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(dataObjectType);
        property_c.setMany(true);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        SDODataObject bb = new SDODataObject();
        List b = new ArrayList();

        dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setDataObject(property + ".0", bb);

        this.assertEquals(bb, dataObject_a.getDataObject(property + ".0"));

    }

    //1. purpose: getBoolean with Defined Boolean Property
    public void testGetDataObjectConversionWithPathFromDefinedBooleanPropertyBracketInPathMiddle() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(dataObjectType);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        SDODataObject bb = new SDODataObject();

        dataObject_a.setDataObject(property1, bb);// c dataobject's a property has value boolean 'true'

        this.assertEquals(bb, dataObject_a.getDataObject(property1));
    }

    //2. purpose: getDataObject with property value is not dataobject
    public void testGetDataObjectConversionFromUndefinedProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_BOOLEAN);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        boolean c = true;
        Boolean C = new Boolean(c);

        dataObject_c.set(property_c, C);

        try {
            dataObject_a.getDataObject(property);
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
}
