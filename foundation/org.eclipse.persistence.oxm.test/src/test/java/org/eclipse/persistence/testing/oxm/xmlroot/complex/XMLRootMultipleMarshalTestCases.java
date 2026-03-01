/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.xmlroot.complex;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

import java.io.StringWriter;

public class XMLRootMultipleMarshalTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/complex/multiple_marshal.xml";

    public XMLRootMultipleMarshalTestCases(String name) throws Exception {
        super(name);
        setControlDocument(getXMLResource());
        setProject(new EmployeeProject());
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

    @Override
    protected Object getControlObject() {
        Employee employee = new Employee();
        Address address= new Address();
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName("my-address");
        xmlRoot.setNamespaceURI("http://www.example.org/2");
        xmlRoot.setObject(address);
        employee.setAnyObject(xmlRoot);

        return xmlRoot;

    }

    public String getXMLResource() {
        return XML_RESOURCE;
    }

    @Override
    public void testObjectToXMLDocument() throws Exception {
        StringWriter dummyWriter = new StringWriter();
        xmlMarshaller.marshal(getWriteControlObject(), dummyWriter);

        super.testObjectToXMLDocument();
    }

    @Override
    public void testObjectToXMLStringWriter() throws Exception {
        StringWriter dummyWriter = new StringWriter();
        xmlMarshaller.marshal(getWriteControlObject(), dummyWriter);

        super.testObjectToXMLStringWriter();
    }

    @Override
    public void testObjectToContentHandler() throws Exception {
        StringWriter dummyWriter = new StringWriter();
        xmlMarshaller.marshal(getWriteControlObject(), dummyWriter);

        super.testObjectToContentHandler();
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "oracle.toplink.testing.ox.xmlroot.complex.XMLRootNullSchemaReferenceTestCases" };
        junit.textui.TestRunner.main(arguments);
    }

}
