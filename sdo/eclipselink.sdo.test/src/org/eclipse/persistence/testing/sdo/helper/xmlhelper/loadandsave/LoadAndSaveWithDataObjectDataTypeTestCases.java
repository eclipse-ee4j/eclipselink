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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;

public class LoadAndSaveWithDataObjectDataTypeTestCases extends LoadAndSaveTestCases {
  
    public LoadAndSaveWithDataObjectDataTypeTestCases(String name) {
        super(name);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/dataObjectDataType.xml");
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/DataObjectDataType.xsd";
    }

    protected String getControlRootURI() {
        return NON_DEFAULT_URI;
    }

    protected String getControlRootName() {
        return "rootType";
    }
    
     protected String getRootInterfaceName() {
        return "RootType";
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveWithDataObjectDataTypeTestCases" };
        TestRunner.main(arguments);
    }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();       
        packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
        return packages;
    }

    protected void registerTypes() {
        ((SDOTypeHelper)typeHelper).reset();

        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);
        SDOProperty xmlDataTypeProperty = (SDOProperty) typeHelper.getOpenContentProperty(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_DATATYPE);
        SDOProperty xmlSchemaTypeProperty = (SDOProperty) typeHelper.getOpenContentProperty(SDOConstants.ORACLE_SDO_URL, SDOConstants. XML_SCHEMA_TYPE_NAME);

        DataObject myChildTypeDO = dataFactory.create("commonj.sdo", "Type");
        myChildTypeDO.set("uri", "http://www.example.org");
        myChildTypeDO.set("name", "childType");
        addProperty(myChildTypeDO, "name", SDOConstants.SDO_STRING, false, false, true);
        Type childType = typeHelper.define(myChildTypeDO);
        ((SDOProperty)childType.getProperty("name")).setXsd(true);
        ((SDOProperty)childType.getProperty("name")).setXsdLocalName("name");

        DataObject myRootTypeDO = dataFactory.create("commonj.sdo", "Type");
        myRootTypeDO.set("uri", "http://www.example.org");
        myRootTypeDO.set("name", "rootType");
        DataObject test1Prop = addProperty(myRootTypeDO, "test1", SDOConstants.SDO_INTOBJECT, false, false, true);

        DataObject test2Prop = addProperty(myRootTypeDO, "test2", SDOConstants.SDO_BOOLEANOBJECT, false, false, true);
        test2Prop.set(xmlDataTypeProperty, SDOConstants.SDO_BOOLEANOBJECT);

        DataObject test3Prop = addProperty(myRootTypeDO, "test3", SDOConstants.SDO_DATE, false, false, true);

        DataObject test4Prop = addProperty(myRootTypeDO, "test4", SDOConstants.SDO_DATETIME, false, false, true);
        
        DataObject test5Prop = addProperty(myRootTypeDO, "test5", SDOConstants.SDO_SHORT, false, false, true);
        test5Prop.set(xmlDataTypeProperty, SDOConstants.SDO_SHORTOBJECT);
                
        DataObject test6Prop = addProperty(myRootTypeDO, "test6", dataObjectType, true, false, true);
        test6Prop.set(xmlDataTypeProperty, dataObjectType);
                
        DataObject test7Prop = addProperty(myRootTypeDO, "test7", childType, true, false, true);        
        test7Prop.set(xmlSchemaTypeProperty, childType);
        test7Prop.set(xmlDataTypeProperty, dataObjectType);

        Type myRootType = typeHelper.define(myRootTypeDO);
        ((SDOType)myRootType).setXsd(true);
        ((SDOType)myRootType).setXsdLocalName("rootType");

        ((SDOProperty)myRootType.getProperty("test1")).setXsd(true);
        ((SDOProperty)myRootType.getProperty("test1")).setXsdLocalName("test1");

        ((SDOProperty)myRootType.getProperty("test2")).setXsd(true);
        ((SDOProperty)myRootType.getProperty("test2")).setXsdLocalName("test2");

        ((SDOProperty)myRootType.getProperty("test3")).setXsd(true);
        ((SDOProperty)myRootType.getProperty("test3")).setXsdLocalName("test3");

        ((SDOProperty)myRootType.getProperty("test4")).setXsd(true);
        ((SDOProperty)myRootType.getProperty("test4")).setXsdLocalName("test4");
        
        ((SDOProperty)myRootType.getProperty("test5")).setXsd(true);
        ((SDOProperty)myRootType.getProperty("test5")).setXsdLocalName("test5");

        ((SDOProperty)myRootType.getProperty("test6")).setXsd(true);
        ((SDOProperty)myRootType.getProperty("test6")).setXsdLocalName("test6");
        
        ((SDOProperty)myRootType.getProperty("test7")).setXsd(true);
        ((SDOProperty)myRootType.getProperty("test7")).setXsdLocalName("test7");
                
        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", myRootType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);

    }
}
