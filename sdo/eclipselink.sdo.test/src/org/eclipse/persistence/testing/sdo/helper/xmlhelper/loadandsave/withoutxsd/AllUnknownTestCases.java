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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import junit.textui.TestRunner;

public class AllUnknownTestCases extends LoadAndSaveUnknownTestCases {
    public AllUnknownTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd.AllUnknownTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/withoutxsd/purchaseOrder.xml");
    }

    protected String getControlWriteFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/withoutxsd/purchaseOrder_allUnknown_write.xml");
    }

    protected String getSchemaName() {
        return null;
    }

    protected void verifyAfterLoad(XMLDocument document) {
        assertNull(document.getRootObject().getContainer());
        DataObject po = document.getRootObject();
        assertNotNull(po);

        assertNull(xsdHelper.getGlobalProperty("http://www.example.org", "globalTest", true));
        assertNull(xsdHelper.getGlobalProperty("http://www.example.org", "globalTest", false));
    }
}
