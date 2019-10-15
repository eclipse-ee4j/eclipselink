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
import commonj.sdo.helper.XMLDocument;

import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

public class LoadAndSaveNillableIsSetNodeNullPolicyTrueTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveNillableIsSetNodeNullPolicyTrueTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/DirectIsSetNodeNullPolicyTrueElement.xsd";
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nillable/DirectIsSetNodeNullPolicyTrueElement.xml";
    }

    protected String getControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nillable/DirectIsSetNodeNullPolicyTrueElementWrite.xml";
    }

    protected String getNoSchemaControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nillable/DirectIsSetNodeNullPolicyTrueElementNoSchema.xml";
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/nillable/DirectIsSetNodeNullPolicyTrueElementNoSchema.xml";
    }

    protected String getControlRootURI() {
        return NON_DEFAULT_URI;
    }

    protected String getControlRootName() {
        return "employee";
    }

     protected String getRootInterfaceName() {
        return "EmployeeType";
    }

     // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
     protected List<String> getPackages() {
         List<String> packages = new ArrayList<String>();
         packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
         return packages;
     }

    protected void verifyAfterLoad(XMLDocument doc) {
        super.verifyAfterLoad(doc);
        Object value = doc.getRootObject().get("id");
        boolean isSet = doc.getRootObject().isSet("id");
        assertNull(value);
        assertTrue(isSet);

        value = doc.getRootObject().get("first-name");
        isSet = doc.getRootObject().isSet("first-name");
        assertNull(value);
        assertFalse(isSet);

        value = doc.getRootObject().get("task");
        isSet = doc.getRootObject().isSet("task");
        assertTrue(value instanceof List);
        assertTrue(((List)value).size() == 0);
        assertFalse(isSet);

        value = doc.getRootObject().get("last-name");
        isSet = doc.getRootObject().isSet("last-name");
        assertNotNull(value);
        assertTrue(isSet);

        value = doc.getRootObject().get("address");
        isSet = doc.getRootObject().isSet("address");
        assertNull(value);
        assertTrue(isSet);
    }

    public void registerTypes() {
        SDOType stringType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.STRING);
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);

        //ADDRESS TYPE
        DataObject addressTypeDO = dataFactory.create(typeType);
        addressTypeDO.set("uri", getControlRootURI());
        addressTypeDO.set("name", "AddressType");

        DataObject addressCityProperty = addProperty(addressTypeDO, "city", stringType, false, false, true);
        addressCityProperty.set("nullable", true);
        Type addressType = typeHelper.define(addressTypeDO);

        // create a new Type for Customers
        DataObject customerType = dataFactory.create(typeType);
        customerType.set("uri", getControlRootURI());
        customerType.set("name", "EmployeeType");
        //customerType.set("name", "Employee");
        // create an idproperty
        DataObject idProp = addProperty(customerType, "id", stringType, false, false, true);
        idProp.set("nullable", true);
        DataObject fnameProp = addProperty(customerType, "first-name", stringType, false, false, true);
        fnameProp.set("nullable", true);
        addProperty(customerType, "task", stringType, false, true, true);
        addProperty(customerType, "last-name", stringType, false, false, true);
        DataObject addressProp = addProperty(customerType, "address", addressType, true, false, true);
        addressProp.set("nullable", true);

        // now define the Customer type so that customers can be made
        Type customerSDOType = typeHelper.define(customerType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", customerSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveNillableIsSetNodeNullPolicyTrueTestCases" };
        TestRunner.main(arguments);
    }
}
