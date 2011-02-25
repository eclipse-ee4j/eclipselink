/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-03-13 09:49:35 - initial implementation 
 ******************************************************************************/  
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
