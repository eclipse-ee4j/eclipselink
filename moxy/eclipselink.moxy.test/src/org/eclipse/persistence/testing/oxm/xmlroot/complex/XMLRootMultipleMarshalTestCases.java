/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlroot.complex;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XMLRootMultipleMarshalTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/complex/multiple_marshal.xml";

    public XMLRootMultipleMarshalTestCases(String name) throws Exception {
        super(name);
        setControlDocument(getXMLResource());
        setProject(new EmployeeProject());
    }

    public boolean isUnmarshalTest() {
        return false;
    }

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

    public void testObjectToXMLDocument() throws Exception {
        StringWriter dummyWriter = new StringWriter();
        xmlMarshaller.marshal(getWriteControlObject(), dummyWriter);
        
        super.testObjectToXMLDocument();
    }

    public void testObjectToXMLStringWriter() throws Exception {
        StringWriter dummyWriter = new StringWriter();
        xmlMarshaller.marshal(getWriteControlObject(), dummyWriter);
        
        super.testObjectToXMLStringWriter();
    }

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
