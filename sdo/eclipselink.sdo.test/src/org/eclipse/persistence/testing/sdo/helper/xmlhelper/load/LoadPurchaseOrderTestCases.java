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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.load;

import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

public class LoadPurchaseOrderTestCases extends SDOXMLHelperLoadTestCases {
    public LoadPurchaseOrderTestCases(String name) {
        super(name);
    }

    protected String getFileNameToLoad() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/purchaseOrderNS.xml");
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrder.xsd";
    }

    protected void verifyDocument(XMLDocument document) {
        super.verifyDocument(document);
        assertEquals("purchaseOrder", document.getRootElementName());
        assertEquals("http://www.example.org", document.getRootElementURI());
        assertEquals(null, document.getEncoding());
        assertEquals(null, document.getXMLVersion());
        assertEquals(null, document.getNoNamespaceSchemaLocation());
        assertEquals(null, document.getSchemaLocation());
        assertTrue(document.isXMLDeclaration());

        java.util.List properties = document.getRootObject().getInstanceProperties();
        assertEquals(6, properties.size());
        for (int i = 0; i < properties.size(); i++) {
            Property property = (Property)properties.get(i);
            boolean isSet = document.getRootObject().isSet(property);
            if (property.getName().equals("poId")) {
                assertFalse(isSet);
            } else if (property.getName().equals("shipTo")) {
                assertTrue(isSet);
                Object value = document.getRootObject().get(property);
                assertNotNull(value);
            } else if (property.getName().equals("billTo")) {
                assertTrue(isSet);
                Object value = document.getRootObject().get(property);
                assertNotNull(value);
            } else if (property.getName().equals("comment")) {
                assertTrue(isSet);
                Object value = document.getRootObject().get(property);
                assertEquals("Hurry, my lawn is going wild!", value);
            } else if (property.getName().equals("items")) {
                assertTrue(isSet);
                Object value = document.getRootObject().get(property);
                assertNotNull(value);
            } else if (property.getName().equals("orderDate")) {
                assertTrue(isSet);
                Object value = document.getRootObject().get(property);
                assertEquals("1999-10-20", value);
            } else {
                fail("An unexpected property was present " + property.getName());
            }
        }
    }

    protected void createTypes() {
    }
}
