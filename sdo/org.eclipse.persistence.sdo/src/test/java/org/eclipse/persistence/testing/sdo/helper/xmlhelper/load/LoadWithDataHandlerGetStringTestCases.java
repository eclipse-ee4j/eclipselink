/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.load;

import commonj.sdo.helper.XMLDocument;
import commonj.sdo.DataObject;

public class LoadWithDataHandlerGetStringTestCases extends SDOXMLHelperLoadTestCases {
    private static String CONTROL_BASE64_VALUE="eHdmb3Rh";

    public LoadWithDataHandlerGetStringTestCases(String name) {
        super(name);
    }

    @Override
    protected String getFileNameToLoad() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datahandler_base64.xml");
    }

    @Override
    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/DataHandler_Base64.xsd";
    }

    @Override
    protected void verifyDocument(XMLDocument document) {
        super.verifyDocument(document);

        DataObject obj = document.getRootObject();
        assertTrue(obj.isSet("simpleDHBinary"));
        String value = obj.getString("simpleDHBinary");
        assertTrue("Unexpected String Value: " + value, value.equals(CONTROL_BASE64_VALUE));
    }

    @Override
    protected void createTypes() {
    }
}
