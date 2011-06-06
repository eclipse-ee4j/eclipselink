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

public class NonNullDefaultRootPersonTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/complex/inheritance/personNonNullRoot.xml";
    /**
      <?xml version="1.0" encoding="UTF-8"?>
      <oxm:pRoot xsi:type="oxm:person" xmlns:oxm="test" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <name>Joe Smith</name>
      </oxm:pRoot>
     */
    protected final static String CONTROL_PERSON_NAME = "Joe Smith";
    protected final static int CONTROL_ID = 15;
    protected final static String CONTROL_ELEMENT_NAME = "oxm:blah";
    protected final static String CONTROL_NAMESPACE_URI = "test";
    
    public NonNullDefaultRootPersonTestCases(String name) throws Exception {
        super(name);
        setControlDocument(getXMLResource());        
        setProject(getTopLinkProject());
    }

    public Project getTopLinkProject() {
        Project p= new XMLRootComplexInheritanceProject();
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
    
     public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlroot.complex.inheritance.NonNullDefaultRootPersonTestCases" };
      
        TestRunner.main(arguments);
    }
    /*
    public void testObjectToXMLStringWriter() throws Exception {
      super.testObjectToXMLStringWriter();
    }
    
      public void testObjectToXMLStringWriter() throws Exception {
        StringWriter writer = new StringWriter();
        xmlMarshaller.marshal(getWriteControlObject(), writer);
       // System.out.println("#################");
        System.out.println(writer.toString());
        
        StringReader reader = new StringReader(writer.toString());
        
        
       // System.out.println("#################");
        InputSource inputSource = new InputSource(reader);
        
        
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();

        objectToXMLDocumentTest(testDocument);
    }
    
     // Unmarshal tests
    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(getXMLResource());
        Object testObject = xmlUnmarshaller.unmarshal(instream, Employee.class);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromDocument() throws Exception {
        Object testObject = xmlUnmarshaller.unmarshal(getControlDocument(), Employee.class);
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromURL() throws Exception {
        java.net.URL url = ClassLoader.getSystemResource(getXMLResource());
        Object testObject = xmlUnmarshaller.unmarshal(url, Employee.class);
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

            this.assertEquals(controlObj.getRootElementLocalName(), testObj.getRootElementLocalName());
            this.assertEquals(controlObj.getRootElementPrefix(), testObj.getRootElementPrefix());
            this.assertEquals(controlObj.getRootElementURI(), testObj.getRootElementURI());
            this.assertEquals(controlObj.getObject(), testObj.getObject());
        } else {
            this.assertEquals(getReadControlObject(), testObject);
        }
    }
    
      // DOES NOT APPLY
    public void testUnmarshallerHandler() throws Exception {
    }

    public void testMyTest() throws Exception {
        Person person= new Person();
        person.setName(CONTROL_PERSON_NAME);
        
        Employee emp = new Employee();
        emp.setName(CONTROL_PERSON_NAME);
        emp.setEmpId(CONTROL_ID);

        XMLRoot pxmlRoot = new XMLRoot();
        pxmlRoot.setRootElementName("blah");
        //pxmlRoot.setRootElementURI(CONTROL_NAMESPACE_URI);
        pxmlRoot.setObject(person);
        
        XMLRoot exmlRoot = new XMLRoot();
        exmlRoot.setRootElementName("blah");
        //exmlRoot.setRootElementURI(CONTROL_NAMESPACE_URI);
        exmlRoot.setObject(emp);
        //pxmlRoot.setRootElementName("oxm:pRoot");
        //pxmlRoot.setRootElementURI("test");
        //exmlRoot.setRootElementName("oxm:pRoot");
        //exmlRoot.setRootElementURI("test");
        Object objectToWrite = exmlRoot;
        //Object objectToWrite = person;
        
         StringWriter writer = new StringWriter();
        xmlMarshaller.marshal(objectToWrite, writer);
        System.out.println("#################Marshal");
        System.out.println(writer.toString());
        System.out.println("#################Marshal");
        
    }
     public void testMyTest2() throws Exception {
     System.out.println("################# UNMARSHAL");
     String resource = "org/eclipse/persistence/testing/oxm/xmlroot/complex/inheritance/employee.xml";
     InputStream instream = ClassLoader.getSystemResourceAsStream(resource);
        Object testObject = xmlUnmarshaller.unmarshal(instream);
        instream.close();     
     
     System.out.println("################# UNMARSHAL");
     }*/
}
