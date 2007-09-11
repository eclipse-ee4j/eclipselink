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
import org.eclipse.persistence.sdo.SDOProperty;
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
        return "http://www.example.org";
    }

    protected String getControlRootName() {
        return "Test";
    }
    
     protected String getRootInterfaceName() {
        return "Test";
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveSchemaTypesTestCases" };
        TestRunner.main(arguments);
    }

    protected void registerTypes() {
        // create a new Type for Customers
        DataObject schemaTypesType = dataFactory.create("commonj.sdo", "Type");

        SDOProperty prop = (SDOProperty)schemaTypesType.getType().getProperty("uri");
        schemaTypesType.set(prop, getControlRootURI());

        prop = (SDOProperty)schemaTypesType.getType().getProperty("name");
        schemaTypesType.set(prop, "Test");

        addProperty(schemaTypesType, "myAnySimpleTypeTest", SDOConstants.SDO_OBJECT, true, false, true);
        addProperty(schemaTypesType, "myAnyTypeTest", SDOConstants.SDO_DATAOBJECT, true, false, true);
        addProperty(schemaTypesType, "myAnyURITest", SDOConstants.SDO_URI, true, false, true);
        addProperty(schemaTypesType, "myBase64BinaryTest", SDOConstants.SDO_BYTES, true, false, true);
        addProperty(schemaTypesType, "myBooleanTest", SDOConstants.SDO_BOOLEAN, true, false, true);
        addProperty(schemaTypesType, "myByteTest", SDOConstants.SDO_BYTE, true, false, true);
        addProperty(schemaTypesType, "myDateTest", SDOConstants.SDO_YEARMONTHDAY, true, false, true);
        addProperty(schemaTypesType, "myDateTimeTest", SDOConstants.SDO_DATETIME, true, false, true);
        addProperty(schemaTypesType, "myDecimalTest", SDOConstants.SDO_DECIMAL, true, false, true);
        addProperty(schemaTypesType, "myDoubleTest", SDOConstants.SDO_DOUBLE, true, false, true);
        addProperty(schemaTypesType, "myDurationTest", SDOConstants.SDO_DURATION, true, false, true);
        addProperty(schemaTypesType, "myENTITIESTest", SDOConstants.SDO_STRINGS, true, false, true);
        addProperty(schemaTypesType, "myENTITYTest", SDOConstants.SDO_STRING, true, false, true);
        addProperty(schemaTypesType, "myFloatTest", SDOConstants.SDO_FLOAT, true, false, true);
        addProperty(schemaTypesType, "myGDayTest", SDOConstants.SDO_DAY, true, false, true);
        addProperty(schemaTypesType, "myGMonthTest", SDOConstants.SDO_MONTH, true, false, true);
        addProperty(schemaTypesType, "myGMonthDayTest", SDOConstants.SDO_MONTHDAY, true, false, true);
        addProperty(schemaTypesType, "myGYearTest", SDOConstants.SDO_YEAR, true, false, true);
        addProperty(schemaTypesType, "myGYearMonthTest", SDOConstants.SDO_YEARMONTH, true, false, true);
        addProperty(schemaTypesType, "myHexBinaryTest", SDOConstants.SDO_BYTES, true, false, true);        
        addProperty(schemaTypesType, "myIDTest", SDOConstants.SDO_STRING, true, false, true);
        addProperty(schemaTypesType, "myIDREFTest", SDOConstants.SDO_STRING, true, false, true);
        addProperty(schemaTypesType, "myIDREFSTest", SDOConstants.SDO_STRINGS, true, false, true);
        addProperty(schemaTypesType, "myIntTest", SDOConstants.SDO_INT, true, false, true);
        addProperty(schemaTypesType, "myIntegerTest", SDOConstants.SDO_INTEGER, true, false, true);
        addProperty(schemaTypesType, "myLanguageTest", SDOConstants.SDO_STRING, true, false, true);
        addProperty(schemaTypesType, "myLongTest", SDOConstants.SDO_LONG, true, false, true);
        addProperty(schemaTypesType, "myNameTest", SDOConstants.SDO_STRING, true, false, true);
        addProperty(schemaTypesType, "myNCNameTest", SDOConstants.SDO_STRING, true, false, true);
        addProperty(schemaTypesType, "myNegativeIntegerTest", SDOConstants.SDO_INTEGER, true, false, true);
        addProperty(schemaTypesType, "myNonNegativeIntegerTest", SDOConstants.SDO_INTEGER, true, false, true);
        addProperty(schemaTypesType, "myNMTOKENTest", SDOConstants.SDO_STRING, true, false, true);
        addProperty(schemaTypesType, "myNMTOKENSTest", SDOConstants.SDO_STRINGS, true, false, true);
        addProperty(schemaTypesType, "myNOTATIONTest", SDOConstants.SDO_STRING, true, false, true);
        addProperty(schemaTypesType, "myNormalizedStringTest", SDOConstants.SDO_STRING, true, false, true);
        addProperty(schemaTypesType, "myPositiveIntegerTest", SDOConstants.SDO_INTEGER, true, false, true);
        addProperty(schemaTypesType, "myNonPositiveIntegerTest", SDOConstants.SDO_INTEGER, true, false, true);
        addProperty(schemaTypesType, "myQNameTest", SDOConstants.SDO_URI, true, false, true);
        addProperty(schemaTypesType, "myShort", SDOConstants.SDO_SHORT, true, false, true);
        addProperty(schemaTypesType, "myStringTest", SDOConstants.SDO_STRING, true, false, true);
        addProperty(schemaTypesType, "myTimeTest", SDOConstants.SDO_TIME, true, false, true);
        addProperty(schemaTypesType, "myTokenTest", SDOConstants.SDO_STRING, true, false, true);
        addProperty(schemaTypesType, "myUnsignedByteTest", SDOConstants.SDO_SHORT, true, false, true);
        addProperty(schemaTypesType, "myUnsignedIntTest", SDOConstants.SDO_LONG, true, false, true);
        addProperty(schemaTypesType, "myUnsignedLongTest", SDOConstants.SDO_INTEGER, true, false, true);
        addProperty(schemaTypesType, "myUnsignedShortTest", SDOConstants.SDO_INT, true, false, true);

        Type schemaTypes = typeHelper.define(schemaTypesType);
        //TODO: workaround since hex is non-spec
        SDOProperty hexProp = (SDOProperty) schemaTypes.getProperty("myHexBinaryTest");        
        ((XMLField)((XMLDirectMapping)hexProp.getXmlMapping()).getField()).setSchemaType(XMLConstants.HEX_BINARY_QNAME);
                
        DataObject propDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        propDO.set("name", getControlRootName());
        propDO.set("type", schemaTypes);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }
}