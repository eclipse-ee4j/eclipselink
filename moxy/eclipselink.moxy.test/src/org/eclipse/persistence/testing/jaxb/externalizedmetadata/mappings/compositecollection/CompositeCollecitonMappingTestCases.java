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
    private static final int RO_ID = 66;
    private static final String RO_CITY = "Woodlawn";
    private static final String RO_STREET = "465 Bayview Dr.";
    private static final String RO_PROVINCE = "ON";
    private static final String RO_POSTAL = "K0A3M0";
    private static final int WO_ID = 77;
    private static final String WO_CITY = "Woodlawn";
    private static final String WO_STREET = "463 Bayview Dr.";
    private static final String WO_PROVINCE = "ON";
    private static final String WO_POSTAL = "K0A3M0";
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
    public Employee getControlObject() {
        // setup Addresses
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
        adds.add(null);
        adds.add(wAddress);
        adds.add(null);

        // setup read-only Address list
        Address roAddress = new Address();
        roAddress.id = RO_ID;
        roAddress.city = RO_CITY;
        roAddress.street = RO_STREET;
        roAddress.province = RO_PROVINCE;
        roAddress.postalCode = RO_POSTAL;

        List<Address> roAdds = new ArrayList<Address>();
        roAdds.add(roAddress);
        
        // setup write-only Address list
        Address woAddress = new Address();
        woAddress.id = WO_ID;
        woAddress.city = WO_CITY;
        woAddress.street = WO_STREET;
        woAddress.province = WO_PROVINCE;
        woAddress.postalCode = WO_POSTAL;

        List<Address> woAdds = new ArrayList<Address>();
        woAdds.add(woAddress);

        Employee emp = new Employee();
        emp.id = 101;
        emp.addresses = adds;
        emp.readOnlyAddressList = roAdds;
        emp.writeOnlyAddressList = woAdds;
        return emp;
    }
    
    public void testSchemaGenAndValidation() {
        // validate employee schema
        compareSchemas(employeeResolver.schemaFiles.get(EMPLOYEES_NS), new File(PATH + "employee.xsd"));
        // validate contacts schema
        compareSchemas(employeeResolver.schemaFiles.get(CONTACTS_NS), new File(PATH + "contacts.xsd"));
        
        // validate employee.xml
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPLOYEES_NS, employeeResolver);
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);

        // validate write-employee.xml
        src = PATH + "write-employee.xml";
        result = validateAgainstSchema(src, EMPLOYEES_NS, employeeResolver);
        assertTrue("Instance doc validation (write-employee.xml) failed unxepectedly: " + result, result == null);
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
        // tweak control object
        Employee ctrlEmp = getControlObject();
        //unmarshal null will result in new Address instances being set in the object
        ctrlEmp.addresses.remove(1);
        ctrlEmp.addresses.add(1, new Address());
        ctrlEmp.addresses.remove(3);
        ctrlEmp.addresses.add(3, new Address());
        // write-only address list will no be read in
        ctrlEmp.writeOnlyAddressList = null;
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
    public void testCompositeCollectionMappingMarshal() {
        // load instance doc
        String src = PATH + "write-employee.xml";
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
            Employee ctrlEmp = getControlObject();
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