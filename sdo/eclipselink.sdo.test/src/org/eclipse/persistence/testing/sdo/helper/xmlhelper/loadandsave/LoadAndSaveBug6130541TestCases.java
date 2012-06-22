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
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

public class LoadAndSaveBug6130541TestCases extends LoadAndSaveTestCases {
    public LoadAndSaveBug6130541TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveBug6130541TestCases" };
        TestRunner.main(arguments);
    }

    /*
        protected String getNoSchemaControlFileName() {
            return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/Bug6130541.xml";
        }

        protected String getControlDataObjectFileName() {
            return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderWithIDREF.xml";
        }
    */
    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/bug6130541/Bug6130541.xml";
    }

    protected String getSchemaName() {
        return "org/eclipse/persistence/testing/sdo/helper/xmlhelper/bug6130541/Bug6130541.xsd";
    }

    protected String getControlRootURI() {
        return "http://theUri";
    }

    protected String getControlRootName() {
        return "company";
    }

    protected String getRootInterfaceName() {
        return "CompanyType";
    }

    /*  protected void generateClasses(String tmpDirName) throws Exception{
         URL url = new URL(getSchemaLocation() + getSchemaName());
         InputStream is = url.openStream();

         SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);

         DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
         schemaResolver.setBaseSchemaLocation(getSchemaLocation());
         StreamSource ss = new StreamSource(is);
         classGenerator.generate(ss, tmpDirName, schemaResolver);
     }*/
    public void registerTypes() {
        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        // create a new Type for Employee
        DataObject empType = dataFactory.create("commonj.sdo", "Type");
        empType.set("uri", "http://theUri");
        empType.set("name", "EmployeeType");

        // create a first name property
        addProperty(empType, "name", stringType, false, false, true);
        addProperty(empType, "jobTitle", stringType, false, false, true);
        addProperty(empType, "manager", empType, true, false, true);
        Type empSDOType = typeHelper.define(empType);

        // create a new Type for Company
        DataObject compnayType = dataFactory.create("commonj.sdo", "Type");
        compnayType.set("uri", "http://theUri");
        compnayType.set("name", "CompanyType");

        addProperty(compnayType, "companyName", stringType, false, false, true);
        addProperty(compnayType, "employee", empSDOType, true, true, true);

        Type companySDOType = typeHelper.define(compnayType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", "company");
        propDO.set("type", companySDOType);
       // typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);

    }
}
