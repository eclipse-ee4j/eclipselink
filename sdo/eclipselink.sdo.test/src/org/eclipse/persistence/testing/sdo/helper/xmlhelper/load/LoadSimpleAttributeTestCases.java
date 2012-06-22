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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.load;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class LoadSimpleAttributeTestCases extends SDOXMLHelperLoadTestCases {
    public LoadSimpleAttributeTestCases(String name) {
        super(name);
    }

    protected String getFileNameToLoad() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleAttribute.xml");
    }

    protected void verifyDocument(XMLDocument document) {
        super.verifyDocument(document);
        assertEquals(CONTROL_ROOT_NAME, document.getRootElementName());
        assertEquals(CONTROL_ROOT_URI, document.getRootElementURI());
        assertEquals(null, document.getEncoding());
        assertEquals(null, document.getXMLVersion());
        assertEquals(null, document.getNoNamespaceSchemaLocation());
        assertEquals(null, document.getSchemaLocation());
        java.util.List properties = document.getRootObject().getInstanceProperties();
        assertEquals(5, properties.size());
        for (int i = 0; i < properties.size(); i++) {
            Property property = (Property)properties.get(i);
            boolean isSet = document.getRootObject().isSet(property);
            if (property.getName().equals(CUSTOMERID)) {
                assertTrue(isSet);
                Object value = document.getRootObject().get(property);
                assertEquals(CONTROL_CUSTOMERID, value);
            } else if (property.getName().equals(SIN)) {
                assertTrue(isSet);
                Object value = document.getRootObject().get(property);
                assertEquals(CONTROL_SIN, value);
            } else {
                assertFalse(isSet);
            }
        }
    }

    protected void createTypes() {
        Type intType = typeHelper.getType("commonj.sdo", "Int");

        // create a new Type for Customers
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");
        customerType.set("uri", "http://www.example.org");
        customerType.set("name", "Customer");

        // create a first name property
        DataObject firstNameProperty = customerType.createDataObject("property");
        firstNameProperty.set("name", "firstName");
        firstNameProperty.set("type", intType);

        // create a last name property
        DataObject lastNameProperty = customerType.createDataObject("property");
        lastNameProperty.set("name", "lastName");
        lastNameProperty.set("type", intType);

        // now define the Customer type so that customers can be made
        typeHelper.define(customerType);
    }
}
