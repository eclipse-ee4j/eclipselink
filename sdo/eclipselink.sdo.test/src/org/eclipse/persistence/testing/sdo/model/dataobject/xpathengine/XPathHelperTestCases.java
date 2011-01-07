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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathengine;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.eclipse.persistence.sdo.helper.extension.XPathHelper;

public class XPathHelperTestCases extends SDOTestCase {
    protected DataObject rootObject;
    protected XPathHelper xpathHelper;

    public XPathHelperTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.xpathengine.XPathHelperTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();
        try {
            InputStream is = new FileInputStream("org/eclipse/persistence/testing/sdo/model/dataobject/xpathengine/PurchaseOrderXPathEngine.xsd");
            List types = xsdHelper.define(is, null);
            XMLDocument document = xmlHelper.load(new FileInputStream("org/eclipse/persistence/testing/sdo/model/dataobject/xpathengine/purchaseOrderXPathEngine.xml"));
            rootObject = document.getRootObject();
            xpathHelper = XPathHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the xsd");
        }
    }
    
    public void testGetContainer() {
        List returnValue = xpathHelper.evaluate("items", rootObject);
        assertTrue(returnValue != null);
        DataObject doa = (DataObject) returnValue.get(0);
        List containerValue = xpathHelper.evaluate("..", doa);
        assertTrue(returnValue != null);
        DataObject cdo = (DataObject) containerValue.get(0);
        assertEquals(rootObject, cdo);
    }
    
    public void testGetAllItems() {
        List returnValue = xpathHelper.evaluate("items/item/", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 4);
        DataObject mower = (DataObject) returnValue.get(0);
        DataObject monitor = (DataObject) returnValue.get(1);
        DataObject battery = (DataObject) returnValue.get(2);
        DataObject rake = (DataObject) returnValue.get(3);

        returnValue = xpathHelper.evaluate("productName", battery);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Battery", pName);

        returnValue = xpathHelper.evaluate("productName", rake);
        assertTrue(returnValue != null && returnValue.size() != 0);
        pName = (String) returnValue.get(0);
        assertEquals("Rake", pName);

        returnValue = xpathHelper.evaluate("productName", mower);
        assertTrue(returnValue != null && returnValue.size() != 0);
        pName = (String) returnValue.get(0);
        assertEquals("Lawnmower", pName);

        returnValue = xpathHelper.evaluate("productName", monitor);
        assertTrue(returnValue != null && returnValue.size() != 0);
        pName = (String) returnValue.get(0);
        assertEquals("Baby Monitor", pName);
    }
    
    public void testGetCustomerName() {
        List returnValue = xpathHelper.evaluate("customer/name", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 1);
        String name = (String) returnValue.get(0);
        assertEquals("Sally", name);
    }
    
    public void testGetNamePrefixWithoutAt() {
        List returnValue = xpathHelper.evaluate("customer/namePrefix", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 1);
        String name = (String) returnValue.get(0);
        assertEquals("Dr.", name);
    }
    
    public void testGetNamePrefixWithAt() {
        List returnValue = xpathHelper.evaluate("customer/@namePrefix", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 1);
        String name = (String) returnValue.get(0);
        assertEquals("Dr.", name);
    }

    public void testGetAllPartNumbers() {
        List returnValue = xpathHelper.evaluate("items/item/partsList/partNumber", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 10);
        ArrayList compareToList = new ArrayList();
        compareToList.add("1");
        compareToList.add("2");
        compareToList.add("3");
        compareToList.add("4");
        compareToList.add("5");
        compareToList.add("6");
        compareToList.add("1");
        compareToList.add("8");
        compareToList.add("9");
        compareToList.add("23");
        assertTrue(returnValue.containsAll(compareToList));
    }
    
    public void testGetAllPOComments() {
        List returnValue = xpathHelper.evaluate("comment", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 2);
        String comment = (String) returnValue.get(0);
        assertEquals("Hurry, my lawn is going wild!", comment);
        comment = (String) returnValue.get(1);
        assertEquals("I love growing grass", comment);
    }

    public void testGetAllLineItemComments() {
        List returnValue = xpathHelper.evaluate("items/item/comment", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 3);
        String comment = (String) returnValue.get(0);
        assertEquals("Confirm this is electric", comment);
        comment = (String) returnValue.get(1);
        assertEquals("This item requires batteries", comment);
        comment = (String) returnValue.get(2);
        assertEquals("Also runs on AC", comment);
    }

    public void testGetCDNPricedItem() {
        List returnValue = xpathHelper.evaluate("items/item/CDNPrice", rootObject);
        // 20070618: an unset value with an IsSetNodeNullPolicy will return the default value increasing the list size to 4
        assertTrue(returnValue != null && returnValue.size() == 4);
        Float price = (Float) returnValue.get(0);
        assertEquals(new Float("0.00"), price);
        price = (Float) returnValue.get(1);
        assertEquals(new Float("39.98"), price);
        price = (Float) returnValue.get(2);
        assertEquals(new Float("2.98"), price);
        price = (Float) returnValue.get(3);
        assertEquals(new Float("12.98"), price);
    }

    public void testMultipleBracketExpression() {
        List returnValue = xpathHelper.evaluate("items/item[2]/partsList/partNumber[4]", rootObject);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pNum = (String) returnValue.get(0);
        assertEquals("23", pNum);
    }

    public void testGetFirstItemViaBracket() {
        List returnValue = xpathHelper.evaluate("items/item[1]", rootObject);
        assertTrue(returnValue != null && returnValue.size() != 0);
        DataObject dao = (DataObject) returnValue.get(0);

        returnValue = xpathHelper.evaluate("productName", dao);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Lawnmower", pName);
    }
    
    public void testGetPartsListFromSecondItemViaBracket() {
        List returnValue = xpathHelper.evaluate("items/item[2]/partsList/partNumber", rootObject);
        assertTrue(returnValue != null && returnValue.size() != 0);
        ArrayList compareToList = new ArrayList();
        compareToList.add("1");
        compareToList.add("8");
        compareToList.add("9");
        compareToList.add("23");
        assertTrue(returnValue.containsAll(compareToList));
    }

    public void testInvalidPositionalExpression() {
        boolean exceptionWasThrown = false;
        try {
            xpathHelper.evaluate("items/item[5]/partsList/partNumber", rootObject);
        } catch (IndexOutOfBoundsException ibe) {
            exceptionWasThrown = true;
        }
        assertTrue(exceptionWasThrown);
    }

    public void testSelfExpression() {
        List returnValue = xpathHelper.evaluate("./items/item[2]/partsList/partNumber/", rootObject);
        assertTrue(returnValue != null && returnValue.size() != 0);
        ArrayList compareToList = new ArrayList();
        compareToList.add("1");
        compareToList.add("8");
        compareToList.add("9");
        compareToList.add("23");
        assertTrue(returnValue.containsAll(compareToList));
    }

    public void testNestedSelfExpression() {
        List returnValue = xpathHelper.evaluate("items/item[2]/partsList/./partNumber/", rootObject);
        assertTrue(returnValue != null && returnValue.size() != 0);
        ArrayList compareToList = new ArrayList();
        compareToList.add("1");
        compareToList.add("8");
        compareToList.add("9");
        compareToList.add("23");
        assertTrue(returnValue.containsAll(compareToList));
    }

    public void testParentExpression() {
        List returnValue = xpathHelper.evaluate("items", rootObject);
        assertTrue(returnValue != null && returnValue.size() != 0);
        DataObject itemsDao = (DataObject) returnValue.get(0);
        returnValue = xpathHelper.evaluate("../items/item[1]/partsList/partNumber/", itemsDao);
        assertTrue(returnValue != null && returnValue.size() != 0);
        ArrayList compareToList = new ArrayList();
        compareToList.add("1");
        compareToList.add("2");
        compareToList.add("3");
        compareToList.add("4");
        compareToList.add("5");
        compareToList.add("6");
        assertTrue(returnValue.containsAll(compareToList));
    }
    
    public void testNestedParentExpression() {
        List returnValue = xpathHelper.evaluate("items/item[1]/partsList/../partsList/partNumber/", rootObject);
        assertTrue(returnValue != null && returnValue.size() != 0);
        ArrayList compareToList = new ArrayList();
        compareToList.add("1");
        compareToList.add("2");
        compareToList.add("3");
        compareToList.add("4");
        compareToList.add("5");
        compareToList.add("6");
        assertTrue(returnValue.containsAll(compareToList));
    }
    
    public void testInvalidNSPrefixExpression() {
        List returnValue = xpathHelper.evaluate("ns0:items/item[2]/partsList/partNumber/", rootObject);
        assertTrue(returnValue == null || returnValue.size() == 0);
    }

    public void testPositionalFloatExpression() {
        List returnValue = xpathHelper.evaluate("items/item[1.9]", rootObject);
        assertTrue(returnValue != null && returnValue.size() != 0);
        DataObject dao = (DataObject) returnValue.get(0);

        returnValue = xpathHelper.evaluate("productName", dao);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Lawnmower", pName);
    }
        
    public void testBrokenExpression() {
        List returnValue = xpathHelper.evaluate("items//>item[name=-!james]", rootObject);
        assertTrue(returnValue == null || returnValue.size() == 0);
    }

    // -------------------- Not supported in XPathHelper -------------------- //
    /*
    public void testGetFirstItemViaDot() {
        List returnValue = xpathHelper.evaluate("items/item.0", rootObject);
        assertTrue(returnValue != null && returnValue.size() != 0);
        DataObject dao = (DataObject) returnValue.get(0);

        returnValue = xpathHelper.evaluate("productName", dao);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Lawnmower", pName);
    }
    
    public void testGetPartsListFromSecondItemViaDot() {
        List returnValue = xpathHelper.evaluate("items/item.1/partsList/partNumber", rootObject);
        assertTrue(returnValue != null && returnValue.size() != 0);
        ArrayList compareToList = new ArrayList();
        compareToList.add("1");
        compareToList.add("8");
        compareToList.add("9");
        compareToList.add("23");
        assertTrue(returnValue.containsAll(compareToList));
    }
    */
}
