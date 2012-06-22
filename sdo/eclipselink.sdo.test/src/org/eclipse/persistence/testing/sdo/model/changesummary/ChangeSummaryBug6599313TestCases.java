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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class ChangeSummaryBug6599313TestCases extends SDOTestCase {
    protected DataObject rootObject;

    public ChangeSummaryBug6599313TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryBug6599313TestCases" };
        TestRunner.main(arguments);
    }

    public String getSchemaName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCS.xsd");
    }

    protected String getModelFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCS.xml");
    }

    public void setUp() {
        super.setUp();        
        try {
            InputStream is = new FileInputStream(getSchemaName());
            List types = xsdHelper.define(is, null);
            XMLDocument document = xmlHelper.load(new FileInputStream(getModelFileName()));
            rootObject = document.getRootObject();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the xsd");
        }
    }

    public void testIsDeletedSingleCase() throws Exception {
        DataObject lineItem1 = (DataObject)rootObject.getDataObject("items").getList("item").get(0);
        assertNotNull(lineItem1);
        lineItem1.unset("product");
        rootObject.getChangeSummary().endLogging();
        rootObject.getChangeSummary().beginLogging();

        //create new object
        DataObject newProduct = dataFactory.create("http://www.example.org", "ProductType");
        newProduct.set("productName", "testProduct");

        lineItem1.set("product", newProduct);

        assertCreated(newProduct, rootObject.getChangeSummary());
        assertTrue(rootObject.getChangeSummary().isCreated(newProduct));

        //delete object
        newProduct.delete();
        assertFalse(rootObject.getChangeSummary().isDeleted(newProduct));
        assertUnchanged(newProduct, rootObject.getChangeSummary());
        rootObject.getChangeSummary().endLogging();
        assertFalse(rootObject.getChangeSummary().isDeleted(newProduct));
        assertUnchanged(newProduct, rootObject.getChangeSummary());
    }

    public void testIsDeletedManyCase() throws Exception {
        rootObject.getChangeSummary().endLogging();
        rootObject.getChangeSummary().beginLogging();

        List lineItems = rootObject.getDataObject("items").getList("item");
        assertNotNull(lineItems);
        assertEquals(2, lineItems.size());

        //create new object
        DataObject newLineItem = dataFactory.create("http://www.example.org", "LineItemType");
        newLineItem.set("partNum", "testPartNum");

        lineItems.add(newLineItem);
        assertEquals(3, lineItems.size());

        assertCreated(newLineItem, rootObject.getChangeSummary());
        assertTrue(rootObject.getChangeSummary().isCreated(newLineItem));
        //delete object
        newLineItem.delete();
        assertFalse(rootObject.getChangeSummary().isDeleted(newLineItem));
        assertUnchanged(newLineItem, rootObject.getChangeSummary());
        rootObject.getChangeSummary().endLogging();
        assertFalse(rootObject.getChangeSummary().isDeleted(newLineItem));
        assertUnchanged(newLineItem, rootObject.getChangeSummary());
    }
}
