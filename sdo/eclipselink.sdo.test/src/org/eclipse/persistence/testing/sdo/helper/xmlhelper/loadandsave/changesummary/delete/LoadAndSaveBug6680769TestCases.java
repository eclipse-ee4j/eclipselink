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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.delete;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveTestCases;

public class LoadAndSaveBug6680769TestCases extends LoadAndSaveTestCases {
    public LoadAndSaveBug6680769TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.delete.LoadAndSaveBug6680769TestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug6680769/bug6680769.xml");
    }

    protected String getControlWriteFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug6680769/bug6680769write.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug6680769/bug6680769noschema.xml");
    }

    protected String getSchemaName() {
        return "root.xsd";
    }

    protected String getControlRootURI() {
        return "http://www.example.org/root";
    }

    protected String getControlRootName() {
        return "root";
    }

    protected String getRootInterfaceName() {
        return "Root";
    }
    
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add("org/example/root");
        packages.add("org/example/data");
        packages.add("org/example/emp");
        return packages;
    }
    
    protected void registerTypes() {
        SDOType changeSummaryType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.CHANGESUMMARY);
        SDOType dataObjectType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.DATAOBJECT);
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        //Data Type
        DataObject dataTypeDO = dataFactory.create(typeType);
        dataTypeDO.set("name", "Data");
        dataTypeDO.set("uri", "http://www.example.org/data");
        addProperty(dataTypeDO, "Value", dataObjectType, true, true, true);
        addProperty(dataTypeDO, "ChangeSummary", changeSummaryType, true, false, true);
        Type dataType = typeHelper.define(dataTypeDO);

        //Emp Type
        DataObject empTypeDO = dataFactory.create(typeType);
        empTypeDO.set("name", "Emp");
        empTypeDO.set("uri", "http://www.example.org/emp");
        addProperty(empTypeDO, "Empno", SDOConstants.SDO_STRING, false, false, true);
        addProperty(empTypeDO, "Ename", SDOConstants.SDO_STRING, false, false, true);
        Type empType = typeHelper.define(empTypeDO);

        //Root Type
        DataObject rootTypeDO = dataFactory.create(typeType);
        rootTypeDO.set("name", "Root");
        rootTypeDO.set("uri", "http://www.example.org/root");
        addProperty(rootTypeDO, "data", dataType, true, false, true);
        Type rootType = typeHelper.define(rootTypeDO);

        //root open content property
        DataObject rootPropDO = dataFactory.create(propertyType);
        rootPropDO.set("name", "root");
        rootPropDO.set("type", rootType);

        typeHelper.defineOpenContentProperty(getControlRootURI(), rootPropDO);

    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/bug6680769/";
    }

    protected List defineTypes() {
        try {
            URL url = new URL(getSchemaLocation() + getSchemaName());
            InputStream is = url.openStream();

            return xsdHelper.define(is, getSchemaLocation());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    protected void generateClasses(String tmpDirName) throws Exception{
            
        URL url = new URL(getSchemaLocation() + getSchemaName());
        InputStream is = url.openStream();
        
        SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);

        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
        schemaResolver.setBaseSchemaLocation(getSchemaLocation());
        StreamSource ss = new StreamSource(is);
        classGenerator.generate(ss, tmpDirName, schemaResolver);
    }


    protected void verifyAfterLoad(XMLDocument doc) {
        super.verifyAfterLoad(doc);
        DataObject rootDO = doc.getRootObject();

        DataObject dataDO = rootDO.getDataObject("data");
        ChangeSummary cs = dataDO.getChangeSummary();
        cs.beginLogging();

        List valueList = (List)dataDO.get("Value");

        //clear the list, this is to recreate the issue that this bug is about
        valueList.clear();
    }
}
