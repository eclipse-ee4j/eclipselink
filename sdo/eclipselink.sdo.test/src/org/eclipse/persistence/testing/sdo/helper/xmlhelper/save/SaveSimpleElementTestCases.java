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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.save;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class SaveSimpleElementTestCases extends SDOXMLHelperSaveTestCases {
    public SaveSimpleElementTestCases(String name) {
        super(name);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleElement.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleElementNoSchema.xml");
    }

    protected XMLDocument getXMLDocumentToSave() {
        DataObject dataObject = dataFactory.create(getControlRootURI(), CONTROL_ROOT_TYPE);
        Property firstNameProp = dataObject.getInstanceProperty(FIRSTNAME);
        dataObject.set(firstNameProp, CONTROL_FIRST_NAME);

        Property lastNameProp = dataObject.getInstanceProperty(LASTNAME);
        dataObject.set(lastNameProp, CONTROL_LAST_NAME);
        XMLDocument doc = xmlHelper.createDocument(dataObject, getControlRootURI(), getControlRootName());
        return doc;
    }

    public void registerTypes() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        // create a new Type for Customers
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");
        customerType.set("uri", CONTROL_ROOT_URI);
        customerType.set("name", CONTROL_ROOT_TYPE);

        // create a first name property
        DataObject firstNameProperty = customerType.createDataObject("property");
        firstNameProperty.set("name", FIRSTNAME);
        firstNameProperty.set("type", stringType);

        // create a last name property
        DataObject lastNameProperty = customerType.createDataObject("property");
        lastNameProperty.set("name", LASTNAME);
        lastNameProperty.set("type", stringType);

        // now define the Customer type so that customers can be made
        typeHelper.define(customerType);
    }
}
