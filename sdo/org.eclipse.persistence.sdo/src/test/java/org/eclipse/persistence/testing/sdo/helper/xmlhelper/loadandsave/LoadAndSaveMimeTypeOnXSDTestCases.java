/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

public class LoadAndSaveMimeTypeOnXSDTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveMimeTypeOnXSDTestCases(String name) {
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
        SDOType bytesType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.BYTES);
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);
        SDOType stringType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.STRING);
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);

        // create a new Type for Customers
        DataObject customerType = dataFactory.create(typeType);
        customerType.set("uri", getControlRootURI());
        customerType.set("name", "EmployeeType");

        // create an idproperty
        addProperty(customerType, "id", stringType, false, false, true);

        // create a first name property
        addProperty(customerType, "name", stringType, false, false, true);

        // create a photo property
        DataObject photoProp = addProperty(customerType, "photo", bytesType, false, false, true);
        photoProp.set(SDOConstants.MIME_TYPE_PROPERTY, "image/jpeg");

        // now define the Customer type so that customers can be made
        Type customerSDOType = typeHelper.define(customerType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", customerSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveMimeTypeOnXSDTestCases" };
        TestRunner.main(arguments);
    }
}
