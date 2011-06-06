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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathpositional;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.exceptions.SDOException;

public class SDODataObjectGetByPositionalPathTest extends SDODataObjectGetByPositionalPathTestCases {
    public SDODataObjectGetByPositionalPathTest(String name) {
        super(name);
    }
    
       
     public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.xpathpositional.SDODataObjectGetByPositionalPathTest" };
        TestRunner.main(arguments);
    }


    // normal test: a/b.0/c as path
    public void testGetByPositionalPathString() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, "test");

        this.assertEquals("test", dataObject_a.get(property));

    }

    // normal test: a/b.0/c as path
    public void testGetByPositionalPathStringObj() {
        this.assertEquals(dataObject_c, dataObject_a.get("PName-a/PName-b.0"));

    }

    // normal test: a/b.0/c as path
    public void testGetByPositionalPathStringNameWithDot() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C + ".");
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_a.set(property4, "test");

        this.assertEquals("test", dataObject_a.get(property4));

    }

    // purpose: test a nonexisted dataobject in the path
    public void testGetByPositionalPathStringWithDataObjectNotInPosition() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, "test");

        try {
            Object value = dataObject_a.get("PName-a/PName-b.1/PName-c");
        } catch (IndexOutOfBoundsException e) {
        	// get() should not throw exception (SDO 2.1 Spec)
            fail("An IndexOutOfBoundsException occurred but was not expected.");
        }
    }

    // purpose: test one of properties is not existed in path
    public void testGetByPositionalPathStringWithDataObjectNotExistedProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);
        
        this.assertNull(dataObject_a.get("PName-a/PName-f.0/PName-c"));

        /*try
        {
          dataObject_a.get("PName-a/PName-f.0/PName-c");
          fail("IllegalArgumentException should be thrown.");
        }catch(IllegalArgumentException e){}*/
    }

    // purpose: test one of properties is not existed in path
    public void testGetByPositionalPathStringWithPropertyNameContainingDot() {        
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDODataObject myTypeDO = (SDODataObject)dataFactory.create(typeType);
        myTypeDO.set("name", "myType");
        myTypeDO.set("uri", "myUri");
        
        this.addProperty(myTypeDO, "P.Name-b",SDOConstants.SDO_STRING, false, false, true);
        
        Type myType = typeHelper.define(myTypeDO);
        
        SDODataObject doTest = (SDODataObject)dataFactory.create(myType);
        assertNull(doTest.get("P.Name-b"));
        
        doTest.set("P.Name-b", "test");
        
        assertEquals("test" ,doTest.get("P.Name-b"));      
    }
    
      public void testGetByPositionalPathStringWithPropertyNameContainingDotMany() {        
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDODataObject myTypeDO = (SDODataObject)dataFactory.create(typeType);
        myTypeDO.set("name", "myType");
        myTypeDO.set("uri", "myUri");

        this.addProperty(myTypeDO, "P.Name-b",SDOConstants.SDO_STRING, false, true, true);
        
        Type myType = typeHelper.define(myTypeDO);
        
        SDODataObject doTest = (SDODataObject)dataFactory.create(myType);
        
        Object value = doTest.get("P.Name-b");
        assertTrue(value instanceof List);
        assertTrue(((List)value).size() == 0);
        
        List values = new ArrayList();
        values.add("test");
        values.add("test2");
        values.add("test3");
        doTest.set("P.Name-b", values);
        
        assertEquals("test" ,doTest.get("P.Name-b.0"));      
        assertEquals("test2" ,doTest.get("P.Name-b.1"));      
        assertEquals("test3" ,doTest.get("P.Name-b.2"));      
    }

    // purpose: test path as ".."
    public void testGetByPositionalPathStringWithContainerPath() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, "test");

        this.assertTrue(dataObject_a == dataObject_b.get(".."));

    }

    // purpose: test path as "/"
    public void testGetByPositionalPathStringWithRootPath() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_STRING);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        dataObject_c.set(property_c, "test");

        this.assertTrue(dataObject_a == dataObject_c.get("/"));

    }

    // purpose: test path as "/"
    public void testGetByPositionalPathStringWithShortDotPath() {
        this.assertEquals(dataObject_c, dataObject_b.get("PName-b.0"));

    }

    // purpose: test path as "/"
    public void testGetByPositionalPathStringWithShortBracketPath() {
        this.assertEquals(dataObject_c, dataObject_b.get("PName-b[1]"));

    }
}
