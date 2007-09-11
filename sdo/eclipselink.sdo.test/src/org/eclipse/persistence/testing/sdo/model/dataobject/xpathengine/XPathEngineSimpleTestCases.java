/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
import org.eclipse.persistence.testing.sdo.SDOTestCase;

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
}