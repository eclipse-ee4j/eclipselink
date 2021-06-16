/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - April 6/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.xmlfragment;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

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
            inputStream.close();
            employee.xmlNode = document.getDocumentElement();
            return employee;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
