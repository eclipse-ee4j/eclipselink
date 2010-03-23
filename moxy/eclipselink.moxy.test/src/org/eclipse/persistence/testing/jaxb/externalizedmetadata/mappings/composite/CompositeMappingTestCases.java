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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlCompositeObjectMappings via eclipselink-oxm.xml
 * 
 */
public class CompositeMappingTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/composite/";
    private static final String HOME_CITY = "Kanata";
    private static final String HOME_STREET = "66 Lakview Drive";
    private static final String HOME_PROVINCE = "ON";
    private static final String HOME_POSTAL = "K2M2K7";
    private static final String WORK_CITY = "Ottawa";
    private static final String WORK_STREET = "45 O'Connor St.";
    private static final String WORK_PROVINCE = "ON";
    private static final String WORK_POSTAL = "K1P1A4";
    private static final String ALT_CITY = "Austin";
    private static final String PRIVATE_CITY = "Dallas";
    private static final String PRIVATE_STREET = "101 Texas Blvd.";
    private static final String PRIVATE_PROVINCE = "TX";
    private static final String PRIVATE_POSTAL = "78726";
    private static final String PHONE_1 = "613.288.0001";
    private static final String PHONE_2 = "613.288.0002";
    private static final String PRIVATE_NUMBER = "000.000.0000";
    private static final String FOO_NAME = "myfoo";
    private static final int DEPT_ID = 101;
    private static final String DEPT_NAME = "Sanitation";

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public CompositeMappingTestCases(String name) {
        super(name);
    }
    
    public Employee getControlObject() {
        Address hAddress = new Address();
        hAddress.city = HOME_CITY;
        hAddress.street = HOME_STREET;
        hAddress.province = HOME_PROVINCE;
        hAddress.postalCode = HOME_POSTAL;
        
        Address wAddress = new Address();
        wAddress.city = WORK_CITY;
        wAddress.street = WORK_STREET;
        wAddress.province = WORK_PROVINCE;
        wAddress.postalCode = WORK_POSTAL;
        
        Address aAddress = new Address();
        aAddress.city = ALT_CITY;

        Address pAddress = new Address();
        pAddress.city = WORK_CITY;
        pAddress.street = WORK_STREET;
        pAddress.province = WORK_PROVINCE;
        pAddress.postalCode = WORK_POSTAL;

        Phone pOne = new Phone();
        pOne.number = PHONE_1;
        Phone pTwo = new Phone();
        pTwo.number = PHONE_2;
        
        Department dept = new Department();
        dept.deptId = DEPT_ID;
        dept.deptName = DEPT_NAME;
        
        Foo foo = new Foo();
        foo.foodata = FOO_NAME;
        
        Phone pPhone = new Phone();
        pPhone.number = PRIVATE_NUMBER;

        Employee emp = new Employee();
        emp.homeAddress = hAddress;
        emp.workAddress = wAddress;
        emp.alternateAddress = aAddress;
        emp.phone1 = pOne;
        emp.phone2 = pTwo;
        emp.foo = foo;
        emp.privatePhone = pPhone;
        emp.department = dept;

        return emp;
    }
    
    public void testEmployeeSchemaGen() {
        try {
            // remove the following setup call when bug# 306372 is resolved
            super.setUp();

            String metadataFile = PATH + "employee-oxm.xml";
            MySchemaOutputResolver oResolver = new MySchemaOutputResolver(); 
            oResolver = generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, metadataFile, 1);
            // validate schema
            String controlSchema = PATH + "employee.xsd";
            compareSchemas(oResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        } catch (Exception e) {
            fail("An exception occurred creating the JAXBContext: " + e.getMessage());
        }
    }
    
    /**
     * Tests XmlCpompositeObjectMapping configuration via eclipselink-oxm.xml. 
     * Here an unmarshal operation is performed. Utilizes xml-attribute and 
     * xml-element.
     * 
     * Positive test.
     */
    public void testCompositeMappingUnmarshal() {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(PATH + "employee.xml");
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + PATH + "employee.xml" + "]");
        }

        // setup control Employee
        Employee ctrlEmp = getControlObject();
        // 'privatePhone' is write only, so no value should be unmarshalled for it
        ctrlEmp.privatePhone = null;

        try {
            JAXBContext jContext = createContext(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "employee-oxm.xml");
            Unmarshaller unmarshaller = jContext.createUnmarshaller();
            Employee empObj = (Employee) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Accessor method was not called as expected", empObj.wasSetCalled);
            assertTrue("Set was not called for absent node as expected", empObj.isADeptSet);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(empObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }

    /**
     * Tests XmlCompositeObjectMapping configuration via eclipselink-oxm.xml. Here a
     * marshal operation is performed. Utilizes xml-attribute and xml-element
     * 
     * Positive test.
     */
    public void testCompositeMappingMarshal() {
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
            JAXBContext jContext = createContext(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "employee-oxm.xml");
            Marshaller marshaller = jContext.createMarshaller();
            Employee ctrlEmp = getControlObject();
            marshaller.marshal(ctrlEmp, testDoc);
            marshaller.marshal(ctrlEmp, System.out);
            assertTrue("Accessor method was not called as expected", ctrlEmp.wasGetCalled);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }

    // THE FOLLOWING CODE CAN BE USED TO GENERATE DEPLOYMENT XML FOR DOCUMENTATION PURPOSES. //

    public void xtestDeploymentXML() {
        XMLProjectWriter.write(new EmployeeProject(), "employee.xml");
    }

    class EmployeeProject extends Project {
        public EmployeeProject() {
            addDescriptor(getEmployeeDescriptor());
            addDescriptor(getFooDescriptor());
        }

        XMLDescriptor getEmployeeDescriptor() {
            // setup mappings
            XMLCompositeObjectMapping fooMapping = new XMLCompositeObjectMapping();
            fooMapping.setAttributeName("foo");
            fooMapping.setXPath(".");

            XMLCompositeObjectMapping homeAddressMapping = new XMLCompositeObjectMapping();
            homeAddressMapping.setAttributeName("homeAddress");
            homeAddressMapping.setXPath("info/contact-info/home-address");

            XMLCompositeObjectMapping workAddressMapping = new XMLCompositeObjectMapping();
            workAddressMapping.setAttributeName("workAddress");
            workAddressMapping.setXPath("info/contact-info/work-address");

            XMLCompositeObjectMapping alternateAddressMapping = new XMLCompositeObjectMapping();
            alternateAddressMapping.setAttributeName("alternateAddress");
            alternateAddressMapping.setXPath("info/contact-info/alternate-address");
            alternateAddressMapping.setIsReadOnly(true);
            
            XMLCompositeObjectMapping phone1Mapping = new XMLCompositeObjectMapping();
            phone1Mapping.setAttributeName("phone1");
            phone1Mapping.setXPath("info/contact-info/phone[1]");
            
            XMLCompositeObjectMapping phone2Mapping = new XMLCompositeObjectMapping();
            phone2Mapping.setAttributeName("phone2");
            phone2Mapping.setXPath("info/contact-info/phone[2]");
            
            XMLCompositeObjectMapping privatePhoneMapping = new XMLCompositeObjectMapping();
            privatePhoneMapping.setAttributeName("privatePhone");
            privatePhoneMapping.setXPath("private-phone");
            privatePhoneMapping.setIsWriteOnly(true);
            
            XMLCompositeObjectMapping departmentMapping = new XMLCompositeObjectMapping();
            departmentMapping.setAttributeName("department");
            departmentMapping.setXPath(".");
            departmentMapping.setSetMethodName("setDepartment");
            departmentMapping.setGetMethodName("getDepartment");
            
            XMLCompositeObjectMapping aDepartmentMapping = new XMLCompositeObjectMapping();
            aDepartmentMapping.setAttributeName("aDept");
            aDepartmentMapping.setXPath("a-dept");
            aDepartmentMapping.setSetMethodName("setADept");
            aDepartmentMapping.setGetMethodName("getADept");
            NullPolicy absNullPolicy = new NullPolicy();
            absNullPolicy.setNullRepresentedByEmptyNode(false);
            absNullPolicy.setNullRepresentedByXsiNil(false);
            absNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
            aDepartmentMapping.setNullPolicy(absNullPolicy);

            // setup descriptor
            XMLDescriptor descriptor = new XMLDescriptor();
            descriptor.setJavaClass(Employee.class);
            descriptor.setDefaultRootElement("employee");
            //descriptor.setNamespaceResolver(namespaceResolver);
            descriptor.addMapping(fooMapping);
            descriptor.addMapping(homeAddressMapping);
            descriptor.addMapping(workAddressMapping);
            descriptor.addMapping(alternateAddressMapping);
            descriptor.addMapping(phone1Mapping);
            descriptor.addMapping(phone2Mapping);
            descriptor.addMapping(privatePhoneMapping);
            descriptor.addMapping(departmentMapping);
            descriptor.addMapping(aDepartmentMapping);
            return descriptor;
        }
        
        XMLDescriptor getFooDescriptor() {
            // setup mappings
            XMLDirectMapping foodataMapping = new XMLDirectMapping();
            foodataMapping.setAttributeName("foodata");
            foodataMapping.setXPath("@foodata");
            // setup descriptor
            XMLDescriptor descriptor = new XMLDescriptor();
            descriptor.setJavaClass(Foo.class);
            descriptor.setDefaultRootElement("foo");
            //descriptor.setNamespaceResolver(namespaceResolver);
            descriptor.addMapping(foodataMapping);
            return descriptor;
        }
    }
}