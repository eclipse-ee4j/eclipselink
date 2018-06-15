/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.load;

import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.DataObject;

public class LoadWithDataHandlerGetStringTestCases extends SDOXMLHelperLoadTestCases {
    private static String CONTROL_BASE64_VALUE="eHdmb3Rh";

    public LoadWithDataHandlerGetStringTestCases(String name) {
        super(name);
    }

    protected String getFileNameToLoad() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datahandler_base64.xml");
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/DataHandler_Base64.xsd";
    }

    protected void verifyDocument(XMLDocument document) {
        super.verifyDocument(document);

        DataObject obj = document.getRootObject();
        assertTrue(obj.isSet("simpleDHBinary"));
        String value = obj.getString("simpleDHBinary");
        assertTrue("Unexpected String Value: " + value, value.equals(CONTROL_BASE64_VALUE));
    }

    protected void createTypes() {
    }
}
