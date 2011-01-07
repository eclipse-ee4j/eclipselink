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

import java.util.ArrayList;
import java.util.List;
import commonj.sdo.DataObject;
import junit.textui.TestRunner;

public class XPathHelperRelationalOPTestCases extends XPathHelperTestCases {
    public XPathHelperRelationalOPTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.xpathengine.XPathHelperRelationalOPTestCases" };
        TestRunner.main(arguments);
    }

    public void testSimpleQuery() {
        List returnValue = xpathHelper.evaluate("customer[name='Sally']", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 1);
        DataObject customer = (DataObject) returnValue.get(0);
        returnValue = xpathHelper.evaluate("name", customer);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String name = (String) returnValue.get(0);
        assertEquals("Sally", name);
    }
    
    public void testMultipleResultEQQuery() {
        List returnValue = xpathHelper.evaluate("items/item[quantity=\"1\"]", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 2);
        DataObject mower = (DataObject) returnValue.get(0);
        DataObject monitor = (DataObject) returnValue.get(1);

        returnValue = xpathHelper.evaluate("productName", mower);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Lawnmower", pName);

        returnValue = xpathHelper.evaluate("productName", monitor);
        assertTrue(returnValue != null && returnValue.size() != 0);
        pName = (String) returnValue.get(0);
        assertEquals("Baby Monitor", pName);
    }

    public void testMultipleResultNEQQuery() {
        List returnValue = xpathHelper.evaluate("items/item[quantity!=1]", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 2);
        DataObject battery = (DataObject) returnValue.get(0);
        DataObject rake = (DataObject) returnValue.get(1);

        returnValue = xpathHelper.evaluate("productName", battery);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Battery", pName);

        returnValue = xpathHelper.evaluate("productName", rake);
        assertTrue(returnValue != null && returnValue.size() != 0);
        pName = (String) returnValue.get(0);
        assertEquals("Rake", pName);
    }

    public void testMultipleResultEQQueryWithAt() {
        List returnValue = xpathHelper.evaluate("items/item[@quantity=1]", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 2);
        DataObject mower = (DataObject) returnValue.get(0);
        DataObject monitor = (DataObject) returnValue.get(1);

        returnValue = xpathHelper.evaluate("productName", mower);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Lawnmower", pName);

        returnValue = xpathHelper.evaluate("productName", monitor);
        assertTrue(returnValue != null && returnValue.size() != 0);
        pName = (String) returnValue.get(0);
        assertEquals("Baby Monitor", pName);
    }

    public void testCDNPricedItemLessThan() {
        List returnValue = xpathHelper.evaluate("items/item[CDNPrice < 100.00]", rootObject);
        // 20070618: an unset value with an IsSetNodeNullPolicy will return the default value increasing the list size to 4
        assertTrue(returnValue != null && returnValue.size() == 4);
    }

    public void testCDNPricedItemGreaterThan() {
        List returnValue = xpathHelper.evaluate("items/item[CDNPrice > 10.40]", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 2);
    }

    public void testCDNPricedItemGreaterThanOrEqualTo() {
        List returnValue = xpathHelper.evaluate("items/item[CDNPrice >= 12.98]", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 2);
    }

    public void testShipDateEQ() {
        List returnValue = xpathHelper.evaluate("items/item[shipDate = 1999-05-21]", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 1);
        DataObject monitor = (DataObject) returnValue.get(0);
        returnValue = xpathHelper.evaluate("productName", monitor);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Baby Monitor", pName);
    }

    public void testShipDateNEQNoResult() {
        List returnValue = xpathHelper.evaluate("items/item[shipDate != 1999-05-21]", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 2);
    }

    public void testShipDateNEQ() {
        List returnValue = xpathHelper.evaluate("items/item[shipDate != 2007-05-31]", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 3);
    }

    public void testSingleChildQuery() {
        List returnValue = xpathHelper.evaluate("items/item[quantity=\"5\"]/productName", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 1);
        String pName = (String) returnValue.get(0);
        assertEquals("Battery", pName);
    }

    public void testSingleMultipleQuery() {
        List returnValue = xpathHelper.evaluate("items/item[quantity=\"1\"]/productName", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 2);
        String pName = (String) returnValue.get(0);
        assertEquals("Lawnmower", pName);
        pName = (String) returnValue.get(1);
        assertEquals("Baby Monitor", pName);
    }
}
