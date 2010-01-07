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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.save;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class SaveSimpleAttributeTestCases extends SDOXMLHelperSaveTestCases {
    public SaveSimpleAttributeTestCases(String name) {
        super(name);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleAttribute.xml");
    }

    protected XMLDocument getXMLDocumentToSave() {        
        DataObject dataObject = dataFactory.create(getControlRootURI(), CONTROL_ROOT_TYPE);        
        Property customerIDProp = dataObject.getInstanceProperty(CUSTOMERID);
        dataObject.set(customerIDProp, CONTROL_CUSTOMERID);

        Property sinProp = dataObject.getInstanceProperty(SIN);
        dataObject.set(sinProp, CONTROL_SIN);        
        XMLDocument doc = xmlHelper.createDocument(dataObject, getControlRootURI(), getControlRootName());        
        return doc;
    }

    public void registerTypes() {
                
        Type intType = typeHelper.getType("commonj.sdo", "Int");

        // create a new Type for Customers        
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");        
        customerType.set("uri", getControlRootURI());
        customerType.set("name", CONTROL_ROOT_TYPE);

        // create a first name property
        DataObject custIDProperty = customerType.createDataObject("property");
        custIDProperty.set("name", CUSTOMERID);
        custIDProperty.set("type", intType);

        // create a last name property
        DataObject sinProperty = customerType.createDataObject("property");
        sinProperty.set("name", SIN);
        sinProperty.set("type", intType);

        // now define the Customer type so that customers can be made
        typeHelper.define(customerType);
    }
}
