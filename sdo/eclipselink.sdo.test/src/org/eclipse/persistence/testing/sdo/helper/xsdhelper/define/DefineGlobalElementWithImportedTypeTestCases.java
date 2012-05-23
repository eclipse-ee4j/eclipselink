/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.sdo.SDOType;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class DefineGlobalElementWithImportedTypeTestCases extends XSDHelperDefineTestCases {

    public DefineGlobalElementWithImportedTypeTestCases(String name) {
        super(name);
    }

    @Override
    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/GlobalElementWithImportedType.xsd";
    }

    @Override
    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/define/";
    }

    @Override
    public List getControlTypes() {
        DataObject importedTypeDO = dataFactory.create("commonj.sdo", "Type");
        importedTypeDO.set("uri", "http://www.example.org/imported");
        importedTypeDO.set("name", "ImportedType");
        DataObject nameProperty = importedTypeDO.createDataObject("property");
        nameProperty.set("name", "name");

        List<Type> types = new ArrayList<Type>(1);
        types.add(typeHelper.define(importedTypeDO));
        return types;
    }

    public void testFoo() {
        InputStream is = getSchemaInputStream(getSchemaToDefine());
        xsdHelper.define(is, getSchemaLocation());

        XMLDocument xmlDoc = xmlHelper.load("<GlobalElement xmlns='http://www.example.org'><name xmlns='http://www.example.org/imported'>ABC</name></GlobalElement>");
        assertEquals("ABC", xmlDoc.getRootObject().get("name"));
    }

}
