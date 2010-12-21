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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

public class SDODataObjectGetShortConversionByXPathQueryTest extends SDODataObjectGetByXPathQueryTestCases {
    public SDODataObjectGetShortConversionByXPathQueryTest(String name) {
        super(name);
    }

    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        SDOProperty prop = (SDOProperty)dataObject_c0.getType().getProperty("test");
        prop.setType(SDOConstants.SDO_SHORT);

        short s = 12;
        Short bb = new Short(s);

        //List b = new ArrayList();
        //dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setShort(propertyTest + "test", bb.shortValue());

        this.assertEquals(bb.shortValue(), dataObject_a.getShort(propertyTest + "test"));
    }

    // purpose: opencontent properties
    public void testGetShortConversionFromDefinedPropertyWithPath() {
        SDOProperty property_c1_object = ((SDOProperty)dataObject_c1.getInstanceProperty("PName-c1"));
        property_c1_object.setType(SDOConstants.SDO_SHORT);

        List objects = new ArrayList();
        short s = 12;
        Short b = new Short(s);
        short s1 = 12;
        Short bb = new Short(s1);
        objects.add(b);
        objects.add(bb);

        dataObject_c1.set(property_c1_object, objects);// add it to instance list

        this.assertEquals(bb.shortValue(), dataObject_a.getShort("PName-a0/PName-b0[number='1']/PName-c1.1"));
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
            dataObject_a.getShort(property1);
        } catch (Exception e) {
            fail("No Exception expected, but caught " + e.getClass());                        
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
        SDOProperty property_c1_object = ((SDOProperty)dataObject_c1.getInstanceProperty("PName-c1"));
        property_c1_object.setType(SDOConstants.SDO_SHORT);

        short s = 12;
        Short bb = new Short(s);

        dataObject_a.setShort("PName-a0/PName-b0[number='1']/PName-c1.0", bb.shortValue());

        this.assertEquals(bb.shortValue(), dataObject_a.getShort("PName-a0/PName-b0[number='1']/PName-c1.0"));
    }
}
