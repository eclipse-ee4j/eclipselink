/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - April 01/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference.Address;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference.Client;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference.PhoneNumber;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference.Root;

/**
 * Tests XmlChoiceObjectMappings via eclipselink-oxm.xml
 * 
 */
public class ChoiceMappingTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/";
    private static final String INT_VAL = "66";
    private static final String FLT_VAL = "66.66";
    
    private MySchemaOutputResolver resolver;
    private Root ctrlRoot;
    private JAXBContext joinNodeContext;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public ChoiceMappingTestCases(String name) {
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
        resolver = generateSchemaWithFileName(new Class[] { }, CONTEXT_PATH, PATH + "employee-oxm.xml", 1);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new File(PATH + "reference/root-oxm.xml"));
        joinNodeContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference.Root.class }, props);
    }


    /**
     * Return the control Employee.
     * 
     * @return
     */
    public Employee getControlObject() {
        Employee emp = new Employee();
        emp.thing = new Integer(INT_VAL);
        emp.readOnlyThing = new Float(FLT_VAL);
        emp.writeOnlyThing = new Integer(INT_VAL);
        return emp;
    }
    
    public Root getControlRoot() {
        if (ctrlRoot == null) {
            Address address1 = new Address("a100", "123 Some Street", "shipping");
            Address address2 = new Address("a101", "66 Dead End Rd.", "home");
            Address address3 = new Address("a101", "45 O'Connor St.", "work");
            Address address4 = new Address("a101", "101 Metcalfe St.", "billing");
            Address address5 = new Address("a102", "61 McClintock Way", "home");
            
            PhoneNumber phone1 = new PhoneNumber("p100", "613.288.6789", "work");
            PhoneNumber phone2 = new PhoneNumber("p100", "613.858.6789", "cell");
            PhoneNumber phone3 = new PhoneNumber("p101", "613.288.0000", "home");
            PhoneNumber phone4 = new PhoneNumber("p101", "613.420.1212", "work");
    
            Client client1 = new Client("c100", address2);
            Client client2 = new Client("c200", phone2);
            
            List<Address> addressList = new ArrayList<Address>();
            addressList.add(address1);
            addressList.add(address2);
            addressList.add(address3);
            addressList.add(address4);
            addressList.add(address5);
            
            List<PhoneNumber> phoneList = new ArrayList<PhoneNumber>();
            phoneList.add(phone1);
            phoneList.add(phone2);
            phoneList.add(phone3);
            phoneList.add(phone4);
            
            List<Client> clients = new ArrayList<Client>();
            clients.add(client1);
            clients.add(client2);
            
            ctrlRoot = new Root(clients, addressList, phoneList);
        }
        return ctrlRoot;
    }
    
    public void testEmployeeSchemaGen() {
        // validate the schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "employee.xsd"));
    }

    public void testInstanceDocValidation() {
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
        src = PATH + "write-employee.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (write-employee) failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests XmlChoiceMapping configuration via eclipselink-oxm.xml. 
     * Here an unmarshal operation is performed. Utilizes xml-attribute and 
     * xml-element.
     * 
     * Positive test.
     */
    public void testChoiceMappingUnmarshal() {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(PATH + "employee.xml");
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + PATH + "employee.xml" + "]");
        }

        // setup control Employee
        Employee ctrlEmp = getControlObject();
        // writeOnlyThing should not be read in
        ctrlEmp.writeOnlyThing = null;
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Employee empObj = (Employee) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Accessor method was not called as expected", empObj.wasSetCalled);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(empObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }

    /**
     * Tests XmlChoiceMapping configuration via eclipselink-oxm.xml. Here a
     * marshal operation is performed. Utilizes xml-attribute and xml-element
     * 
     * Positive test.
     */
    public void testChoiceMappingMarshal() {
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

        // test marshal
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

    public void testChoiceWithJoinNodesMarshal() {
        try {
            // load instance doc
            String src = PATH + "reference/root.xml";

            // setup control document
            Document testDoc = parser.newDocument();
            Document ctrlDoc = parser.newDocument();
            try {
                ctrlDoc = getControlDocument(src);
            } catch (Exception e) {
                e.printStackTrace();
                fail("An unexpected exception occurred loading control document [" + src + "].");
            }

            joinNodeContext.createMarshaller().marshal(getControlRoot(), testDoc);
            //joinNodeContext.createMarshaller().marshal(getControlRoot(), System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e1) {
            e1.printStackTrace();
            fail("Marshal operation failed: " + e1.getMessage());
        }
    }

    public void testChoiceWithJoinNodesUnmarshal() {
        try {
            Root unmarshalledRoot = (Root) joinNodeContext.createUnmarshaller().unmarshal(new File(PATH + "reference/root.xml"));
            assertEquals(unmarshalledRoot, getControlRoot());
        } catch (JAXBException e1) {
            e1.printStackTrace();
            fail("Unmarshal operation failed: " + e1.getMessage());
        }
    }

    /**
     * Tests exception handling for wrong number of XmlJoinNodes in an XnlElements.
     * 
     * Expects:
     * 
     * "Exception Description: Property [preferredContactMethod] on class [Client] 
     * has an XmlElements declaration containing an unequal amount of XmlElement/XmlJoinNodes.  
     * It is required that there be a corresponding XmlJoinNodes for each XmlElement 
     * contained within the XmlElements declaration."
     * 
     * Negative test.
     */
    public void testIncorrectNumberOfXmlJoinNodes() {
        try {
            Map<String, Object> props = new HashMap<String, Object>();
            props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new File(PATH + "reference/root-invalid-oxm.xml"));
            JAXBContext ctx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference.Root.class }, props);
        } catch (JAXBException e1) {
            return;
        }
        fail("The expected exception was never thrown.");
    }
}