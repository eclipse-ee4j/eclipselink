/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
