/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - March 24/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.load;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

public class LoadWithDataHandlerIsSetTestCases extends SDOXMLHelperLoadTestCases  {

    public LoadWithDataHandlerIsSetTestCases(String name) {
        super(name);
    }

    @Override
    protected String getFileNameToLoad() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datahandler_unset.xml");
    }

    @Override
    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/DataHandler_Base64.xsd";
    }

    @Override
    protected void verifyDocument(XMLDocument document) {
        super.verifyDocument(document);

        DataObject obj = document.getRootObject();
        assertFalse(obj.isSet("simpleDHBinary"));
    }

    @Override
    protected void createTypes() {
    }

}
