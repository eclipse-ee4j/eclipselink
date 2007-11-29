/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;

import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;

public class LoadAndSaveMimeTypeOnXSDTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveMimeTypeOnXSDTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();

        XMLMarshaller aMarshaller = ((SDOXMLHelper)xmlHelper).getXmlMarshaller();
        XMLUnmarshaller anUnmarshaller = ((SDOXMLHelper)xmlHelper).getXmlUnmarshaller();
        XMLAttachmentMarshaller anAttachmentMarshaller = new AttachmentMarshallerImpl();
        XMLAttachmentUnmarshaller anAttachmentUnmarshaller = new AttachmentUnmarshallerImpl();
        aMarshaller.setAttachmentMarshaller(anAttachmentMarshaller);
        anUnmarshaller.setAttachmentUnmarshaller(anAttachmentUnmarshaller);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/EmployeeWithMimeTypeOnXSD.xsd";        
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/EmployeeWithMimeTypeOnXSD.xml");
    }

    protected String getNoSchemaControlFileName() {
      //return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/EmployeeWithMimeTypeOnXSD.xml");
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/EmployeeWithMimeTypeOnXSDNoSchema.xml");
    }

    protected String getControlRootURI() {
        return NON_DEFAULT_URI;
    }

    protected String getControlRootName() {
        return "employeeType";
    }
    
    protected String getRootInterfaceName() {
        return "EmployeeType";
    }

    public void registerTypes() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type bytesType = typeHelper.getType("commonj.sdo", "Bytes");

        // create a new Type for Customers
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");
        customerType.set("uri", getControlRootURI());
        customerType.set("name", "EmployeeType");
        
        // create an idproperty        
        addProperty(customerType, "id", stringType, true, false, true);
        
        // create a first name property        
        addProperty(customerType, "name", stringType, true, false, true);        

        // create a photo property        
        DataObject photoProp = addProperty(customerType, "photo", bytesType, true, false, true);        
        photoProp.set(SDOConstants.MIME_TYPE_PROPERTY, "image/jpeg");
        
        // now define the Customer type so that customers can be made
        Type customerSDOType = typeHelper.define(customerType);
        
        DataObject propDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        propDO.set("name", getControlRootName());
        propDO.set("type", customerSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveMimeTypeOnXSDTestCases" };
        TestRunner.main(arguments);
    }
}