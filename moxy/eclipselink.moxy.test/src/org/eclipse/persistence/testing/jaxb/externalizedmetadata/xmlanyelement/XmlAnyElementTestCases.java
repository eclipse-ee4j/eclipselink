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
 * dmccann - October 26/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests XmlAnyElement via eclipselink-oxm.xml
 *
 */
public class XmlAnyElementTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlanyelement/";
    public static final String RETURN_STRING = "Giggity";
    public static final String STUFF_STRING_VALUE = "This is some stuff";
    public static final String STUFF_STRING_VALUE2 = "This is some more stuff";
    private Class[] employeeClassArray;
    private Class[] employeeWithListClassArray;
    private Employee ctrlEmp;
    private EmployeeWithList ctrlEmpWithList;
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlAnyElementTestCases(String name) {
        super(name);
    }

    /**
     * Lazy load the Employee class array.
     * @return
     */
    private Class[] getEmployeeClassArray() {
        if (employeeClassArray == null) {
            employeeClassArray = new Class[] { Employee.class };
        }
        return employeeClassArray;
    }

    /**
     * Lazy load the EmployeeWithList class array.
     * @return
     */
    private Class[] getEmployeeWithListClassArray() {
        if (employeeWithListClassArray == null) {
            employeeWithListClassArray = new Class[] { EmployeeWithList.class };
        }
        return employeeWithListClassArray;
    }

    /**
     * Lazy load the control Employee.
     * @return
     */
    private Employee getControlEmployee() {
        if (ctrlEmp == null) {
            ctrlEmp = new Employee();
            ctrlEmp.a = 1;
            ctrlEmp.b = "3";
            Element elt = null;
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                elt = doc.createElement(MyDomAdapter.STUFF_STR);
                elt.appendChild(doc.createTextNode(STUFF_STRING_VALUE));
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            ctrlEmp.stuff = elt;
        }
        return ctrlEmp;
    }
    
    /**
     * Lazy load the control Employee.
     * @return
     */
    private EmployeeWithList getControlEmployeeWithList() {
        if (ctrlEmpWithList == null) {
            ctrlEmpWithList = new EmployeeWithList();
            ctrlEmpWithList.a = 1;
            ctrlEmpWithList.b = "3";
            ctrlEmpWithList.stuff = new ArrayList<Object>();
            Element elt1 = null;
            Element elt2 = null;
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                elt1 = doc.createElement(MyDomAdapter.STUFF_STR);
                elt1.appendChild(doc.createTextNode(STUFF_STRING_VALUE));
                elt2 = doc.createElement(MyDomAdapter.STUFF_STR);
                elt2.appendChild(doc.createTextNode(STUFF_STRING_VALUE2));
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            ctrlEmpWithList.stuff.add(elt1);
            ctrlEmpWithList.stuff.add(elt2);
        }
        return ctrlEmpWithList;
    }

    /**
     * Lazy load the control Employee.
     * @return
     */
    private Employee getControlEmployeeForAdapterTests() {
        if (ctrlEmp == null) {
            ctrlEmp = new Employee();
            ctrlEmp.a = 1;
            ctrlEmp.b = "3";
            Dom stuff = new Dom();
            stuff.stuffStr = STUFF_STRING_VALUE;
            ctrlEmp.stuff = stuff;
        }
        return ctrlEmp;
    }
    
    /**
     * Lazy load the control Employee.
     * @return
     */
    private EmployeeWithList getControlEmployeeWithListForAdapterTests() {
        if (ctrlEmpWithList == null) {
            ctrlEmpWithList = new EmployeeWithList();
            ctrlEmpWithList.a = 1;
            ctrlEmpWithList.b = "3";
            ctrlEmpWithList.stuff = new ArrayList<Object>();
            Dom stuff = new Dom();
            stuff.stuffStr = STUFF_STRING_VALUE;
            ctrlEmpWithList.stuff.add(stuff);
            stuff = new Dom();
            stuff.stuffStr = STUFF_STRING_VALUE2;
            ctrlEmpWithList.stuff.add(stuff);
        }
        return ctrlEmpWithList;
    }

    /**
     * Tests schema generation for @XmlAnyElement override via 
     * eclipselink-oxm.xml
     * 
     * Positive test.
     */
    public void testSchemaGeneration() {
        String metadataFile = PATH + "eclipselink-oxm-xml-list.xml";
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(getEmployeeWithListClassArray(), CONTEXT_PATH, metadataFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred creating the JAXBContext: " + e.getMessage());
        }

        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        jCtx.generateSchema(outputResolver);
        
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        // validate instance doc
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }    
    
    /**
     * Tests that the DomHandler is set properly.  This will make use of a DomHandler that
     * returns the string "Giggity" in the getElement() method.  This will allow us to
     * detect that the handler was used during unmarshal.
     * 
     * Positive test.
     */
    public void testDomHandler() {
        String metadataFile = PATH + "eclipselink-oxm-dom-handler.xml";
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(getEmployeeClassArray(), CONTEXT_PATH, metadataFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred creating the JAXBContext: " + e.getMessage());
        }
  
        // test unmarshal
        Employee emp = null;
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            String src = PATH + "employee.xml";
            emp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", emp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document.");
        }

        assertNotNull("The Employee did not umnmarshal properly: 'stuff' is null.", emp.stuff);
        assertTrue("The Employee did not umnmarshal properly: expected 'stuff' to be [" + RETURN_STRING + "] but was [" + emp.stuff + "]", emp.stuff.equals(RETURN_STRING));
    }
    
    /**
     * Tests @XmlAnyElement via eclipselink-oxm.xml.  Here, a number of
     * overrides are performed. An unmarshal operation is performed to 
     * ensure the Any is processed correctly.
     * 
     * Positive test.
     */
    public void testXmlAnyElementUnmarshal() {
        String metadataFile = PATH + "eclipselink-oxm.xml";
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(getEmployeeClassArray(), CONTEXT_PATH, metadataFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred creating the JAXBContext: " + e.getMessage());
        }

        // test unmarshal
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            String src = PATH + "employee-default-ns.xml";
            Employee emp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", emp);
            assertEquals(getControlEmployee(), emp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document.");
        }
    }
        
    /**
     * Tests @XmlAnyElement via eclipselink-oxm.xml.  Here, a number of
     * overrides are performed. Amarshal operation is performed to ensure 
     * the Any is processed correctly.
     * 
     * Positive test.
     */
    public void testXmlAnyElementMarshal() {
        String metadataFile = PATH + "eclipselink-oxm.xml";
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(getEmployeeClassArray(), CONTEXT_PATH, metadataFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred creating the JAXBContext: " + e.getMessage());
        }

        Document testDoc = parser.newDocument();
        
        // test marshal
        Marshaller marshaller = jCtx.createMarshaller();
        try {
            marshaller.marshal(getControlEmployee(), testDoc);
            String src = PATH + "employee-default-ns.xml";
            Document ctrlDoc = parser.newDocument();
            try {
                ctrlDoc = getControlDocument(src);
            } catch (Exception e) {
                e.printStackTrace();
                fail("An unexpected exception occurred loading control document [" + src + "].");
            }
            assertTrue("The Employee did not marshal correctly - document comparison failed: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred marshalling the Employee.");
        }
    }

    /**
     * Tests lax set to true.  There is a sub-employee element which should be
     * unmarshalled to an Employee, since lax is true and the Employee is 
     * known, i.e. can be eagerly marshalled to.
     * 
     * Positive test.
     */
    public void testLaxTrue() {
        String metadataFile = PATH + "eclipselink-oxm-lax.xml";
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(getEmployeeClassArray(), CONTEXT_PATH, metadataFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred creating the JAXBContext: " + e.getMessage());
        }
      
        // setup the control Employee
        Employee ctrlEmp = new Employee();
        ctrlEmp.a = 1;
        ctrlEmp.b = "3";
        Employee emp = new Employee();
        emp.a = 666;
        emp.b = "999";
        ctrlEmp.stuff = emp;

        // test unmarshal
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            String src = PATH + "employee-with-employee.xml";
            Employee employee = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", employee);
            assertEquals(ctrlEmp, employee);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document.");
        }
    }

    /**
     * Tests lax set to false.  There is a sub-employee element which should be
     * unmarshalled to an Element, since lax is false.
     * 
     * Positive test.
     */
    public void testLaxFalse() {
        String metadataFile = PATH + "eclipselink-oxm.xml";
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(getEmployeeClassArray(), CONTEXT_PATH, metadataFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred creating the JAXBContext: " + e.getMessage());
        }
    	
        // setup the control Employee
        Element empElt = null;
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element aElt = doc.createElement("a");
            aElt.appendChild(doc.createTextNode("666"));
            Element bElt = doc.createElement("b");
            bElt.appendChild(doc.createTextNode("999"));
            empElt = doc.createElement("employee");
            empElt.appendChild(aElt);
            empElt.appendChild(bElt);
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        }
        Employee ctrlEmp = new Employee();
        ctrlEmp.a = 1;
        ctrlEmp.b = "3";
        ctrlEmp.stuff = empElt;

        // test unmarshal
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            String src = PATH + "employee-with-employee.xml";
            Employee emp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", emp);
            assertEquals(ctrlEmp, emp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document.");
        }
    }

    /**
     * Tests AnyObjectMapping.
     * 
     * Positive test.
     */
    public void testXmlAdapterMarshalSingle() {
        String metadataFile = PATH + "eclipselink-oxm-xml-adapter.xml";
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(getEmployeeClassArray(), CONTEXT_PATH, metadataFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred creating the JAXBContext: " + e.getMessage());
        }

        Marshaller marshaller = jCtx.createMarshaller();
        try {
            Document testDoc = parser.newDocument();
            marshaller.marshal(getControlEmployeeForAdapterTests(), testDoc);
            String src = PATH + "employee-default-ns.xml";
            Document ctrlDoc = parser.newDocument();
            try {
                ctrlDoc = getControlDocument(src);
            } catch (Exception e) {
                e.printStackTrace();
                fail("An unexpected exception occurred loading control document [" + src + "].");
            }
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred marshalling the Employee: " + e.getMessage());
        }
    }

    /**
     * Tests AnyObjectMapping.
     * 
     * Positive test.
     */
    public void testXmlAdapterUnmarshalSingle() {
        String metadataFile = PATH + "eclipselink-oxm-xml-adapter.xml";
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(getEmployeeClassArray(), CONTEXT_PATH, metadataFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred creating the JAXBContext: " + e.getMessage());
        }

        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            String src = PATH + "employee-default-ns.xml";
            Employee testEmp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", testEmp);
            assertEquals(getControlEmployeeForAdapterTests(), testEmp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document: " + e.getMessage());
        }
    }
    
    /**
     * Tests AnyCollectionMapping.
     * 
     * Positive test.
     */
    public void testXmlAdapterMarshalCollection() {
        String metadataFile = PATH + "eclipselink-oxm-xml-adapter-list.xml";
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(getEmployeeWithListClassArray(), CONTEXT_PATH, metadataFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred creating the JAXBContext: " + e.getMessage());
        }

        Marshaller marshaller = jCtx.createMarshaller();
        try {
            Document testDoc = parser.newDocument();
            marshaller.marshal(getControlEmployeeWithListForAdapterTests(), testDoc);

            String src = PATH + "employee-with-list.xml";
            Document ctrlDoc = parser.newDocument();
            try {
                ctrlDoc = getControlDocument(src);
            } catch (Exception e) {
                e.printStackTrace();
                fail("An unexpected exception occurred loading control document [" + src + "].");
            }
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred marshalling the Employee: " + e.getMessage());
        }
    }

    /**
     * Tests AnyCollectionMapping.
     * 
     * Positive test.
     */
    public void testXmlAdapterUnmarshalCollection() {
        String metadataFile = PATH + "eclipselink-oxm-xml-adapter-list.xml";
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(getEmployeeWithListClassArray(), CONTEXT_PATH, metadataFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred creating the JAXBContext: " + e.getMessage());
        }
        
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            String src = PATH + "employee-with-list.xml";
            EmployeeWithList testEmp = (EmployeeWithList) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", testEmp);
            assertEquals(getControlEmployeeWithListForAdapterTests(), testEmp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document: " + e.getMessage());
        }
    }
}
