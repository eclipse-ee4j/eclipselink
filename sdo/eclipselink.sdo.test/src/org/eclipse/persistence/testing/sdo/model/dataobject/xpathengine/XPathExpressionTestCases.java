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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.eclipse.persistence.sdo.helper.extension.XPathExpression;
import org.eclipse.persistence.sdo.helper.extension.XPathHelper;

public class XPathExpressionTestCases extends SDOTestCase {
    private DataObject rootObject;
    private XPathHelper xpathHelper;

    public XPathExpressionTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.xpathengine.XPathExpressionTestCases" };
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
    
    public void testBasicExpression() {
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
}
