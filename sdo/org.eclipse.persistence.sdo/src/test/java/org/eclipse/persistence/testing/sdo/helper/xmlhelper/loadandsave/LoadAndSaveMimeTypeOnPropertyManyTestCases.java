/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;

import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;

public class LoadAndSaveMimeTypeOnPropertyManyTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveMimeTypeOnPropertyManyTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();

        XMLMarshaller aMarshaller = ((SDOXMLHelper)xmlHelper).getXmlMarshaller();
        XMLUnmarshaller anUnmarshaller = ((SDOXMLHelper)xmlHelper).getXmlUnmarshaller();
        XMLAttachmentMarshaller anAttachmentMarshaller = new AttachmentMarshallerImpl("c_id0");
        XMLAttachmentUnmarshaller anAttachmentUnmarshaller = new AttachmentUnmarshallerImpl("Testing".getBytes());
        aMarshaller.setAttachmentMarshaller(anAttachmentMarshaller);
        anUnmarshaller.setAttachmentUnmarshaller(anAttachmentUnmarshaller);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/EmployeeWithMimeTypeOnPropertyManyCase.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/EmployeeWithMimeTypeOnPropertyManyCase.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/EmployeeWithMimeTypeOnPropertyManyCaseNoSchema.xml");
    }

    protected String getControlRootURI() {
        return "http://www.example.org";
    }

    protected String getControlRootName() {
        return "employeeType";
    }

    protected String getRootInterfaceName() {
        return "EmployeeType";
    }

    public void registerTypes() {
        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");
        SDOType bytesType = (SDOType) typeHelper.getType("commonj.sdo", "Bytes");
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        // create a new Type for Customers
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");
        customerType.set("uri", getControlRootURI());
        customerType.set("name", "EmployeeType");

        // create an idproperty
        addProperty(customerType, "id", stringType, false, false, true);

        // create a first name property
        addProperty(customerType, "name", stringType, false, false, true);

        // create a photo property
        DataObject photoProp = addProperty(customerType, "photo", bytesType, false, true, true);
        photoProp.set(SDOConstants.MIME_TYPE_PROPERTY_PROPERTY, "photoMimeType");

        addProperty(customerType, "photoMimeType", stringType, false, false, true);

        // now define the Customer type so that customers can be made
         Type customerSDOType = typeHelper.define(customerType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", customerSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveMimeTypeOnPropertyManyTestCases" };
        TestRunner.main(arguments);
    }
}
