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
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathengine;

import java.util.List;
import commonj.sdo.DataObject;
import junit.textui.TestRunner;

public class XPathHelperLogicalOPTestCases extends XPathHelperTestCases {
    public XPathHelperLogicalOPTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.xpathengine.XPathHelperLogicalOPTestCases" };
        TestRunner.main(arguments);
    }

    public void testANDQuery() {
        List returnValue = xpathHelper.evaluate("items/item[quantity=1 and specialOrder='true']", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 1);
        DataObject mower = (DataObject) returnValue.get(0);

        returnValue = xpathHelper.evaluate("productName", mower);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Lawnmower", pName);
    }

    public void testANDORANDQuery() {
        List returnValue = xpathHelper.evaluate("items/item[quantity=1 and specialOrder='true' or quantity=5 and specialOrder='false']", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 2);
        DataObject mower = (DataObject) returnValue.get(0);
        DataObject battery = (DataObject) returnValue.get(1);

        returnValue = xpathHelper.evaluate("productName", mower);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Lawnmower", pName);

        returnValue = xpathHelper.evaluate("productName", battery);
        assertTrue(returnValue != null && returnValue.size() != 0);
        pName = (String) returnValue.get(0);
        assertEquals("Battery", pName);
    }
    
    public void testANDQueryWithNE() {
        List returnValue = xpathHelper.evaluate("items/item[quantity!=3 and specialOrder='true']", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 1);
        DataObject mower = (DataObject) returnValue.get(0);

        returnValue = xpathHelper.evaluate("productName", mower);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Lawnmower", pName);
    }

    public void testORQueryWithGT() {
        List returnValue = xpathHelper.evaluate("items/item[quantity>1 or specialOrder='false']", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 3);
    }

    public void testORQueryWithLT() {
        List returnValue = xpathHelper.evaluate("items/item[quantity<5 or specialOrder='false']", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 4);
    }
    
    public void testANDORQueryWithParenthesis() {
        List returnValue = xpathHelper.evaluate("items/item[quantity=5 and (specialOrder='false' or productName='Lawnmower')]", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 1);
        DataObject battery = (DataObject) returnValue.get(0);

        returnValue = xpathHelper.evaluate("productName", battery);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Battery", pName);
    }

    public void testANDORQueryWithoutParenthesis() {
        List returnValue = xpathHelper.evaluate("items/item[quantity=5 and specialOrder='false' or productName='Lawnmower']", rootObject);
        assertTrue(returnValue != null && returnValue.size() == 2);
        DataObject mower = (DataObject) returnValue.get(0);
        DataObject battery = (DataObject) returnValue.get(1);

        returnValue = xpathHelper.evaluate("productName", mower);
        assertTrue(returnValue != null && returnValue.size() != 0);
        String pName = (String) returnValue.get(0);
        assertEquals("Lawnmower", pName);

        returnValue = xpathHelper.evaluate("productName", battery);
        assertTrue(returnValue != null && returnValue.size() != 0);
        pName = (String) returnValue.get(0);
        assertEquals("Battery", pName);
    }
}
