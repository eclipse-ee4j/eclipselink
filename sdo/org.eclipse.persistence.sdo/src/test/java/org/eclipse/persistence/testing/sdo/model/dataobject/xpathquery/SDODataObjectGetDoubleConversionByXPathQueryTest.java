/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class SDODataObjectGetDoubleConversionByXPathQueryTest extends SDODataObjectGetByXPathQueryTestCases {
    public SDODataObjectGetDoubleConversionByXPathQueryTest(String name) {
        super(name);
    }

    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        SDOProperty prop = dataObject_c0.getType().getProperty("test");
        prop.setType(SDOConstants.SDO_DOUBLE);

        double bb = 12.0;

        //List b = new ArrayList();
        //dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setDouble(propertyTest + "test", bb);

        assertEquals("testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet failed", bb, dataObject_a.getDouble(propertyTest + "test"), (float)0.0);
    }

    // purpose: opencontent properties
    public void testGetBooleanConversionFromDefinedPropertyWithPath() {
        SDOProperty property_c1_object = dataObject_c1.getInstanceProperty("PName-c1");
        property_c1_object.setType(SDOConstants.SDO_DOUBLE);
        List objects = new ArrayList();
        Double b = 12.0;
        Double bb = 11.0;
        objects.add(b);
        objects.add(bb);
        dataObject_c1.set(property_c1_object, objects);

        //dataObject_c1.set("PName-a0/PName-b0[number='1']/PName-c1", objects);// add it to instance list
        assertEquals("testGetBooleanConversionFromDefinedPropertyWithPath failed", bb, dataObject_a.getDouble("PName-a0/PName-b0[number='1']/PName-c1.1"), (float)0.0);
    }

    //2. purpose: getDataObject with property value is not dataobject
    public void testGetDataObjectConversionFromUndefinedProperty() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);

        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(dataObjectType);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        SDODataObject C = new SDODataObject();

        dataObject_c.set(property_c, C);

        try {
            dataObject_a.getDouble(property1);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());
        }
    }

    //3. purpose: getDataObject with property set to boolean value
    public void testGetDataObjectConversionFromProperty() {
        //try {
        assertNull(dataObject_a.getDataObject("PName-a/notExistedTest"));

        //fail("IllegalArgumentException should be thrown.");
        //} catch (IllegalArgumentException e) {
        //}
    }

    //purpose: getDataObject with nul value
    public void testGetDataObjectConversionWithNullArgument() {
        String p = null;
        assertNull(dataObject_a.getDataObject(p));
    }

    public void testSetGetDataObjectWithQueryPath() {

        /*   SDOProperty property_c1_object = new SDOProperty(aHelperContext);
           property_c1_object.setName("PName-c1");
           property_c1_object.setContainment(true);
           property_c1_object.setMany(true);
           property_c1_object.setType(SDOConstants.SDO_DOUBLE);

           type_c0.addDeclaredProperty(property_c1_object);*/
        Double b = 12.0;

        dataObject_a.setDouble("PName-a0/PName-b0[number='1']/PName-c1.0", b);

        assertEquals("testSetGetDataObjectWithQueryPath failed", b, dataObject_a.getDouble("PName-a0/PName-b0[number='1']/PName-c1.0"), (float)0.0);
    }
}
