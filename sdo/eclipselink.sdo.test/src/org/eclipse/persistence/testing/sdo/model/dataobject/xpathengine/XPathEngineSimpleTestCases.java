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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathengine;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.helper.SDODataFactory;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.eclipse.persistence.exceptions.SDOException;

public class XPathEngineSimpleTestCases extends SDOTestCase {
    private DataObject rootObject;

    public XPathEngineSimpleTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.xpathengine.XPathEngineSimpleTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();
        try {
            InputStream is = new FileInputStream("org/eclipse/persistence/testing/sdo/model/dataobject/xpathengine/PurchaseOrderXPathEngine.xsd");
            List types = xsdHelper.define(is, null);
            XMLDocument document = xmlHelper.load(new FileInputStream("org/eclipse/persistence/testing/sdo/model/dataobject/xpathengine/purchaseOrderXPathEngine.xml"));
            rootObject = document.getRootObject();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the xsd");
        }
    }

    // ------------------- Literal tests -------------------//
    public void testQuotedStringQueryWithoutWhitespace1() {
        Object returnValue = rootObject.get("items/item[partNum= \'926-AA\']");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Baby Monitor", ((DataObject)returnValue).get("productName"));
    }
    
    public void testQuotedStringQueryWithoutWhitespace2() {
        Object returnValue = rootObject.get("items/item[partNum = \"926-AA\"]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Baby Monitor", ((DataObject)returnValue).get("productName"));
    }
   
    public void testQuotedStringQueryWithoutWhitespace3() {
        Object returnValue = rootObject.get("items/item[partNum =\"926-AA\"]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Baby Monitor", ((DataObject)returnValue).get("productName"));
    }
    
    public void testInvalidQuotedStringQueryWithWhitespace1() {
        Object returnValue = rootObject.get("items/item[partNum=\' 926-AA\']");
        // should fail
        assertTrue(returnValue == null);
    }
    
    public void testInvalidQuotedStringQueryWithWhitespace2() {
        Object returnValue = rootObject.get("items/item[partNum = \" 926-AA\"]");
        // should fail
        assertTrue(returnValue == null);
    }
    
    public void testInvalidQuotedStringQueryWithWhitespace3() {
        Object returnValue = rootObject.get("items/item[partNum= \" 926-AA\"]");
        // should fail
        assertTrue(returnValue == null);
    }

    public void testInvalidQuotedStringQueryWithWhitespace4() {
        Object returnValue = rootObject.get("items/item[partNum =\" 926-AA\"]");
        // should fail
        assertTrue(returnValue == null);
    }

    public void testValidQuotedStringQueryWithWhitespace1() {
        Object returnValue = rootObject.get("items/item[partNum=\' 872-AA\']");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Lawnmower", ((DataObject)returnValue).get("productName"));
    }
    
    public void testValidQuotedStringQueryWithWhitespace2() {
        Object returnValue = rootObject.get("items/item[partNum = \" 872-AA\"]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Lawnmower", ((DataObject)returnValue).get("productName"));
    }
    
    public void testValidQuotedStringQueryWithWhitespace3() {
        Object returnValue = rootObject.get("items/item[partNum= \" 872-AA\"]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Lawnmower", ((DataObject)returnValue).get("productName"));
    }

    public void testValidQuotedStringQueryWithWhitespace4() {
        Object returnValue = rootObject.get("items/item[partNum =\" 872-AA\"]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Lawnmower", ((DataObject)returnValue).get("productName"));
    }

    public void testStringQueryWithWhitespaces1() {
        Object returnValue = rootObject.get("items/item[partNum= 926-AA]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Baby Monitor", ((DataObject)returnValue).get("productName"));
    }
    
    public void testStringQueryWithWhitespaces2() {
        Object returnValue = rootObject.get("items/item[partNum =926-AA]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Baby Monitor", ((DataObject)returnValue).get("productName"));
    }
    
    public void testStringQueryWithWhitespaces3() {
        Object returnValue = rootObject.get("items/item[partNum = 926-AA]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Baby Monitor", ((DataObject)returnValue).get("productName"));
    }

    // ------------------- Number tests -------------------//
    public void testFloatQueryWithWhitespaces1() {
        Object returnValue = rootObject.get("items/item[USPrice = 148.95]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Lawnmower", ((DataObject)returnValue).get("productName"));
    }
    
    public void testFloatQueryWithWhitespaces2() {
        Object returnValue = rootObject.get("items/item[USPrice= 148.95]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Lawnmower", ((DataObject)returnValue).get("productName"));
    }
    
    public void testFloatQueryWithWhitespaces3() {
        Object returnValue = rootObject.get("items/item[USPrice =148.95]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Lawnmower", ((DataObject)returnValue).get("productName"));
    }

    // ------------------- Boolean tests -------------------//
    public void testBooleanQueryWithWhitespaces1() {
        Object returnValue = rootObject.get("items/item[specialOrder = true]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Lawnmower", ((DataObject)returnValue).get("productName"));
    }

    public void testBooleanQueryWithWhitespaces2() {
        Object returnValue = rootObject.get("items/item[specialOrder= true]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Lawnmower", ((DataObject)returnValue).get("productName"));
    }

    public void testBooleanQueryWithWhitespaces3() {
        Object returnValue = rootObject.get("items/item[specialOrder =true]");
        assertTrue(returnValue instanceof DataObject);
        assertEquals("Lawnmower", ((DataObject)returnValue).get("productName"));
    }
    
    // ------------------- Set with unreachable path tests -------------------//
    public void testSetPropertyOnNullDataObject() {
        boolean expectedEx = false;
        boolean unexpectedEx = false;
        
        try {
            SDODataFactory factory =  (SDODataFactory) getHelperContext().getDataFactory();
            SDODataObject po = (SDODataObject) factory.create("http://www.example.org", "PurchaseOrderType");
            po.set("customer/gender", "male");
        } catch (SDOException sdoex) {
            // ensure the correct SDO exception was thrown
            if (sdoex.getMessage().lastIndexOf("Cannot perform operation on property") != -1) {
                expectedEx = true;
            }
        } catch (Exception x) {
            unexpectedEx = true;
        }
        assertTrue("An SDOException was not thrown as expceted.", expectedEx);
        assertFalse("An unexpected exception occurred.", unexpectedEx);
    }

    public void testSetPropertyOnNullNestedDataObject() {
        boolean expectedEx = false;
        boolean unexpectedEx = false;
        
        try {
            SDODataFactory factory =  (SDODataFactory) getHelperContext().getDataFactory();
            SDODataObject po = (SDODataObject) factory.create("http://www.example.org", "PurchaseOrderType");
            SDODataObject items = (SDODataObject) po.createDataObject("items");
            items.createDataObject("item");
            po.set("items/item.0/partslist/partNumber", "989");
        } catch (SDOException sdoex) {
            // ensure the correct SDO exception was thrown
            if (sdoex.getMessage().lastIndexOf("Cannot perform operation on property") != -1) {
                expectedEx = true;
            }
        } catch (Exception x) {
            unexpectedEx = true;
        }
        assertTrue("An SDOException was not thrown as expceted.", expectedEx);
        assertFalse("An unexpected exception occurred.", unexpectedEx);
    }

    public void testSetPropertyOnNullListWrapper() {
        boolean expectedEx = false;
        boolean unexpectedEx = false;
        
        try {
            SDODataFactory factory =  (SDODataFactory) getHelperContext().getDataFactory();
            SDODataObject po = (SDODataObject) factory.create("http://www.example.org", "PurchaseOrderType");
            po.createDataObject("items");
            po.set("items/item[1]/productName", "Gizmo");
        } catch (SDOException sdoe) {
            expectedEx = true;
        } catch (Exception x) {
            unexpectedEx = true;
        }
        assertTrue("An SDOException was not thrown as expceted.", expectedEx);
        assertFalse("An unexpected exception occurred.", unexpectedEx);
    }
}
