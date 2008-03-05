/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.typehelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.sql.Timestamp;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class DataTypeBug5959761TestCases extends SDOTestCase {
    public DataTypeBug5959761TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.define.DataTypeBug5959761TestCases" };
        TestRunner.main(arguments);
    }

    public void testDataTypeAnnotation() throws Exception {
        DataObject timestampSTDo = dataFactory.create("commonj.sdo", "Type");
        timestampSTDo.set("uri", "http://sdo.sample.service/types/");
        timestampSTDo.set("name", "TimeStampeST");
        timestampSTDo.set("dataType", true);
        timestampSTDo.set(SDOConstants.JAVA_CLASS_PROPERTY, "java.sql.Timestamp");
        Type timeStampType = typeHelper.define(timestampSTDo);

        DataObject rootTypeDO = dataFactory.create("commonj.sdo", "Type");
        rootTypeDO.set("name", "Root");
        rootTypeDO.set("uri", "http://sdo.sample.service/types/");
        rootTypeDO.set("dataType", false);

        DataObject rootPropDO = rootTypeDO.createDataObject("property");
        rootPropDO.set("name", "hireDate");
        rootPropDO.set("type", SDOConstants.SDO_DATETIME);
        rootPropDO.set(SDOConstants.XMLDATATYPE_PROPERTY, timeStampType);
        rootPropDO.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        Type rootType = typeHelper.define(rootTypeDO);
        
        DataObject propDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        propDO.set("name", "Root");
        propDO.set("type", rootType);
        typeHelper.defineOpenContentProperty("http://sdo.sample.service/types/", propDO);
        
        FileInputStream xmlFile = new FileInputStream(getXmlFileNameToLoad());
        XMLDocument document = xmlHelper.load(xmlFile);
        DataObject root = document.getRootObject();
        
        Object value = root.get("hireDate");
        assertNotNull(value);
        assertTrue(value instanceof Timestamp);
    }
    
    private String getXmlFileNameToLoad(){
      return ("./org/eclipse/persistence/testing/sdo/helper/typehelper/timestamp.xml");
    }
}
