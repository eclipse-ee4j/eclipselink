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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;

public class LoadAndSaveInheritanceBug6043501TestCases extends LoadAndSaveTestCases {
    public LoadAndSaveInheritanceBug6043501TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveInheritanceBug6043501TestCases" };
        TestRunner.main(arguments);
    }

    protected String getSchemaName() {
        return "CompanyBug6043501.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/companyBug6043501.xml");
    }
      
    protected String getControlWriteFileName(){
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/companyBug6043501Write.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/companyBug6043501NoSchema.xml");
    }

    protected String getControlRootURI() {
        return "http://testUri";
    }

    protected String getControlRootName() {
        return "company";
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
    
    protected String getRootInterfaceName() {
        return "CompanyType";
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

    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/schemas/";
    }

    public void registerTypes() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        // create a new Type for Person
        DataObject personType = dataFactory.create("commonj.sdo", "Type");
        personType.set("uri", getControlRootURI());
        personType.set("name", "PersonType");
        // create a first name property
        addProperty(personType, "name", stringType, false, false, true);
        Type personSDOType = typeHelper.define(personType);

        // create a new Type for Employee
        DataObject empType = dataFactory.create("commonj.sdo", "Type");
        empType.set("uri", "http://someOtherUri");
        empType.set("name", "EmployeeType");

        List baseTypesList = new ArrayList();
        baseTypesList.add(personSDOType);
        empType.set("baseType", baseTypesList);
        // create a first name property
        addProperty(empType, "jobTitle", stringType, false, false, true);
        Type empSDOType = typeHelper.define(empType);

        // create a new Type for Company
        DataObject compnayType = dataFactory.create("commonj.sdo", "Type");
        compnayType.set("uri", "http://someOtherUri");
        compnayType.set("name", "CompanyType");

        addProperty(compnayType, "companyName", stringType, false, false, true);
        addProperty(compnayType, "ceo", empSDOType, true, false, true);

        Type companySDOType = typeHelper.define(compnayType);

    }
}
