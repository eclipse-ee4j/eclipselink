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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.mixed;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveTestCases;
import org.w3c.dom.Document;

public class LoadAndSaveMixedContentTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveMixedContentTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.mixed.LoadAndSaveMixedContentTestCases" };
        TestRunner.main(arguments);
    }

    protected String getSchemaName() {
        return "Employee.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/mixed/Employee.xml");
    }
    
    protected String getPath() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/mixed/");
    }

    protected String getNoSchemaControlFileName() {
        return getControlFileName();
    }

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/mixed/";
    }

    protected String getControlRootURI() {
        return "http://www.example.org";
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
    
    public void testLoadAndSaveMixedComplexType() throws Exception {
        try {
            FileInputStream is = new FileInputStream(getPath() + getSchemaName());           
            List types = xsdHelper.define(getSchema(is, getSchemaLocation() + getSchemaName()));
            FileReader reader = new FileReader(getControlFileName());
            XMLDocument document = xmlHelper.load(reader, null, null);
            String s = xmlHelper.save(document.getRootObject(), getControlRootURI(), getControlRootName());
            compareXML(getControlFileName(), s);
        } catch (IllegalArgumentException iae) {
            fail(iae.getMessage());
        }
    }

    public void testNoSchemaLoadAndSaveMixedComplexType() throws Exception {
        // registerTypes();
        String controlFileName = "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/mixed/EmployeeNoXsiNamespace.xml";
        FileInputStream inputStream = new FileInputStream(controlFileName);
        XMLDocument document = xmlHelper.load(inputStream, null, getOptions());
        verifyAfterLoad(document);
        StringWriter writer = new StringWriter();
        xmlHelper.save(document, writer, null);
        compareXML(controlFileName, writer.toString());
    }

    public void testLoadAndSaveMixedComplexTypeFromDO() throws Exception {
        registerTypes();
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        XMLDocument document = xmlHelper.load(inputStream, null, getOptions());
        verifyAfterLoad(document);
        StringWriter writer = new StringWriter();
        xmlHelper.save(document, writer, null);
        compareXML(getControlWriteFileName(), writer.toString());
    }

    protected void generateClasses(String tmpDirName) throws Exception {
        URL url = new URL(getSchemaLocation() + getSchemaName());
        InputStream is = url.openStream();
        SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);
        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
        schemaResolver.setBaseSchemaLocation(getSchemaLocation());
        StreamSource ss = new StreamSource(is);
        classGenerator.generate(ss, tmpDirName, schemaResolver);
    }

    protected List defineTypes() {
        try {
            URL url = new URL(getSchemaLocation() + getSchemaName());
            InputStream is = url.openStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(is);
            DOMSource ds = new DOMSource(doc);
            DefaultSchemaResolver sr = new DefaultSchemaResolver();
            sr.setBaseSchemaLocation(getSchemaLocation());
            return ((SDOXSDHelper)xsdHelper).define(ds, sr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void registerTypes() {
        Type intType = typeHelper.getType("commonj.sdo", "Int");
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type decimalType = typeHelper.getType("commonj.sdo", "Decimal");
        Type dateType = typeHelper.getType("commonj.sdo", "Date");
        Type booleanType = typeHelper.getType("commonj.sdo", "Boolean");
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject empTypeDO = defineType("http://www.example.org", "EmployeeType");
        addProperty(empTypeDO, "id", stringType, false, false, true);
        addProperty(empTypeDO, "name", stringType, false, false, true);
        empTypeDO.set("sequenced", true);
        
        Type empType = typeHelper.define(empTypeDO);
        
        DataObject empPropDO = dataFactory.create(propertyType);
        empPropDO.set("name", getControlRootName());
        empPropDO.set("type", empType);
        
        typeHelper.defineOpenContentProperty("http://www.example.org", empPropDO);
    }
}
