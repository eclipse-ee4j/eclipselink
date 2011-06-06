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

import java.util.ArrayList;
import java.util.List;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

public class LoadAndSaveGroupTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveGroupTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveGroupTestCases" };
        TestRunner.main(arguments);
    }

    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
    }

    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/group/Group.xml";
    }

    protected String getNoSchemaControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/group/GroupNoSchema.xml";
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/group/Group.xsd";
    }

    protected String getControlRootURI() {
        return "theURI";
    }

    // Override package generation based on the JAXB 2.0 algorithm in SDOUtil.java
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();       
        packages.add("theuri");
        return packages;
    }
    
    protected String getControlRootName() {
        return "AAA";
    }

    protected String getRootInterfaceName() {
        return "AAA";
    }

    public void registerTypes() {
        SDOType stringType = (SDOType) typeHelper.getType("commonj.sdo", "String");
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
        SDOType propertyType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.PROPERTY);

        DataObject AAATypeDO = dataFactory.create(typeType);
        AAATypeDO.set("uri", getControlRootURI());
        AAATypeDO.set("name", getControlRootName());
        addProperty(AAATypeDO, "BBB", SDOConstants.SDO_STRING, false, false, true);
        addProperty(AAATypeDO, "CCC", SDOConstants.SDO_STRING, false, false, true);
        addProperty(AAATypeDO, "testElement", SDOConstants.SDO_STRING, false, false, true);
        addProperty(AAATypeDO, "XXX", SDOConstants.SDO_STRING, false, false, true);
        addProperty(AAATypeDO, "YYY", SDOConstants.SDO_STRING, false, false, true);
        Type AAAType = typeHelper.define(AAATypeDO);

        DataObject propDO = dataFactory.create(propertyType);
        propDO.set("name", getControlRootName());
        propDO.set("type", AAAType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }
}
