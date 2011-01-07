/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - January 28/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlDirectMappings via eclipselink-oxm.xml
 * 
 */
public class DirectMappingTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/direct/";
    private static final String FNAME = "Joe";
    private static final String LNAME = "Oracle";
    private static final String PNAME = "XML External Metadata Support";
    private static final String DATA1 = "data one";
    private static final String DATA2 = "data two";
    private static final int EMPID = 66;
    private static final int MGRID = 99;
    private static final int PROJECT_ID = 999;
    private static final Double SALARY = 123456.78;
    private static final String CHARACTER_DATA = "<characters>a b c d e f g</characters>";
    private static final String PRIVATE_DATA = "This is some private data";
    private static final String EMPLOYEES_NS = "http://www.example.com/employees";
    private static final String PROJECTS_NS = "http://www.example.com/projects";
    private static final String CURRENCY = "CAD";
    private static final double PRICE = 123.456;
    private static final int PROPCOUNT = 3;
    private static final String PROPKEY_1 = "1";
    private static final String PROPKEY_2 = "2";
    private static final String PROPKEY_3 = "3";
    private static final String PROPVAL_1 = "A";
    private static final int PROPVAL_2 = 66;
    private static final boolean PROPVAL_3 = true;
    
    private Class[] employeeArray;
    private Class[] priceArray;
    private JAXBContext empCtx;
    private JAXBContext priceCtx;
    private XMLDescriptor employeeDesc;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public DirectMappingTestCases(String name) {
        super(name);
    }

    private Employee getControlObject() {
        Employee ctrlEmp = new Employee();
        ctrlEmp.firstName = FNAME;
        ctrlEmp.lastName = LNAME;
        ctrlEmp.empId = EMPID;
        ctrlEmp.mgrId = MGRID;
        ctrlEmp.setProject(PNAME);
        ctrlEmp.data1 = DATA1;
        ctrlEmp.data2 = DATA2;
        ctrlEmp.salary = SALARY;
        ctrlEmp.privateData = PRIVATE_DATA;
        ctrlEmp.characterData = CHARACTER_DATA;
        ctrlEmp.projectId = PROJECT_ID;
        return ctrlEmp;
    }

    private Price getControlPriceObject() {
        Price ctrlPrice = new Price();
        ctrlPrice.currency = CURRENCY;
        ctrlPrice.price = new BigDecimal(PRICE);
        return ctrlPrice;
    }

    /**
     * Lazy load the employee class array.
     * 
     * @return
     */
    private Class[] getEmployeeArray() {
        if (employeeArray == null) {
            employeeArray = new Class[] { Employee.class };
        }
        return employeeArray;
    }
    
    /**
     * Lazy load the price class array.
     * 
     * @return
     */
    private Class[] getPriceArray() {
        if (priceArray == null) {
            priceArray = new Class[] { Price.class };
        }
        return priceArray;
    }
    
    /**
     * Lazy load the JAXBContext for the employee class.  Uses the 
     * eclipselink-oxm.xml metadata file.
     * 
     * @return
     */
    private JAXBContext getJAXBContextForEmployee() {
        if (empCtx == null) {
            try {
                empCtx = createContext(getEmployeeArray(), CONTEXT_PATH, PATH + "eclipselink-oxm.xml");
            } catch (JAXBException e1) {
                e1.printStackTrace();
            }
            assertNotNull("JAXBContext creation for Employee failed", empCtx);
        }
        return empCtx;
    }

    /**
     * Lazy load the JAXBContext for the price class.  Uses the 
     * eclipselink-oxm-xml-value.xml metadata file.
     * 
     * @return
     */
    private JAXBContext getJAXBContextForPrice() {
        if (priceCtx == null) {
            try {
                priceCtx = createContext(getPriceArray(), CONTEXT_PATH, PATH + "eclipselink-oxm-xml-value.xml");
            } catch (JAXBException e1) {
                e1.printStackTrace();
            }
            assertNotNull("JAXBContext creation for Price failed", priceCtx);
        }
        return priceCtx;
    }

    private XMLDescriptor getDescriptorForEmployee() {
        if (employeeDesc == null) {
            try {
                employeeDesc = getJAXBContextForEmployee().getXMLContext().getDescriptor(new QName(EMPLOYEES_NS, "employee"));
            } catch (Exception x) {
                x.printStackTrace();
            }
            assertNotNull("Employee descriptor is null", employeeDesc);
        }
        return employeeDesc;
    }
    
    /**
     * Tests schema generation for XmlDirectMapping via eclipselink-oxm.xml.
     * Utilizes xml-attribute and xml-element. xml-value is tested separately
     * below.  Instance doc validation is done here as well.
     * 
     * Positive test.
     */
    public void testSchemaGenAndValidation() {
        // generate employee and project schemas
        MySchemaOutputResolver resolver = generateSchemaWithFileName(getEmployeeArray(), CONTEXT_PATH, PATH + "eclipselink-oxm.xml", 2);
        // validate employees schema
        String controlSchema = PATH + "employees.xsd";
        compareSchemas(resolver.schemaFiles.get(EMPLOYEES_NS), new File(controlSchema));
        // validate projects schema
        controlSchema = PATH + "projects.xsd";
        compareSchemas(resolver.schemaFiles.get(PROJECTS_NS), new File(controlSchema));
        // validate employee.xml
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPLOYEES_NS, resolver);
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
        // validate write-employee.xml
        src = PATH + "write-employee.xml";
        result = validateAgainstSchema(src, EMPLOYEES_NS, resolver);
        assertTrue("Instance doc validation (write-employee.xml) failed unxepectedly: " + result, result == null);

        // generate vehicles schema
        resolver = generateSchemaWithFileName(new Class[] { Car.class, Truck.class }, CONTEXT_PATH, PATH + "vehicles-oxm.xml", 2);
        // validate vehicles schema
        controlSchema = PATH + "vehicles.xsd";
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));

        // generate team schema
        resolver = generateSchemaWithFileName(new Class[] { Team.class }, CONTEXT_PATH, PATH + "team-oxm.xml", 3);
        // validate vehicles schema
        controlSchema = PATH + "team.xsd";
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));

        // generate price schema
        resolver = generateSchemaWithFileName(getPriceArray(), CONTEXT_PATH, PATH + "eclipselink-oxm-xml-value.xml", 1);
        // validate price schema
        controlSchema = PATH + "price.xsd";
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        // validate price.xml
        src = PATH + "price.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (price.xml) failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests XmlDirectMapping configuration via eclipselink-oxm.xml. Here an
     * unmarshal operation is performed. Utilizes xml-attribute and xml-element.
     * xml-value is tested separately below.
     * 
     * Positive test.
     */
    public void testDirectMappingUnmarshal() {
        // create the JAXBContext
        JAXBContext jCtx = getJAXBContextForEmployee();

        // load instance doc
        String src = PATH + "employee.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // setup control Employee
        Employee ctrlEmp = getControlObject();
        // 'privateData' is write only
        ctrlEmp.privateData = null;
        // JAXB will default a null String to "" 
        ctrlEmp.someString = "";

        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            Employee empObj = (Employee) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(empObj));
            assertTrue("Accessor method was not called as expected", empObj.wasSetCalled);
            assertTrue("Set was not called for absent node as expected", empObj.isAStringSet);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }

    /**
     * Tests XmlDirectMapping configuration via eclipselink-oxm.xml. Here a
     * marshal operation is performed. Utilizes xml-attribute and xml-element.
     * xml-value is tested separately below.
     * 
     * Positive test.
     */
    public void testDirectMappingMarshal() {
        // create the JAXBContext
        JAXBContext jCtx = getJAXBContextForEmployee();

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
        Marshaller marshaller = jCtx.createMarshaller();
        try {
            Employee ctrlEmp = getControlObject();
            ctrlEmp.setSomeString(null);
            marshaller.marshal(ctrlEmp, testDoc);
            //marshaller.marshal(ctrlEmp, System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
            assertTrue("Accessor method was not called as expected", ctrlEmp.wasGetCalled);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }

    /**
     * Tests XmlDirectMapping configuration via eclipselink-oxm.xml. Here an
     * unmarshal operation is performed. Utilizes xml-value. xml-attribute and
     * xml-element are tested above.
     * 
     * Positive test.
     */
    public void testDirectMappingXmlValueUnmarshal() {
        // create the JAXBContext
        JAXBContext jCtx = getJAXBContextForPrice();

        // load instance doc
        String src = PATH + "price.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // setup control Price
        Price ctrlPrice = getControlPriceObject();
        // write-only so nothing should be set for price
        ctrlPrice.price = null;

        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            Price priceObj = (Price) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", priceObj);
            assertTrue("Unmarshal failed:  Price objects are not equal", ctrlPrice.equals(priceObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }

    /**
     * Tests XmlDirectMapping configuration via eclipselink-oxm.xml. Here a
     * marshal operation is performed. Utilizes xml-value. xml-attribute and
     * xml-element are tested above.
     * 
     * Positive test.
     */
    public void testDirectMappingXmlValueMarshal() {
        // create the JAXBContext
        JAXBContext jCtx = getJAXBContextForPrice();

        // load instance doc
        String src = PATH + "price.xml";

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
        Marshaller marshaller = jCtx.createMarshaller();
        try {
            Price ctrlPrice = getControlPriceObject();
            marshaller.marshal(ctrlPrice, testDoc);
            //marshaller.marshal(ctrlPrice, System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
            assertTrue("Accessor method was not called as expected", ctrlPrice.wasGetCalled);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }

    /**
     * Test setting property-level properties via xml-element.
     * Tests setting a String, Integer and Boolean.
     * 
     * Positive test.
     */
    public void testMappingProperties() {
        // get the Employee descriptor
        XMLDescriptor xdesc = getDescriptorForEmployee();
        DatabaseMapping projectNameMapping = xdesc.getMappingForAttributeName("projectName");
        assertNotNull("No user-defined properties exist on the mapping for [projectName]", projectNameMapping.getProperties());
        validateProperties(projectNameMapping.getProperties());
    }
    
    /**
     * Test setting descriptor-level properties via XML metadata.
     * Tests setting a String, Integer and Boolean.
     * 
     * Positive test.
     */
    public void testDescriptorProperties() {
        // create the JAXBContext
        JAXBContext jCtx = getJAXBContextForEmployee();
        assertNotNull("JAXBContext creation failed", jCtx);
        XMLDescriptor xdesc = jCtx.getXMLContext().getDescriptor(new QName(EMPLOYEES_NS, "employee"));
        assertNotNull("Employee descriptor is null", xdesc);
        assertNotNull("No user-defined properties exist on the descriptor for Employee", xdesc.getProperties());
        validateProperties(xdesc.getProperties());
    }
    
    /**
     * Validates user-defined properties set via xml metadata.
     * 
     * @param props
     */
    private void validateProperties(Map props) {
        assertTrue("Expected [" + PROPCOUNT + "] user-defined properties, but there were [" + props.size() + "]", props.size() == PROPCOUNT);
        // verify entries exist for each key
        assertNotNull("No property found for key [" + PROPKEY_1 + "]", props.get(PROPKEY_1));
        assertNotNull("No property found for key [" + PROPKEY_2 + "]", props.get(PROPKEY_2));
        assertNotNull("No property found for key [" + PROPKEY_3 + "]", props.get(PROPKEY_3));
        // verify value-types
        assertTrue("Expected value-type [String] for key [" + PROPKEY_1 + "] but was [" + props.get(PROPKEY_1).getClass().getName() + "]", props.get(PROPKEY_1) instanceof String);
        assertTrue("Expected value-type [Integer] for key [" + PROPKEY_2 + "] but was [" + props.get(PROPKEY_2).getClass().getName() + "]", props.get(PROPKEY_2) instanceof Integer);
        assertTrue("Expected value-type [Boolean] for key [" + PROPKEY_3 + "] but was [" + props.get(PROPKEY_3).getClass().getName() + "]", props.get(PROPKEY_3) instanceof Boolean);
        // verify values
        assertTrue("Expected property value [" + PROPVAL_1 + "] for key [" + PROPKEY_1 + "] but was [" + props.get(PROPKEY_1) + "]", PROPVAL_1.equals(props.get(PROPKEY_1)));
        assertTrue("Expected property value [" + PROPVAL_2 + "] for key [" + PROPKEY_2 + "] but was [" + props.get(PROPKEY_2) + "]", PROPVAL_2 == (Integer) props.get(PROPKEY_2));
        assertTrue("Expected property value [" + PROPVAL_3 + "] for key [" + PROPKEY_3 + "] but was [" + props.get(PROPKEY_3) + "]", PROPVAL_3 == (Boolean) props.get(PROPKEY_3));
    }
}