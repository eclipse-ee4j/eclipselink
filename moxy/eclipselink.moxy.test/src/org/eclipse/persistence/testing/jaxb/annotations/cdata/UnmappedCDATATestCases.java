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
//     Blaise Doughan - 2.3.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.cdata;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class UnmappedCDATATestCases extends JAXBTestCases {

    private static final String XML_RESOURCE_UNMARSHAL = "org/eclipse/persistence/testing/jaxb/annotations/xmlcdata/unmapped.xml";
    private static final String XML_RESOURCE_MARSHAL = "org/eclipse/persistence/testing/jaxb/annotations/xmlcdata/employee.xml";

    public UnmappedCDATATestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Employee.class});
        setControlDocument(XML_RESOURCE_UNMARSHAL);
        setWriteControlDocument(XML_RESOURCE_MARSHAL);
    }

    @Override
    public Employee getControlObject() {
        Employee emp = new Employee();
        emp.name = "Jane Doe";
        emp.xmlData = "<root><child>A string wrapped in cdata</child></root>";
        emp.nestedCData = "<![CDATA[nested]]>";
        emp.anotherCData1 = "here ]> > ]] ] is no replacement";
        emp.anotherCData2 = "here ]]]>> is one replacement only";
        emp.anotherCData3 = "]]>]]>]]>";
        return emp;
    }

    @Override
    public void testObjectToContentHandler() {
        //CDATA sections don't work with content handlers
    }

}
