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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.defineandgenerate;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.oxm.XMLConstants;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class DefineAndGenerateBug5893546TestCases extends XSDHelperDefineAndGenerateTestCases {
    public DefineAndGenerateBug5893546TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(DefineAndGenerateBug5893546TestCases.class);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/Bug5893546Emp.xsd";
    }

    public List getControlTypes() {
        List types = new ArrayList();
        String uri = "http://example.com/emp/";
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        SDOType empSDOType = new SDOType(uri, "Emp");
        empSDOType.setInstanceClassName("com.example.hr.Emp");
        SDOProperty prop = new SDOProperty(aHelperContext);
        prop.setName("ename");
        prop.setType(stringType);
        prop.setXsd(true);
        prop.setXsdLocalName("ename");
        empSDOType.addDeclaredProperty(prop);

        types.add(empSDOType);
        return types;
    }

    public void testBug5893546() throws Exception {
   
        DataObject empTypeDO = dataFactory.create(SDOConstants.SDO_URL, "Type");        
        empTypeDO.set("uri", "http://example.com/emp/");
        empTypeDO.set("name", "Emp");
        
        addProperty(empTypeDO, "ename", SDOConstants.SDO_STRING, false, false, true);
        Type empType = typeHelper.define(empTypeDO);
        
        List baseTypes = new ArrayList();
        baseTypes.add(empType);
        
        DataObject typeDO = dataFactory.create(SDOConstants.SDO_URL, "Type");
        typeDO.set("uri", "http://example.com/commemp/");
        typeDO.set("name", "CommEmp");
        typeDO.set("baseType", baseTypes);

        DataObject propDO = typeDO.createDataObject("property");
        propDO.set("name", "agent");
        propDO.set("many", false);
        propDO.set("type", empType);
        propDO.set("containment", true);

        Type type = typeHelper.define(typeDO);
        ((SDOType)type).setInstanceClassName("com.example.commemp.CommEmp");

        List types = new ArrayList();
        types.add(type);
        String generatedSchema = xsdHelper.generate(types, null);              
        
        String controlSchema = getSchema(getControlGeneratedFileName());
        log("EXPECTED: \n" + controlSchema);
        log("ACTUAL: \n" + generatedSchema);

        StringReader reader = new StringReader(generatedSchema);
        InputSource inputSource = new InputSource(reader);
        Document generatedSchemaDoc = parser.parse(inputSource);        
        reader.close();
        
        assertXMLIdentical(getDocument("org/eclipse/persistence/testing/sdo/helper/xsdhelper/Bug5893546.xsd"), generatedSchemaDoc);
        
    }
}
