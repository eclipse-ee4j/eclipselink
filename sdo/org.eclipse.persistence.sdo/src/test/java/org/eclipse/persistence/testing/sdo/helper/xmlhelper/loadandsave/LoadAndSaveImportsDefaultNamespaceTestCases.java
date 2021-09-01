/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;


public class LoadAndSaveImportsDefaultNamespaceTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveImportsDefaultNamespaceTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveImportsDefaultNamespaceTestCases" };
        TestRunner.main(arguments);
    }

    @Override
    protected String getSchemaName() {
        return "Dept.xsd";
    }

    @Override
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/dept.xml");
    }

    @Override
    protected String getControlWriteFileName(){
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/deptWrite.xml");
    }

    @Override
    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/deptNoSchema.xml");
    }

    @Override
    protected String getControlRootURI() {
        return "http://sdo.sample.service.types/Dept/";
    }

    @Override
    protected String getControlRootName() {
        return "dept";
    }

    @Override
    protected String getRootInterfaceName() {
        return "Dept";
    }

    @Override
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

    @Override
    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xmlhelper/";
    }

    @Override
    protected List getPackages() {
      List packages = new ArrayList();
      packages.add("sdo/sample/service/types/dept");
      packages.add("sdo/sample/service/types/emp");

      return packages;
    }

    @Override
    protected void generateClasses(String tmpDirName) throws Exception{

        URL url = new URL(getSchemaLocation() + getSchemaName());
        InputStream is = url.openStream();

        SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);

        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
        schemaResolver.setBaseSchemaLocation(getSchemaLocation());
        StreamSource ss = new StreamSource(is);
        classGenerator.generate(ss, tmpDirName, schemaResolver);
    }

    @Override
    public void registerTypes() {
        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject empType = defineType("http://sdo.sample.service.types/Emp/", "Emp");
        DataObject empnoProp = addProperty(empType, "Empno", stringType);
        empnoProp.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        DataObject ename = addProperty(empType, "Ename", stringType);
        ename.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        Type empSDOType = typeHelper.define(empType);

        DataObject deptType = defineType(getControlRootURI(), getControlRootName());
        DataObject deptNo = addProperty(deptType, "Deptno", stringType);
        deptNo.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        DataObject dname = addProperty(deptType, "Dname", stringType);
        dname.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        DataObject empsProp = addProperty(deptType, "Emp", empSDOType);
        empsProp.set("many", true);
        empsProp.set("containment", true);

        Type deptSDOType = typeHelper.define(deptType);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", deptSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }
}
