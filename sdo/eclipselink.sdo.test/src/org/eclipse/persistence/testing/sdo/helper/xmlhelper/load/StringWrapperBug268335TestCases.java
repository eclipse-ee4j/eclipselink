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
//     rbarkhouse - 2009-03-13 09:49:35 - initial implementation
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.load;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

public class StringWrapperBug268335TestCases extends SDOXMLHelperLoadTestCases {
    public StringWrapperBug268335TestCases(String name) {
        super(name);
    }

    protected String getFileNameToLoad() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/load/268335-string.xml");
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/load/268335.xsd";
    }

    protected void verifyDocument(XMLDocument document) {
        super.verifyDocument(document);

        DataObject stringDO = document.getRootObject();
        String doClassName = stringDO.getClass().getCanonicalName();

        // Test that the root object's class is an instance of StringWrapperImpl,
        // not StringsWrapperImpl (bug 268335)
        assertEquals("The wrong wrapper type was created.", doClassName, "org.eclipse.persistence.sdo.types.SDOWrapperType.StringWrapperImpl");
    }

    protected void createTypes() {
    }
}
