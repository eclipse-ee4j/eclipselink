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
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetStringConversionByXPathQueryTest extends SDODataObjectGetByXPathQueryTestCases {
    public SDODataObjectGetStringConversionByXPathQueryTest(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery.SDODataObjectGetStringConversionByXPathQueryTest" };
        TestRunner.main(arguments);
    }


    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        SDOProperty p = (SDOProperty)type_c0.getDeclaredPropertiesMap().get("test");        ;
        p.setType(SDOConstants.SDO_STRING);
        //p.setMany(true);
        //type_c0.addDeclaredProperty(p);
        
        dataObject_c._setType(type_c);

        String bb = "aTest";

        //List b = new ArrayList();
        //dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setString(propertyTest + "test", bb);

        this.assertEquals(bb, dataObject_a.getString(propertyTest + "test"));
    }

    // purpose: opencontent properties
    public void testGetStringConversionFromDefinedPropertyWithPath() {
        SDOProperty property_c1_object = ((SDOProperty)dataObject_c1.getInstanceProperty("PName-c1"));
        property_c1_object.setType(SDOConstants.SDO_STRING);

        //type_c0.addDeclaredProperty(property_c1_object);
        List objects = new ArrayList();
        String b = "aTest1";
        String bb = "aTest";
        objects.add(b);
        objects.add(bb);

        dataObject_c1.set(property_c1_object, objects);// add it to instance list

        this.assertEquals(bb, dataObject_a.getString("PName-a0/PName-b0[number='1']/PName-c1.1"));
    }
    
    // purpose: numberblah is invalid property name
    public void testGetStringConversionFromUnDefinedPropertyWithPath() {
        SDOProperty property_c1_object = ((SDOProperty)dataObject_c1.getInstanceProperty("PName-c1"));
        property_c1_object.setType(SDOConstants.SDO_STRING);

        //type_c0.addDeclaredProperty(property_c1_object);
        List objects = new ArrayList();
        String b = "aTest1";
        String bb = "aTest";
        objects.add(b);
        objects.add(bb);

        dataObject_c1.set(property_c1_object, objects);// add it to instance list

        this.assertEquals(null, dataObject_a.getString("PName-a0/PName-b0[numberblah='1']/PName-c1.1"));
    }

    //2. purpose: getDataObject with property value is not dataobject
    public void testGetDataObjectConversionFromUndefinedProperty() {
        SDOProperty prop = (SDOProperty)dataObject_c0.getType().getProperty("test");
        prop.setType(SDOConstants.SDO_BYTES);
        //dataObject_c.setType(type_c);
        byte[] C = { 1, 2 };

        dataObject_c0.set("test", C);

        try {
            dataObject_a.getString(propertyTest);
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
        //SDOProperty property_c1_object = new SDOProperty(aHelperContext);
        SDOProperty property_c1_object = (SDOProperty)type_c0.getDeclaredPropertiesMap().get("PName-c1");
        property_c1_object.setName("PName-c1");
        property_c1_object.setContainment(true);
        property_c1_object.setMany(true);
        property_c1_object.setType(SDOConstants.SDO_STRING);

        type_c0.addDeclaredProperty(property_c1_object);

        String bb = "aTest";

        dataObject_a.setString("PName-a0/PName-b0[number='1']/PName-c1.0", bb);

        this.assertEquals(bb, dataObject_a.getString("PName-a0/PName-b0[number='1']/PName-c1.0"));
    }
}
