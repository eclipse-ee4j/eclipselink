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
 * mmacivor - April 20/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmlinversereference;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 *
 */
public class XmlInverseReferenceMappingTestCases extends ExternalizedMetadataTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmlinversereference/root.xml";
    private static final String CONTROL_ID = "a222";
    private static final String CONTROL_NAME = "Joe Smith";
    private static final String CONTROL_ADD_ID_1 = "a199";
    private static final String CONTROL_ADD_STREET_1 = "Some Other St.";
    private static final String CONTROL_ADD_CITY_1 = "Anyothertown";
    private static final String CONTROL_ADD_COUNTRY_1 = "Canada";
    private static final String CONTROL_ADD_ZIP_1 = "X0X0X0";
    private static final String CONTROL_ADD_ID_2 = "a99";
    private static final String CONTROL_ADD_STREET_2 = "Some St.";
    private static final String CONTROL_ADD_CITY_2 = "Anytown";
    private static final String CONTROL_ADD_COUNTRY_2 = "Canada";
    private static final String CONTROL_ADD_ZIP_2 = "X0X0X0";
    private static final String CONTROL_PHONE_ID_1 = "a123";
    private static final String CONTROL_PHONE_NUM_1 = "613-123-4567";
    private static final String CONTROL_PHONE_ID_2 = "a456";
    private static final String CONTROL_PHONE_NUM_2 = "613-234-5678";

    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmlinversereference";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmlinversereference/";
    private MySchemaOutputResolver employeeResolver;
    
    public XmlInverseReferenceMappingTestCases(String name) throws Exception {
        super(name);
    }

    protected Root getControlObject() {
        Employee employee = new Employee();
        employee.id = CONTROL_ID;
        employee.name = CONTROL_NAME;
        
        Address address = new Address();
        address.id = CONTROL_ADD_ID_1;
        address.street = CONTROL_ADD_STREET_1;
        address.city = CONTROL_ADD_CITY_1;
        address.country = CONTROL_ADD_COUNTRY_1;
        address.zip = CONTROL_ADD_ZIP_1;
        address.emp = new Vector<Employee>();
        address.emp.add(employee);
        
        employee.address = address;
        
        employee.phones = new ArrayList();
        
        PhoneNumber num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_1;
        num.number = CONTROL_PHONE_NUM_1;
        num.emp = employee;
        employee.phones.add(num);
        
        num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_2;
        num.number = CONTROL_PHONE_NUM_2;
        num.emp = employee;
        employee.phones.add(num);
        
        Root root = new Root();
        root.employee = employee;
        return root;
    }

    public Root getWriteControlObject() {
        ArrayList rootAddresses = new ArrayList();
        ArrayList rootPhones = new ArrayList();

        Employee employee = new Employee();
        employee.id = CONTROL_ID;
        employee.name = CONTROL_NAME;
        
        Address address = new Address();
        address.id = CONTROL_ADD_ID_1;
        address.street = CONTROL_ADD_STREET_1;
        address.city = CONTROL_ADD_CITY_1;
        address.country = CONTROL_ADD_COUNTRY_1;
        address.zip = CONTROL_ADD_ZIP_1;
        address.emp = new Vector<Employee>();
        address.emp.add(employee);
        rootAddresses.add(address);
        
        employee.address = address;
        
        address = new Address();
        address.id = CONTROL_ADD_ID_2;
        address.street = CONTROL_ADD_STREET_2;
        address.city = CONTROL_ADD_CITY_2;
        address.country = CONTROL_ADD_COUNTRY_2;
        address.zip = CONTROL_ADD_ZIP_2;
        rootAddresses.add(address);
        employee.phones = new ArrayList();
        
        PhoneNumber num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_1;
        num.number = CONTROL_PHONE_NUM_1;
        num.emp = employee;
        employee.phones.add(num);
        rootPhones.add(num);
        
        num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_2;
        num.number = CONTROL_PHONE_NUM_2;
        num.emp = employee;
        employee.phones.add(num);
        rootPhones.add(num);
        
        Root root = new Root();
        root.employee = employee;
        root.addresses = rootAddresses;
        root.phoneNumbers = rootPhones;
        return root;
    }
    
    /**
     * The schema is generated here.  Note that schema generation will create the 
     * JAXBContext.
     *  
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();        
        employeeResolver = generateSchemaWithFileName(new Class[] { Root.class }, CONTEXT_PATH, PATH + "root-oxm.xml", 1);
    }

    public void testSchemaGenAndValidation() {
        // validate root schema
        compareSchemas(employeeResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "root.xsd"));
        // validate root.xml
        String src = PATH + "root.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, employeeResolver);
        assertTrue("Instance doc validation (root.xml) failed unxepectedly: " + result, result == null);
    }

    public void testInverseReferenceMarshal() {
        // setup control document
        String src = PATH + "root.xml";
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }

        // test marshal
        Root ctrlObj = getWriteControlObject();
        Marshaller marshaller = jaxbContext.createMarshaller();
        try {
            marshaller.marshal(ctrlObj, testDoc);
            //marshaller.marshal(ctrlObj, System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
    
    public void testInverseReferenceUnmarshal() {
        // load instance doc
        String src = PATH + "root.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // unmarshal
        Root ctrlObj = getControlObject();
        Root root = null;
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try {
            root = (Root) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", root);
            assertTrue("Unmarshal failed:  Root objects are not equal", ctrlObj.equals(root));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
}
