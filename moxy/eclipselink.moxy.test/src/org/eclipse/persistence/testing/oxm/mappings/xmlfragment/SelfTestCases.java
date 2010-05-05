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
*     bdoughan - April 6/2010 - 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.xmlfragment;

import java.io.InputStream;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class SelfTestCases extends XMLMappingTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/xmlfragment/self.xml";

    public SelfTestCases(String name) throws Exception {
        super(name);
        setProject(new SelfProject());
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        try {
            Employee employee = new Employee();
            employee.firstName = "Jane";
            employee.lastName = "Doe";
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            Document document = parser.parse(inputStream);
            removeEmptyTextNodes(document);
            inputStream.close();
            employee.xmlNode = document.getDocumentElement();
            return employee;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
