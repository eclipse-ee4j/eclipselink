/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.xmlroot.complex.inheritance;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.Person;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class NonNullDefaultRootMatchingPersonXMLRootTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/complex/inheritance/personNonNullRootMatchingXMLRoot.xml";
    /**
      <?xml version="1.0" encoding="UTF-8"?>
      <oxm:pRoot xsi:type="oxm:person" xmlns:oxm="test" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <name>Joe Smith</name>
      </oxm:pRoot>
     */
    protected final static String CONTROL_PERSON_NAME = "Joe Smith";
    protected final static int CONTROL_ID = 15;
    protected final static String CONTROL_NAMESPACE_URI = "test";

    public NonNullDefaultRootMatchingPersonXMLRootTestCases(String name) throws Exception {
        super(name);
        setControlDocument(getXMLResource());
        setProject(getTopLinkProject());
    }

    public Project getTopLinkProject() {
        Project p = new XMLRootComplexInheritanceProject();
        ((XMLDescriptor)p.getDescriptor(Person.class)).setDefaultRootElement("oxm:pRoot");
        ((XMLDescriptor)p.getDescriptor(Employee.class)).setDefaultRootElement("oxm:pRoot");
        return p;
    }

    public String getXMLResource() {
        return XML_RESOURCE;
    }

    protected Object getControlObject() {
        Person p = new Person();
        p.setName(CONTROL_PERSON_NAME);
        return p;
    }

    public Object getWriteControlObject() {
        Person p = new Person();
        p.setName(CONTROL_PERSON_NAME);

        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setObject(p);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setLocalName("oxm:pRoot");
        return xmlRoot;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlroot.complex.inheritance.NonNullDefaultRootMatchingPersonXMLRootTestCases" };
        TestRunner.main(arguments);
    }

}
