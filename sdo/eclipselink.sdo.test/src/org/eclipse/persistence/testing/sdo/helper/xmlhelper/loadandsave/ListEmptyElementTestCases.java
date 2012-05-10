/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class ListEmptyElementTestCases extends LoadAndSaveTestCases {

    public ListEmptyElementTestCases(String name) {
        super(name);
    }

    @Override
    protected String getRootInterfaceName() {
        return "Root";
    }

    @Override
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
        return packages;
    }

    @Override
    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/EmptyList.xsd";
    }

    @Override
    protected String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/EmptyList.xml";
    }

    @Override
    protected String getControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/EmptyList_Write.xml";
    }

    @Override
    protected String getNoSchemaControlWriteFileName() {
        return getControlWriteFileName();
    }

    @Override
    protected void registerTypes() {
        defineTypes();
    }

    @Override
    protected String getControlRootName() {
        return "root";
    }

    @Override
    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        List items = document.getRootObject().getList("item");
        assertEquals(null, items.get(0));
        assertEquals("A", items.get(1));
        assertEquals(null, items.get(2));
    }

}