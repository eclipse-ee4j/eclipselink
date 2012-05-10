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
package org.eclipse.persistence.testing.sdo.model.dataobject;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDODataObjectOpenContentBug6011530TestCases extends SDOTestCase {
    private DataObject rootDataObject;
    private DataObject rootDataObject2;
    private DataObject childDataObject;
    private DataObject childDataObject2;

    public SDODataObjectOpenContentBug6011530TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectOpenContentBug6011530TestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();
        Type stringType = SDOConstants.SDO_STRING;

        DataObject addressType = defineType("my.uri", "address");
        addProperty(addressType, "city", stringType, false, false, true);
        addProperty(addressType, "street", stringType, false, false, true);

        Type addressSDOType = typeHelper.define(addressType);

        DataObject empType = defineType("my.uri", "employee");
        addProperty(empType, "name", stringType, false, false, true);
        addProperty(empType, "address", addressSDOType, true, false, true);
        empType.set("open", true);

        Type empSDOType = typeHelper.define(empType);

        rootDataObject = dataFactory.create(empSDOType);
        rootDataObject.set("name", "Bob Smith");

        rootDataObject2 = dataFactory.create(empSDOType);
        rootDataObject2.set("name", "JaneDoe");
        childDataObject = dataFactory.create(addressSDOType);
        childDataObject2 = dataFactory.create(addressSDOType);
    }

    public void testSetDefineOpenContent() throws Exception {
        rootDataObject.set("addressOpenContent", childDataObject);
        Property openProp = rootDataObject.getInstanceProperty("addressOpenContent");
        assertNotNull(openProp);
        assertTrue(openProp.isOpenContent());
        assertTrue(openProp.isContainment());
        assertFalse(openProp.isMany());
        assertEquals(childDataObject.getType(), openProp.getType());

    }
    
     public void testSetDefineOpenContentManyProperty() throws Exception {
        List value = new ArrayList();
        value.add(childDataObject);
        rootDataObject.set("addressOpenContent", value);
        Property openProp = rootDataObject.getInstanceProperty("addressOpenContent");
        assertNotNull(openProp);
        assertTrue(openProp.isOpenContent());
        assertTrue(openProp.isContainment());
        assertTrue(openProp.isMany());
        assertEquals(childDataObject.getType(), openProp.getType());
    }
    
      public void testSetDefineOpenContentManyPropertyContainmentChild() throws Exception {
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);

        List value = new ArrayList();
        Type addressSDOType = typeHelper.getType("my.uri", "address");
        DataObject childDataObjectContainment = dataFactory.create(addressSDOType);
        
        DataObject someOther = dataFactory.create(typeType);
        someOther.set("name", "someOther");
        someOther.set("uri", "my.uri");
        addProperty(someOther,"test", addressSDOType, true, false, true);        
        Type someOtherParentType = typeHelper.define(someOther);
        DataObject someOtherParentDO = dataFactory.create(someOtherParentType);
        someOtherParentDO.set("test", childDataObjectContainment);
                
        value.add(childDataObjectContainment);
        rootDataObject.set("addressOpenContent", value);
        Property openProp = rootDataObject.getInstanceProperty("addressOpenContent");
        assertNotNull(openProp);
        assertTrue(openProp.isOpenContent());
        assertFalse(openProp.isContainment());
        assertTrue(openProp.isMany());
        assertEquals(childDataObject.getType(), openProp.getType());
    }
    
    public void testSetDefineOpenContentManySimpleProperty() throws Exception {
        List value = new ArrayList();
        value.add(new Integer(4));
        rootDataObject.set("addressOpenContent", value);
        Property openProp = rootDataObject.getInstanceProperty("addressOpenContent");
        assertNotNull(openProp);
        assertTrue(openProp.isOpenContent());
        assertFalse(openProp.isContainment());
        assertTrue(openProp.isMany());
        assertEquals(SDOConstants.SDO_INTOBJECT, openProp.getType());
        
        assertTrue(rootDataObject.isSet("addressOpenContent"));
        rootDataObject.unset("addressOpenContent");
        assertFalse(rootDataObject.isSet("addressOpenContent"));
    }
    
     public void testSetDefineOpenContentManySimplePropertyEmpty() throws Exception {
        List value = new ArrayList();        
        rootDataObject.set("addressOpenContent", value);
        Property openProp = rootDataObject.getInstanceProperty("addressOpenContent");
        assertNull(openProp);
        
    }
    
     public void testSetDefineOpenContentManySimplePropertyNullInList() throws Exception {
        List value = new ArrayList();        
        value.add(null);
        rootDataObject.set("addressOpenContent", value);
        Property openProp = rootDataObject.getInstanceProperty("addressOpenContent");
        assertNull(openProp);        
    }

    public void testSetDefineOpenContentContained() throws Exception {
        rootDataObject2.set("address", childDataObject2);

        rootDataObject.set("addressOpenContent", childDataObject2);
        Property openProp = rootDataObject.getInstanceProperty("addressOpenContent");
        assertNotNull(openProp);
        assertTrue(openProp.isOpenContent());
        assertFalse(openProp.isContainment());
        assertFalse(openProp.isMany());
        assertEquals(childDataObject.getType(), openProp.getType());

    }

    public void testSetDefineOpenContentMany() throws Exception {
        List theList = new ArrayList();
        theList.add(childDataObject);
        theList.add(childDataObject2);

        rootDataObject.set("addressOpenContent", theList);
        Property openProp = rootDataObject.getInstanceProperty("addressOpenContent");
        assertNotNull(openProp);
        assertTrue(openProp.isOpenContent());
        assertTrue(openProp.isContainment());
        assertTrue(openProp.isMany());
        assertEquals(childDataObject.getType(), openProp.getType());
    }

    public void testSetDefineOpenContentSimple() throws Exception {
        rootDataObject.set("stringOpenContent", "someString");
        Property openProp = rootDataObject.getInstanceProperty("stringOpenContent");
        assertNotNull(openProp);
        assertTrue(openProp.isOpenContent());
        assertFalse(openProp.isContainment());
        assertFalse(openProp.isMany());
        assertEquals(SDOConstants.SDO_STRING, openProp.getType());

    }
    
    public void testSetDefineOpenContentSimple2() throws Exception {
        BigInteger value = new BigInteger("123");
        rootDataObject.set("stringOpenContent", value);
        Property openProp = rootDataObject.getInstanceProperty("stringOpenContent");
        assertNotNull(openProp);
        assertTrue(openProp.isOpenContent());
        assertFalse(openProp.isContainment());
        assertFalse(openProp.isMany());
        assertEquals(SDOConstants.SDO_INTEGER, openProp.getType());
    }
        
}
