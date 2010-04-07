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
 * dmccann - January 28/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
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
    private static final double SALARY = 123456.78;
    private static final String CHARACTER_DATA = "<characters>a b c d e f g</characters>";
    private static final String PRIVATE_DATA = "This is some private data";
    private static final String EMPLOYEES_NS = "http://www.example.com/employees";
    private static final String PROJECTS_NS = "http://www.example.com/projects";
    private MySchemaOutputResolver employeeResolver;
    private static final String CURRENCY = "CAD";
    private static final double PRICE = 123.456;

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
     * This method's primary purpose id to generate schema(s). Validation of
     * generated schemas will occur in the testXXXGen method(s) below. Note that
     * the JAXBContext is created from this call and is required for
     * marshal/unmarshal, etc. tests.
     * 
     */
    public void setUp() throws Exception {
        super.setUp();
        employeeResolver = generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "eclipselink-oxm.xml", 2);
    }

    /**
     * Tests schema generation for XmlDirectMapping via eclipselink-oxm.xml.
     * Utilizes xml-attribute and xml-element. xml-value is tested separately
     * below.
     * 
     * Positive test.
     */
    public void testDirectMappingSchemaGen() {
        // validate schemas
        String controlSchema = PATH + "employees.xsd";
        compareSchemas(employeeResolver.schemaFiles.get(EMPLOYEES_NS), new File(controlSchema));
    }

    /**
     * Tests XmlDirectMapping configuration via eclipselink-oxm.xml. Here an
     * unmarshal operation is performed. Utilizes xml-attribute and xml-element.
     * xml-value is tested separately below.
     * 
     * Positive test.
     */
    public void testDirectMappingUnmarshal() {
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

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
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
        Marshaller marshaller = jaxbContext.createMarshaller();
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
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { Price.class }, CONTEXT_PATH, PATH + "eclipselink-oxm-xml-value.xml");
        } catch (JAXBException e1) {
            e1.printStackTrace();
            fail("JAXBContext creation failed");
        }
        // load instance doc
        String src = PATH + "price.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }

        // setup control Price
        Price ctrlPrice = getControlPriceObject();

        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            Price priceObj = (Price) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", priceObj);
            assertTrue("Unmarshal failed:  Price objects are not equal", ctrlPrice.equals(priceObj));
            assertTrue("Accessor method was not called as expected", priceObj.wasSetCalled);
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
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { Price.class }, CONTEXT_PATH, PATH + "eclipselink-oxm-xml-value.xml");
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("JAXBContext creation failed");
        }

        // load instance doc
        String src = PATH + "price-write.xml";

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
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
    
    public void testVehiclesSchemaGen() {
        try {
            String metadataFile = PATH + "vehicles-oxm.xml";
            MySchemaOutputResolver oResolver = new MySchemaOutputResolver(); 
            oResolver = generateSchemaWithFileName(new Class[] { Car.class, Truck.class }, CONTEXT_PATH, metadataFile, 2);
            // validate schema
            String controlSchema = PATH + "vehicles.xsd";
            compareSchemas(oResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        } catch (Exception e) {
            fail("An exception occurred creating the JAXBContext: " + e.getMessage());
        }
    }

    public void testTeamSchemaGen() {
        try {
            String metadataFile = PATH + "team-oxm.xml";
            MySchemaOutputResolver oResolver = new MySchemaOutputResolver(); 
            oResolver = generateSchemaWithFileName(new Class[] { Team.class }, CONTEXT_PATH, metadataFile, 3);
            // validate schema
            String controlSchema = PATH + "team.xsd";
            compareSchemas(oResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        } catch (Exception e) {
            fail("An exception occurred creating the JAXBContext: " + e.getMessage());
        }
    }

    // THE FOLLOWING CODE CAN BE USED TO GENERATE DEPLOYMENT XML FOR DOCUMENTATION PURPOSES. //

    public void xtestDeploymentXML() {
        XMLProjectWriter.write(new EmployeeProject(), "employee.xml");
        XMLProjectWriter.write(new PriceProject(), "price.xml");
    }

    class EmployeeProject extends Project {
        public EmployeeProject() {
            addDescriptor(getEmployeeDescriptor());
        }

        XMLDescriptor getEmployeeDescriptor() {
            // setup direct mappings
            XMLDirectMapping idMapping = new XMLDirectMapping();
            idMapping.setAttributeName("empId");
            idMapping.setXPath("@id");

            XMLDirectMapping firstNameMapping = new XMLDirectMapping();
            firstNameMapping.setAttributeName("firstName");
            firstNameMapping.setXPath("info/personal-info/first-name/text()");

            XMLDirectMapping lastNameMapping = new XMLDirectMapping();
            lastNameMapping.setAttributeName("lastName");
            lastNameMapping.setXPath("info/personal-info/last-name/text()");

            XMLDirectMapping mgrIdMapping = new XMLDirectMapping();
            mgrIdMapping.setAttributeName("mgrId");
            mgrIdMapping.setXPath("projects/prj:project/@prj:managerId");

            XMLDirectMapping projectNameMapping = new XMLDirectMapping();
            projectNameMapping.setAttributeName("projectName");
            projectNameMapping.setXPath("projects/prj:project/text()");
            projectNameMapping.setSetMethodName("setProject");
            projectNameMapping.setGetMethodName("getProject");

            XMLDirectMapping data1Mapping = new XMLDirectMapping();
            data1Mapping.setAttributeName("data1");
            data1Mapping.setXPath("pieces-of-data/data[1]");

            XMLDirectMapping data2Mapping = new XMLDirectMapping();
            data2Mapping.setAttributeName("data2");
            data2Mapping.setXPath("pieces-of-data/data[2]");

            XMLDirectMapping salaryMapping = new XMLDirectMapping();
            salaryMapping.setAttributeName("salary");
            salaryMapping.setXPath("@salary");
            salaryMapping.setIsReadOnly(true);

            XMLDirectMapping privateDataMapping = new XMLDirectMapping();
            privateDataMapping.setAttributeName("privateData");
            privateDataMapping.setXPath("private-data");
            privateDataMapping.setIsWriteOnly(true);

            XMLDirectMapping charDataMapping = new XMLDirectMapping();
            charDataMapping.setAttributeName("characterData");
            charDataMapping.setXPath("character-data");
            charDataMapping.setIsCDATA(true);

            XMLDirectMapping projectIdMapping = new XMLDirectMapping();
            projectIdMapping.setAttributeName("projectId");
            projectIdMapping.setXPath("project-id");
            projectIdMapping.setNullValue(-1);

            XMLDirectMapping someStringMapping = new XMLDirectMapping();
            someStringMapping.setAttributeName("someString");
            someStringMapping.setXPath("some-string");
            someStringMapping.setGetMethodName("getSomeString");
            someStringMapping.setSetMethodName("setSomeString");
            IsSetNullPolicy aNullPolicy = new IsSetNullPolicy();
            aNullPolicy.setNullRepresentedByEmptyNode(false);
            aNullPolicy.setNullRepresentedByXsiNil(true);
            aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
            aNullPolicy.setIsSetMethodName("isSetSomeString");
            aNullPolicy.setIsSetParameterTypes(new Class[] { java.lang.Boolean.class });
            aNullPolicy.setIsSetParameters(new Object[] { "true" });
            someStringMapping.setNullPolicy(aNullPolicy);

            XMLDirectMapping aStringMapping = new XMLDirectMapping();
            aStringMapping.setAttributeName("aString");
            aStringMapping.setXPath("a-string");
            aStringMapping.setGetMethodName("getAString");
            aStringMapping.setSetMethodName("setAString");
            NullPolicy absNullPolicy = new NullPolicy();
            absNullPolicy.setNullRepresentedByEmptyNode(false);
            absNullPolicy.setNullRepresentedByXsiNil(false);
            absNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
            someStringMapping.setNullPolicy(absNullPolicy);

            // setup namespace resolver
            NamespaceResolver namespaceResolver = new NamespaceResolver();
            namespaceResolver.setDefaultNamespaceURI("http://www.example.com/employees");
            namespaceResolver.put("prj", "http://www.example.com/projects");

            // setup descriptor
            XMLDescriptor descriptor = new XMLDescriptor();
            descriptor.setJavaClass(Employee.class);
            descriptor.setDefaultRootElement("employee");
            descriptor.setNamespaceResolver(namespaceResolver);
            descriptor.addMapping(idMapping);
            descriptor.addMapping(firstNameMapping);
            descriptor.addMapping(lastNameMapping);
            descriptor.addMapping(mgrIdMapping);
            descriptor.addMapping(projectNameMapping);
            descriptor.addMapping(data1Mapping);
            descriptor.addMapping(data2Mapping);
            descriptor.addMapping(salaryMapping);
            descriptor.addMapping(privateDataMapping);
            descriptor.addMapping(charDataMapping);
            descriptor.addMapping(projectIdMapping);
            descriptor.addMapping(someStringMapping);
            descriptor.addMapping(aStringMapping);

            return descriptor;
        }
    }
    
    class PriceProject extends Project {
        public PriceProject() {
            addDescriptor(getPriceDescriptor());
        }

        XMLDescriptor getPriceDescriptor() {
            // setup direct mappings
            XMLDirectMapping priceMapping = new XMLDirectMapping();
            priceMapping.setAttributeName("price");
            priceMapping.setXPath("price-data");
            priceMapping.setIsReadOnly(true);
            priceMapping.setSetMethodName("setPrice");

            XMLDirectMapping currencyMapping = new XMLDirectMapping();
            currencyMapping.setAttributeName("currency");
            currencyMapping.setXPath("@currency");

            // setup descriptor
            XMLDescriptor descriptor = new XMLDescriptor();
            descriptor.setJavaClass(Price.class);
            descriptor.setDefaultRootElement("price-data");
            descriptor.addMapping(priceMapping);
            descriptor.addMapping(currencyMapping);
            return descriptor;
        }
    }
}