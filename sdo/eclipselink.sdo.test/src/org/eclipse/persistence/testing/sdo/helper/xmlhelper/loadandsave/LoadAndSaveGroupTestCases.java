/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;

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

    protected String getControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/group/GroupWrite.xml";
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

    protected String getControlRootName() {
        return "AAA";
    }

    protected String getRootInterfaceName() {
        return "AAA";
    }

    public void registerTypes() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");

        DataObject AAATypeDO = dataFactory.create(SDOConstants.SDO_TYPE);
        AAATypeDO.set("uri", getControlRootURI());
        AAATypeDO.set("name", getControlRootName());
        addProperty(AAATypeDO, "BBB", SDOConstants.SDO_STRING, false, false, true);
        addProperty(AAATypeDO, "CCC", SDOConstants.SDO_STRING, false, false, true);
        addProperty(AAATypeDO, "testElement", SDOConstants.SDO_STRING, false, false, true);
        addProperty(AAATypeDO, "XXX", SDOConstants.SDO_STRING, false, false, true);
        addProperty(AAATypeDO, "YYY", SDOConstants.SDO_STRING, false, false, true);
        Type AAAType = typeHelper.define(AAATypeDO);

        DataObject propDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        propDO.set("name", getControlRootName());
        propDO.set("type", AAAType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);
    }
}