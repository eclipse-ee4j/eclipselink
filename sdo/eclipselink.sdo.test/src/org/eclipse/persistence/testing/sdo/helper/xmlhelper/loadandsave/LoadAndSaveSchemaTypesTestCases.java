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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import java.util.ArrayList;
import java.util.List;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public class LoadAndSaveSchemaTypesTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveSchemaTypesTestCases(String name) {
        super(name);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/schemaTypes.xml");
    }

    protected String getNoSchemaControlFileName() {        
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/schemaTypesNoSchema.xml");
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/schemas/SchemaTypes.xsd";
    }

    protected String getControlRootURI() {
        return NON_DEFAULT_URI;
    }

    protected String getControlRootName() {
        return "Test";
    }

     protected String getRootInterfaceName() {
        return "MyTestType";
    }

     // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
     protected List<String> getPackages() {
         List<String> packages = new ArrayList<String>();       
         packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
         return packages;
     }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveSchemaTypesTestCases" };
        TestRunner.main(arguments);
    }

    protected void registerTypes() {
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        // create a new Type for Customers
        DataObject schemaTypesType = dataFactory.create("commonj.sdo", "Type");

        SDOProperty prop = (SDOProperty)schemaTypesType.getType().getProperty("uri");
        schemaTypesType.set(prop, getControlRootURI());

        prop = (SDOProperty)schemaTypesType.getType().getProperty("name");
        schemaTypesType.set(prop, "Test");

        addProperty(schemaTypesType, "myAnySimpleTypeTest", SDOConstants.SDO_OBJECT, false, false, true);
        addProperty(schemaTypesType, "myAnyTypeTest", dataObjectType, true, false, true);
        addProperty(schemaTypesType, "myAnyTypeTest2", dataObjectType, true, false, true);
        addProperty(schemaTypesType, "myAnyTypeTest3", dataObjectType, true, true, true);
        addProperty(schemaTypesType, "myAnyTypeTest4", dataObjectType, true, true, true);
        addProperty(schemaTypesType, "myAnyURITest", SDOConstants.SDO_URI, false, false, true);
        addProperty(schemaTypesType, "myBase64BinaryTest", SDOConstants.SDO_BYTES, false, false, true);
        addProperty(schemaTypesType, "myBooleanTest", SDOConstants.SDO_BOOLEAN, false, false, true);
        addProperty(schemaTypesType, "myByteTest", SDOConstants.SDO_BYTE, false, false, true);
        addProperty(schemaTypesType, "myDateTest", SDOConstants.SDO_YEARMONTHDAY, false, false, true);
        addProperty(schemaTypesType, "myDateTimeTest", SDOConstants.SDO_DATETIME, false, false, true);
        addProperty(schemaTypesType, "myDecimalTest", SDOConstants.SDO_DECIMAL, false, false, true);
        addProperty(schemaTypesType, "myDoubleTest", SDOConstants.SDO_DOUBLE, false, false, true);
        addProperty(schemaTypesType, "myDurationTest", SDOConstants.SDO_DURATION, false, false, true);
        addProperty(schemaTypesType, "myENTITIESTest", SDOConstants.SDO_STRINGS, false, false, true);
        addProperty(schemaTypesType, "myENTITYTest", SDOConstants.SDO_STRING, false, false, true);
        addProperty(schemaTypesType, "myFloatTest", SDOConstants.SDO_FLOAT, false, false, true);
        addProperty(schemaTypesType, "myGDayTest", SDOConstants.SDO_DAY, false, false, true);
        addProperty(schemaTypesType, "myGMonthTest", SDOConstants.SDO_MONTH, false, false, true);
        addProperty(schemaTypesType, "myGMonthDayTest", SDOConstants.SDO_MONTHDAY, false, false, true);
        addProperty(schemaTypesType, "myGYearTest", SDOConstants.SDO_YEAR, false, false, true);
        addProperty(schemaTypesType, "myGYearMonthTest", SDOConstants.SDO_YEARMONTH, false, false, true);
        addProperty(schemaTypesType, "myIDTest", SDOConstants.SDO_STRING, false, false, true);
        addProperty(schemaTypesType, "myIDREFTest", SDOConstants.SDO_STRING, false, false, true);
        addProperty(schemaTypesType, "myIDREFSTest", SDOConstants.SDO_STRINGS, false, false, true);
        addProperty(schemaTypesType, "myIntTest", SDOConstants.SDO_INT, false, false, true);
        addProperty(schemaTypesType, "myIntegerTest", SDOConstants.SDO_INTEGER, false, false, true);
        addProperty(schemaTypesType, "myLanguageTest", SDOConstants.SDO_STRING, false, false, true);
        addProperty(schemaTypesType, "myLongTest", SDOConstants.SDO_LONG, false, false, true);
        addProperty(schemaTypesType, "myNameTest", SDOConstants.SDO_STRING, false, false, true);
        addProperty(schemaTypesType, "myNCNameTest", SDOConstants.SDO_STRING, false, false, true);
        addProperty(schemaTypesType, "myNegativeIntegerTest", SDOConstants.SDO_INTEGER, false, false, true);
        addProperty(schemaTypesType, "myNonNegativeIntegerTest", SDOConstants.SDO_INTEGER, false, false, true);
        addProperty(schemaTypesType, "myNMTOKENTest", SDOConstants.SDO_STRING, false, false, true);
        addProperty(schemaTypesType, "myNMTOKENSTest", SDOConstants.SDO_STRINGS, false, false, true);
        addProperty(schemaTypesType, "myNOTATIONTest", SDOConstants.SDO_STRING, false, false, true);
        addProperty(schemaTypesType, "myNormalizedStringTest", SDOConstants.SDO_STRING, false, false, true);
        addProperty(schemaTypesType, "myPositiveIntegerTest", SDOConstants.SDO_INTEGER, false, false, true);
        addProperty(schemaTypesType, "myNonPositiveIntegerTest", SDOConstants.SDO_INTEGER, false, false, true);
        addProperty(schemaTypesType, "myQNameTest", SDOConstants.SDO_URI, false, false, true);
        addProperty(schemaTypesType, "myShort", SDOConstants.SDO_SHORT, false, false, true);
        addProperty(schemaTypesType, "myStringTest", SDOConstants.SDO_STRING, false, false, true);
        addProperty(schemaTypesType, "myTimeTest", SDOConstants.SDO_TIME, false, false, true);
        addProperty(schemaTypesType, "myTokenTest", SDOConstants.SDO_STRING, false, false, true);
        addProperty(schemaTypesType, "myUnsignedByteTest", SDOConstants.SDO_SHORT, false, false, true);
        addProperty(schemaTypesType, "myUnsignedIntTest", SDOConstants.SDO_LONG, false, false, true);
        addProperty(schemaTypesType, "myUnsignedLongTest", SDOConstants.SDO_INTEGER, false, false, true);
        addProperty(schemaTypesType, "myUnsignedShortTest", SDOConstants.SDO_INT, false, false, true);

        addProperty(schemaTypesType, "myLongWrapperTest", SDOConstants.SDO_LONGOBJECT, false, false, true);
        addProperty(schemaTypesType, "myBooleanWrapperTest", SDOConstants.SDO_BOOLEANOBJECT, false, false, true);

        Type schemaTypes = typeHelper.define(schemaTypesType);
        //TODO: workaround since hex is non-spec

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", schemaTypes);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);

        DataObject personTypeDO = dataFactory.create("commonj.sdo", "Type");
        personTypeDO.set("uri", getControlRootURI());
        personTypeDO.set("name", "PersonType");
        addProperty(personTypeDO, "firstName", SDOConstants.SDO_STRING, false, false, true);
        addProperty(personTypeDO, "lastName", SDOConstants.SDO_STRING, false, false, true);
        Type personType = typeHelper.define(personTypeDO);
    }
}
