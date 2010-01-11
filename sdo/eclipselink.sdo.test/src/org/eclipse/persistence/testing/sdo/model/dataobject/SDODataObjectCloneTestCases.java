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
package org.eclipse.persistence.testing.sdo.model.dataobject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class SDODataObjectCloneTestCases extends SDOTestCase {

    private SDODataObject purchaseOrder;
    
    public SDODataObjectCloneTestCases(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
         String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectCloneTestCases" };
         TestRunner.main(arguments);
    }
    
    public void setUp() {
        super.setUp();
        String xsdString = getXSDString("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeep.xsd");

        // Generate a list of sdo types based on the purchaseOrder.xsd and print
        // out info about them
        List types = xsdHelper.define(xsdString);

        // create a Purchase Order dataObject and write it to XML called
        try {
            FileInputStream inStream = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderNSDeep.xml");
            XMLDocument document = xmlHelper.load(inStream);
            purchaseOrder = (SDODataObject)document.getRootObject();
            inStream.close();
        } catch (IOException e) {
            fail("An IOException occurred during setup.");
        }
    }

    private String getXSDString(String filename) {
        try {
            FileInputStream inStream = new FileInputStream(filename);
            byte[] bytes = new byte[inStream.available()];
            inStream.read(bytes);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void testListClone() {
        DataObject items = purchaseOrder.getDataObject("items");
        ListWrapper lineItems = (ListWrapper)items.getList("item");
        ListWrapper lineItemsClone = (ListWrapper)lineItems.clone();
        
        assertFalse(lineItems == lineItemsClone);
        
        assertFalse(lineItems.getCurrentElements() == lineItemsClone.getCurrentElements());
        
        assertTrue(lineItems.size() == lineItemsClone.size());
        assertTrue(lineItems.get(0) == lineItemsClone.get(0));
        assertTrue(lineItems.get(1) == lineItemsClone.get(1));
    }

}
