/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlroot.complex;

import java.io.InputStream;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.xmlroot.Person;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class XMLRootComplexTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/complex/employee.xml";
    protected final static String CONTROL_PERSON_NAME = "Joe Smith";
    protected final static String CONTROL_ELEMENT_NAME = "oxm:employee";
    protected final static String CONTROL_NAMESPACE_URI = "test";

    public XMLRootComplexTestCases(String name) throws Exception {
        super(name);
        setControlDocument(getXMLResource());
        setProject(getTopLinkProject());
    }

    public Project getTopLinkProject() {
        return new XMLRootComplexProject();
    }

    protected Object getControlObject() {
        Person peep = new Person();
        peep.setName(CONTROL_PERSON_NAME);

        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CONTROL_ELEMENT_NAME);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(peep);
        return xmlRoot;
    }

    // Unmarshal tests
    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(getXMLResource());
        Object testObject = xmlUnmarshaller.unmarshal(instream, Person.class);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromDocument() throws Exception {
        Object testObject = xmlUnmarshaller.unmarshal(getControlDocument(), Person.class);
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromURL() throws Exception {
        java.net.URL url = ClassLoader.getSystemResource(getXMLResource());
        Object testObject = xmlUnmarshaller.unmarshal(url, Person.class);
        xmlToObjectTest(testObject);
    }

    public void xmlToObjectTest(Object testObject) throws Exception {
        log("\n**testXMLDocumentToObject**");
        log("Expected:");
        log(getReadControlObject().toString());
        log("Actual:");
        log(testObject.toString());

        if (getReadControlObject() instanceof XMLRoot) {
            XMLRoot controlObj = (XMLRoot)getReadControlObject();
            XMLRoot testObj = (XMLRoot)testObject;

            this.assertEquals(controlObj.getLocalName(), testObj.getLocalName());            
            this.assertEquals(controlObj.getNamespaceURI(), testObj.getNamespaceURI());
            this.assertEquals(controlObj.getObject(), testObj.getObject());
        } else {
            this.assertEquals(getReadControlObject(), testObject);
        }
    }

    // DOES NOT APPLY
    public void testUnmarshallerHandler() throws Exception {
    }

    public String getXMLResource() {
        return XML_RESOURCE;
    }
}