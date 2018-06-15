/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import java.util.ArrayList;
import java.util.List;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

public class LoadAndSaveEmptyElementTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveEmptyElementTestCases(String name) {
        super(name);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/empty/input.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/empty/input.xml");
    }

    protected String getControlWriteFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/empty/output.xml");
    }

    protected String getNoSchemaControlWriteFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/empty/output.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/empty/input.xml");
    }

    protected String getControlRootURI() {
        return "namespace1";
    }

    protected String getControlRootName() {
        return "root";
    }

    protected String getRootInterfaceName() {
        return "RootType";
    }

    protected void verifyAfterLoad (XMLDocument document) {
        super.verifyAfterLoad(document);
        DataObject rootDO = document.getRootObject();
        assertNull(rootDO.get("nillableEmptyString"));
        assertFalse(rootDO.isSet("nillableEmptyString"));

        assertNull(rootDO.get("nillableNilString"));
        assertTrue(rootDO.isSet("nillableNilString"));

        assertNull(rootDO.get("nonnillableEmptyString"));
        assertTrue(rootDO.isSet("nonnillableEmptyString"));

        assertNull(rootDO.get("nonnillableNilString"));
        assertTrue(rootDO.isSet("nonnillableNilString"));

        assertNull(rootDO.get("nillableEmptyDecimal"));
        assertFalse(rootDO.isSet("nillableEmptyDecimal"));

        assertNull(rootDO.get("nillableNilDecimal"));
        assertTrue(rootDO.isSet("nillableNilDecimal"));

        assertNull(rootDO.get("nonnillableEmptyDecimal"));
        assertTrue(rootDO.isSet("nonnillableEmptyDecimal"));

        assertNull(rootDO.get("nonnillableNilDecimal"));
        assertTrue(rootDO.isSet("nonnillableNilDecimal"));
     }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add("namespace1");
        return packages;
    }

    public void registerTypes() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type decimalType = typeHelper.getType("commonj.sdo", "Decimal");
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        // create a new Type for Customers
        DataObject rootType = dataFactory.create("commonj.sdo", "Type");
        rootType.set("uri", getControlRootURI());
        rootType.set("name", "rootType");

        DataObject prop = addProperty(rootType, "nillableEmptyString", stringType, false, false, true);
        prop.setBoolean("nullable", true);

        prop = addProperty(rootType, "nillableNilString", stringType, false, false, true);
        prop.setBoolean("nullable", true);

        addProperty(rootType, "nonnillableEmptyString", stringType, false, false, true);

        addProperty(rootType, "nonnillableNilString", stringType, false, false, true);

        prop = addProperty(rootType, "nillableEmptyDecimal", decimalType, false, false, true);
        prop.setBoolean("nullable", true);

        prop = addProperty(rootType, "nillableNilDecimal", decimalType, false, false, true);
        prop.setBoolean("nullable", true);

        addProperty(rootType, "nonnillableEmptyDecimal", decimalType, false, false, true);

        addProperty(rootType, "nonnillableNilDecimal", decimalType, false, false, true);


        // now define the Customer type so that customers can be made
        Type rootSDOType = typeHelper.define(rootType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", rootSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveSimpleElementTestCases" };
        TestRunner.main(arguments);
    }
}
