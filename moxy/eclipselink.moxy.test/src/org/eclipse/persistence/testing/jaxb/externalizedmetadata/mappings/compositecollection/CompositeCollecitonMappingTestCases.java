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
 * dmccann - March 19/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.compositecollection;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlCompositeCollectionMappings via eclipselink-oxm.xml
 * 
 */
public class CompositeCollecitonMappingTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.compositecollection";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/compositecollection/";
    private static final int HOME_ID = 67;
    private static final String HOME_CITY = "Kanata";
    private static final String HOME_STREET = "66 Lakview Drive";
    private static final String HOME_PROVINCE = "ON";
    private static final String HOME_POSTAL = "K2M2K7";
    private static final int WORK_ID = 76;
    private static final String WORK_CITY = "Ottawa";
    private static final String WORK_STREET = "45 O'Connor St.";
    private static final String WORK_PROVINCE = "ON";
    private static final String WORK_POSTAL = "K1P1A4";
    private static final String EMPLOYEES_NS = "http://www.example.com/employees"; 
    private static final String CONTACTS_NS = "http://www.example.com/contacts"; 
    
    private MySchemaOutputResolver employeeResolver;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public CompositeCollecitonMappingTestCases(String name) {
        super(name);
    }
    
    /**
     * This method's primary purpose id to generate schema(s). Validation of
     * generated schemas will occur in the testXXXGen method(s) below. Note that
     * the JAXBContext is created from this call and is required for
     * marshal/unmarshal, etc. tests.
     * 
     */
    public void setUp() throws Exception {
        super.setUp();
        employeeResolver = generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "employee-oxm.xml", 2);
    }

    /**
     * Create the control Employee object.
     * 
     */
    public Employee getControlObject(boolean nullAddresses) {
        Address hAddress = new Address();
        hAddress.id = HOME_ID;
        hAddress.city = HOME_CITY;
        hAddress.street = HOME_STREET;
        hAddress.province = HOME_PROVINCE;
        hAddress.postalCode = HOME_POSTAL;
        
        Address wAddress = new Address();
        wAddress.id = WORK_ID;
        wAddress.city = WORK_CITY;
        wAddress.street = WORK_STREET;
        wAddress.province = WORK_PROVINCE;
        wAddress.postalCode = WORK_POSTAL;

        List<Address> adds = new ArrayList<Address>();
        adds.add(hAddress);
        if (nullAddresses) { adds.add(null); }
        adds.add(wAddress);
        if (nullAddresses) { adds.add(null); }
        
        Employee emp = new Employee();
        emp.id = 101;
        emp.addresses = adds;
        return emp;
    }
    
    public void testEmployeeSchemaGen() {
        // validate employee schema
        compareSchemas(employeeResolver.schemaFiles.get(EMPLOYEES_NS), new File(PATH + "employee.xsd"));
        // validate contacts schema
        compareSchemas(employeeResolver.schemaFiles.get(CONTACTS_NS), new File(PATH + "contacts.xsd"));
    }
    
    /**
     * Tests XmlCompositeCollectionMapping configuration via eclipselink-oxm.xml. 
     * Here an unmarshal operation is performed. Utilizes xml-attribute and 
     * xml-element.
     * 
     * Positive test.
     */
    public void testCompositeCollectionMappingUnmarshal() {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(PATH + "employee.xml");
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + PATH + "employee.xml" + "]");
        }
        try {
            Employee empObj = (Employee) jaxbContext.createUnmarshaller().unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Accessor method was not called as expected", empObj.wasSetCalled);
            assertTrue("Unmarshal failed:  Employee objects are not equal", getControlObject(false).equals(empObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }

    /**
     * Tests XmlCompositeCollectionMapping configuration via eclipselink-oxm.xml. Here a
     * marshal operation is performed. Utilizes xml-attribute and xml-element
     * 
     * Positive test.
     */
    public void testCompositeCollectionMappingMarshal() {
        // load instance doc
        String src = PATH + "employee.xml";
        // setup control document
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            Employee ctrlEmp = getControlObject(false);
            marshaller.marshal(ctrlEmp, testDoc);
            //marshaller.marshal(ctrlEmp, System.out);
            assertTrue("Accessor method was not called as expected", ctrlEmp.wasGetCalled);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }

    /**
     * Tests XmlCompositeCollectionMapping configuration via eclipselink-oxm.xml. 
     * Here an unmarshal operation is performed. Utilizes xml-attribute and 
     * xml-element.
     * 
     * Positive test.
     */
    public void testCompositeCollectionNullPolicyMappingUnmarshal() {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(PATH + "employee-null-addresses.xml");
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + PATH + "employee.xml" + "]");
        }
        // tweak control object (unmarshal null will result in new Address instances being set in the object)
        Employee ctrlEmp = getControlObject(true);
        ctrlEmp.addresses.remove(1);
        ctrlEmp.addresses.add(1, new Address());
        ctrlEmp.addresses.remove(3);
        ctrlEmp.addresses.add(3, new Address());
        try {
            Employee empObj = (Employee) jaxbContext.createUnmarshaller().unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Accessor method was not called as expected", empObj.wasSetCalled);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(empObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }

    /**
     * Tests XmlCompositeCollectionMapping configuration via eclipselink-oxm.xml. Here a
     * marshal operation is performed. Utilizes xml-attribute and xml-element
     * 
     * Positive test.
     */
    public void testCompositeCollectionNullPolicyMappingMarshal() {
        // load instance doc
        String src = PATH + "employee-null-addresses.xml";
        // setup control document
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            Employee ctrlEmp = getControlObject(true);
            marshaller.marshal(ctrlEmp, testDoc);
            //marshaller.marshal(ctrlEmp, System.out);
            assertTrue("Accessor method was not called as expected", ctrlEmp.wasGetCalled);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
}