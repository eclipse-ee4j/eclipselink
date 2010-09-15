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
 * dmccann - August 26/2009 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import junit.textui.TestRunner;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests relationship mapping configuration via XmlJoinNode & XmlJoinNodes.
 *
 */
public class XmlJoinNodeTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmljoinnode/";
    private static final String OXM_DOC = PATH + "company-oxm.xml";
    private static final String INVALID_OXM_DOC = PATH + "invalid-xml-join-node-oxm.xml";
    private static final String XSD_DOC = PATH + "company.xsd";
    private static final String WORK_ADD_XSD_DOC = PATH + "work-address.xsd";
    private static final String INSTANCE_DOC = PATH + "company.xml";
    private static final String WORK_ADDRESS_NS = "http://www.example.com";
    private Class[] classes;
    private MySchemaOutputResolver resolver;
    private JAXBContext jCtx;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlJoinNodeTestCases(String name) {
        super(name);
    }
    
    /**
     * This method will be responsible for schema generation, which will create the 
     * JAXBContext we will use.  The eclipselink metadata file will be validated
     * as well.
     * 
     */
    public void setUp() throws Exception {
        super.setUp();
        classes = new Class[] { Company.class };
        // schema generation also creates the JAXBContext
        resolver = generateSchemaWithFileName(classes, CONTEXT_PATH, OXM_DOC, 2);
        jCtx = getJAXBContext();
        assertNotNull("Setup failed: JAXBContext is null", jCtx);
    }
    
    /**
     * Return the control Company object.
     */
    private Company getControlObject() {
        Address ottawa100 = new Address(100, "45 O'Connor St.", "400", "Ottawa", "K1P1A4");
        Address ottawa200 = new Address(200, "1 Anystreet Rd.", "9", "Ottawa", "K4P1A2");
        Address kanata100 = new Address(100, "99 Some St.", "1001", "Kanata", "K0A3m0");
        Employee emp101 = new Employee(101, ottawa100);
        Employee emp102 = new Employee(102, kanata100);
        ArrayList<Employee> empList = new ArrayList<Employee>();
        empList.add(emp101);
        empList.add(emp102);
        ArrayList<Address> addList = new ArrayList<Address>();
        addList.add(kanata100);
        addList.add(ottawa100);
        addList.add(ottawa200);
        return new Company(empList, addList);
    }

    /**
     * Validate schema generation.
     * 
     * Positive test.
     */
    public void testSchemaGen() {
        // validate company schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(XSD_DOC));
        
        // validate work address schema
        compareSchemas(resolver.schemaFiles.get(WORK_ADDRESS_NS), new File(WORK_ADD_XSD_DOC));
    }

    /**
     * Verifies that the xml-key entries were processed and set on 
     * the Address descriptor.
     * 
     * Positive test.
     */
    public void testPrimaryKeysWereSet() {
        XMLDescriptor xdesc = jCtx.getXMLContext().getDescriptor(new QName("business-address"));
        Vector<String> pkFields = xdesc.getPrimaryKeyFieldNames();
        assertTrue("Expected [2] primary key fields for Address, but were [" + pkFields.size() + "]", pkFields.size() == 2);
        assertTrue("Expected primary key field [@id] for Address, but was [" + pkFields.elementAt(0) + "]", pkFields.elementAt(0).equals("@id"));
        assertTrue("Expected primary key field [city/text()] for Address, but was [" + pkFields.elementAt(1) + "]", pkFields.elementAt(1).equals("city/text()"));
    }
    
    /**
     * Test unmarshal.
     * 
     * Positive test.
     */
    public void testUnmarshal() {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(INSTANCE_DOC);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + INSTANCE_DOC + "]");
        }
        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            Company testCo = (Company) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshal failed - Company is null", testCo);
            // verify employee 101
            Employee emp = testCo.employees.get(0);
            assertNotNull(emp);
            Address workAddress = emp.workAddress;
            assertNotNull(workAddress);
            assertNotNull(workAddress.id);
            assertNotNull(workAddress.cityName);
            assertTrue("Expected work address id [ba100] but was [" + workAddress.id + "]", workAddress.id == 100);
            assertTrue("Expected work address city [Ottawa] but was [" + workAddress.cityName + "]", workAddress.cityName.equals("Ottawa"));
            // verify employee 102
            emp = testCo.employees.get(1);
            assertNotNull(emp);
            workAddress = emp.workAddress;
            assertNotNull(workAddress);
            assertNotNull(workAddress.id);
            assertNotNull(workAddress.cityName);
            assertTrue("Expected work address id [ba100] but was [" + workAddress.id + "]", workAddress.id == 100);
            assertTrue("Expected work address city [Kanata] but was [" + workAddress.cityName + "]", workAddress.cityName.equals("Kanata"));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }
    
    /**
     * Test marshal.
     * 
     * Positive test.
     */
    public void testMarshal() {
        // setup control document
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(INSTANCE_DOC);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + INSTANCE_DOC + "].");
        }

        Marshaller marshaller = jCtx.createMarshaller();
        try {
            marshaller.marshal(getControlObject(), testDoc);
            // marshaller.marshal(getControlObject(), System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }
    
    /**
     * Tests that an exception is thrown if XmlJoinNode is set on an invalid Property,
     * as in the case where the Property type is String.
     * 
     * Negative test.
     */
    public void testInvalidXmlJoinNode() {
        boolean exception = false;
        try {
            createContext(classes, CONTEXT_PATH, INVALID_OXM_DOC);
        } catch (JAXBException e) {
            exception = true;
        }
        assertTrue("The excepted exception was not thrown.", exception);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode.XmlJoinNodeTestCases" };
        TestRunner.main(arguments);
    }
}
