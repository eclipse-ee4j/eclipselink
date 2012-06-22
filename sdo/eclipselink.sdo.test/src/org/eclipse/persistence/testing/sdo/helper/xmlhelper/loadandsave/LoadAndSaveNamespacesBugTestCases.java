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

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;

public class LoadAndSaveNamespacesBugTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveNamespacesBugTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveNamespacesBugTestCases" };
        TestRunner.main(arguments);
    }

    protected List defineTypes() {
        List allTypes = new ArrayList();
        List types1 = xsdHelper.define(getSchema("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/namespaces/simpleSDOSchema.xsd"));
        List types2 = xsdHelper.define(getSchema("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/namespaces/MySDO.xsd"));

        allTypes.addAll(types1);
        allTypes.addAll(types2);

        return allTypes;
    }

    protected String getSchemaName() {
        return "";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/namespaces/namespacesBug.xml");
    }

    protected String getControlWriteFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/namespaces/namespacesBugWrite.xml");
    }

    protected String getNoSchemaControlWriteFileName() {
        return getControlWriteFileName();
    }
 
    protected String getControlRootURI() {
        return "http://www.example.org/";
    }

    protected String getControlRootName() {
        return "arg0";
    }

    protected void generateClasses(String tmpDirName) throws Exception {
        String xsdString = getSchema("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/namespaces/simpleSDOSchema.xsd");
        StringReader reader = new StringReader(xsdString);
        SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);
        classGenerator.generate(reader, tmpDirName);

        xsdString = getSchema("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/namespaces/MySDO.xsd");
        reader = new StringReader(xsdString);
        classGenerator.generate(reader, tmpDirName);
    }

    protected List getPackages() {
        List packages = new ArrayList();        
        packages.add("myPackage");
        return packages;
    }

    protected String getRootInterfaceName() {
        return "MySDO";
    }

    public void registerTypes() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        DataObject mySDOTypeDO = defineType(getControlRootURI(), "MySDO");
        mySDOTypeDO.set(SDOConstants.JAVA_CLASS_PROPERTY, "myPackage.MySDO");
        addProperty(mySDOTypeDO, "stringPart", stringType, false, false, true);
        addProperty(mySDOTypeDO, "intPart", SDOConstants.SDO_INT, false, false, true);
        Type mySDOType = typeHelper.define(mySDOTypeDO);

        DataObject bindingInfoTypeTypeDO = defineType(getControlRootURI(), "bindingInfoType");
        bindingInfoTypeTypeDO.set(SDOConstants.JAVA_CLASS_PROPERTY, "myPackage.BindingInfoType");
        addProperty(bindingInfoTypeTypeDO, "testString", stringType, false, false, true);
        Type bindingInfoTypeType = typeHelper.define(bindingInfoTypeTypeDO);
    }

    public void testBuildDataObjectAndSave() throws Exception {
        defineTypes();
        DataObject rootDO = dataFactory.create(getControlRootURI(), "MySDO");
        rootDO.set("intPart", 0);
        XMLDocument doc = xmlHelper.createDocument(rootDO, getControlRootURI(), getControlRootName());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xmlHelper.save(doc, outputStream, null);
        
        compareXML(getControlDataObjectFileName(), outputStream.toString());
    }
}
