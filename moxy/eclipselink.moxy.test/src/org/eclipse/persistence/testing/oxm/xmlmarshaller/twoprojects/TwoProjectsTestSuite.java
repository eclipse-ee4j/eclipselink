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
package org.eclipse.persistence.testing.oxm.xmlmarshaller.twoprojects;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.oxm.platform.XMLPlatform;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;

public class TwoProjectsTestSuite extends OXTestCase {
    private static final String CONTEXT_PATH_DOM = "TwoProjects-Customer-DOM:TwoProjects-Employee-DOM";
    private static final String CONTEXT_PATH_SAX = "TwoProjects-Customer-SAX:TwoProjects-Employee-SAX";
    private static final String XML_RESOURCE_CUSTOMER = "org/eclipse/persistence/testing/oxm/xmlmarshaller/twoprojects/customer.xml";
    private static final String XML_RESOURCE_EMPLOYEE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/twoprojects/employee.xml";
    private static final String CONTROL_ADDRESS_STREET = "123 A Street";
    private static final String CONTROL_ADDRESS_CITY = "Any Town";
    private static final String CONTROL_PHONE_NUMBER_VALUE = "613-555-1111";
    private DocumentBuilder parser;
    private XMLMarshaller xmlMarshaller;
    private XMLUnmarshaller xmlUnmarshaller;

    public TwoProjectsTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.twoprojects.TwoProjectsTestSuite" });
    }

    public void setUp() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        parser = dbf.newDocumentBuilder();
    }

    public void testAddLoggedInSession() throws Exception {
        XMLContext xmlContext = new XMLContext(new CustomerProject());
        Project empProject = new EmployeeProject();
        XMLPlatform platform = new SAXPlatform();
        empProject.setLogin(new XMLLogin(platform));

        DatabaseSession employeeSession = empProject.createDatabaseSession();

        employeeSession.login();
        xmlContext.addSession(employeeSession);

        xmlMarshaller = xmlContext.createMarshaller();
        Employee employee = getControlEmployee();
        Document controlDocument = getControlDocument(XML_RESOURCE_EMPLOYEE);

        Document testDocument = parser.newDocument();
        xmlMarshaller.marshal(employee, testDocument);
        log("\nExpected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        log("");

        this.assertXMLIdentical(controlDocument, testDocument);
    }

    public void testAddLoggedOutSession() throws Exception {
        XMLContext xmlContext = new XMLContext(new CustomerProject());
        Project empProject = new EmployeeProject();

        XMLPlatform platform = new SAXPlatform();
        empProject.setLogin(new XMLLogin(platform));

        DatabaseSession employeeSession = empProject.createDatabaseSession();
        employeeSession.logout();
        xmlContext.addSession(employeeSession);

        xmlMarshaller = xmlContext.createMarshaller();
        Employee employee = getControlEmployee();
        Document controlDocument = getControlDocument(XML_RESOURCE_EMPLOYEE);

        Document testDocument = parser.newDocument();
        xmlMarshaller.marshal(employee, testDocument);
        log("\nExpected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        log("");

        this.assertXMLIdentical(controlDocument, testDocument);
    }

    public void testAddDuplicateSession() throws Exception {
        XMLContext xmlContext = new XMLContext(CONTEXT_PATH_SAX);

        Project empProject = new EmployeeProject();
        XMLPlatform platform = new SAXPlatform();
        empProject.setLogin(new XMLLogin(platform));

        DatabaseSession employeeSession = empProject.createDatabaseSession();

        xmlContext.addSession(employeeSession);

        xmlMarshaller = xmlContext.createMarshaller();
        Employee employee = getControlEmployee();
        Document controlDocument = getControlDocument(XML_RESOURCE_EMPLOYEE);

        Document testDocument = parser.newDocument();
        xmlMarshaller.marshal(employee, testDocument);
        log("\nExpected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        log("");

        this.assertXMLIdentical(controlDocument, testDocument);

        int numSessions = xmlContext.getSessions().size();
        this.assertEquals(3, numSessions);        
    }

    public void testMarshalCustomerSAX() throws Exception {
        XMLContext xmlContext = new XMLContext(CONTEXT_PATH_SAX);
        xmlMarshaller = xmlContext.createMarshaller();

        Customer customer = getControlCustomer();
        Document controlDocument = getControlDocument(XML_RESOURCE_CUSTOMER);

        Document testDocument = parser.newDocument();
        xmlMarshaller.marshal(customer, testDocument);
        log("\nExpected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        log("");

        this.assertXMLIdentical(controlDocument, testDocument);
    }

    public void testMarshalCustomerCollectionConstructor() throws Exception {
    	ArrayList projects = new ArrayList();
    	projects.add(new CustomerProject());
    	projects.add(new EmployeeProject());
        XMLContext xmlContext = new XMLContext(projects);
        xmlMarshaller = xmlContext.createMarshaller();

        Customer customer = getControlCustomer();
        Document controlDocument = getControlDocument(XML_RESOURCE_CUSTOMER);

        Document testDocument = parser.newDocument();
        xmlMarshaller.marshal(customer, testDocument);
        log("\nExpected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        log("");

        this.assertXMLIdentical(controlDocument, testDocument);
    }

    public void testUnmarshalCustomerSAX() throws Exception {
        XMLContext xmlContext = new XMLContext(CONTEXT_PATH_SAX);
        xmlUnmarshaller = xmlContext.createUnmarshaller();

        Customer controlCustomer = getControlCustomer();
        InputStream instream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_CUSTOMER);
        Customer testCustomer = (Customer)xmlUnmarshaller.unmarshal(instream);

        log("\nExpected:");
        log(controlCustomer.toString());
        log("\nActual:");
        log(testCustomer.toString());
        log("");

        this.assertEquals(controlCustomer, testCustomer);
    }

    public void testUnmarshalCustomerCollectionConstructor() throws Exception {
    	ArrayList projects = new ArrayList();
    	projects.add(new CustomerProject());
    	projects.add(new EmployeeProject());
        XMLContext xmlContext = new XMLContext(projects);
        xmlUnmarshaller = xmlContext.createUnmarshaller();

        Customer controlCustomer = getControlCustomer();
        InputStream instream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_CUSTOMER);
        Customer testCustomer = (Customer)xmlUnmarshaller.unmarshal(instream);

        log("\nExpected:");
        log(controlCustomer.toString());
        log("\nActual:");
        log(testCustomer.toString());
        log("");

        this.assertEquals(controlCustomer, testCustomer);
    }
    
    public void testMarshalEmployeeSAX() throws Exception {
        XMLContext xmlContext = new XMLContext(CONTEXT_PATH_SAX);
        xmlMarshaller = xmlContext.createMarshaller();

        Employee employee = getControlEmployee();
        Document controlDocument = getControlDocument(XML_RESOURCE_EMPLOYEE);

        Document testDocument = parser.newDocument();
        xmlMarshaller.marshal(employee, testDocument);
        log("\nExpected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        log("");

        this.assertXMLIdentical(controlDocument, testDocument);
    }
    
    public void testMarshalEmployeeCollectionConstructor() throws Exception {
    	ArrayList projects = new ArrayList();
    	projects.add(new CustomerProject());
    	projects.add(new EmployeeProject());
        XMLContext xmlContext = new XMLContext(projects);
        xmlMarshaller = xmlContext.createMarshaller();

        Employee employee = getControlEmployee();
        Document controlDocument = getControlDocument(XML_RESOURCE_EMPLOYEE);

        Document testDocument = parser.newDocument();
        xmlMarshaller.marshal(employee, testDocument);
        log("\nExpected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        log("");

        this.assertXMLIdentical(controlDocument, testDocument);
    }    

    public void testUnmarshalEmployeeSAX() throws Exception {
        XMLContext xmlContext = new XMLContext(CONTEXT_PATH_SAX);
        xmlUnmarshaller = xmlContext.createUnmarshaller();

        Employee controlEmployee = getControlEmployee();
        InputStream instream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_EMPLOYEE);
        Employee testEmployee = (Employee)xmlUnmarshaller.unmarshal(instream);

        log("\nExpected:");
        log(controlEmployee.toString());
        log("\nActual:");
        log(testEmployee.toString());
        log("");

        this.assertEquals(controlEmployee, testEmployee);
    }

    public void testUnmarshalEmployeeCollectionConstructor() throws Exception {
    	ArrayList projects = new ArrayList();
    	projects.add(new CustomerProject());
    	projects.add(new EmployeeProject());
        XMLContext xmlContext = new XMLContext(projects);

        xmlUnmarshaller = xmlContext.createUnmarshaller();

        Employee controlEmployee = getControlEmployee();
        InputStream instream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_EMPLOYEE);
        Employee testEmployee = (Employee)xmlUnmarshaller.unmarshal(instream);

        log("\nExpected:");
        log(controlEmployee.toString());
        log("\nActual:");
        log(testEmployee.toString());
        log("");

        this.assertEquals(controlEmployee, testEmployee);
    }

    public void testMarshalCustomerDOM() throws Exception {
        XMLContext xmlContext = new XMLContext(CONTEXT_PATH_DOM);
        xmlMarshaller = xmlContext.createMarshaller();

        Customer customer = getControlCustomer();
        Document controlDocument = getControlDocument(XML_RESOURCE_CUSTOMER);

        Document testDocument = parser.newDocument();
        xmlMarshaller.marshal(customer, testDocument);
        log("\nExpected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        log("");

        this.assertXMLIdentical(controlDocument, testDocument);
    }

    public void testUnmarshalCustomerDOM() throws Exception {
        XMLContext xmlContext = new XMLContext(CONTEXT_PATH_DOM);
        xmlUnmarshaller = xmlContext.createUnmarshaller();

        Customer controlCustomer = getControlCustomer();
        InputStream instream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_CUSTOMER);
        Customer testCustomer = (Customer)xmlUnmarshaller.unmarshal(instream);

        log("\nExpected:");
        log(controlCustomer.toString());
        log("\nActual:");
        log(testCustomer.toString());
        log("");

        this.assertEquals(controlCustomer, testCustomer);
    }

    public void testMarshalEmployeeDOM() throws Exception {
        XMLContext xmlContext = new XMLContext(CONTEXT_PATH_DOM);
        xmlMarshaller = xmlContext.createMarshaller();

        Employee employee = getControlEmployee();
        Document controlDocument = getControlDocument(XML_RESOURCE_EMPLOYEE);

        Document testDocument = parser.newDocument();
        xmlMarshaller.marshal(employee, testDocument);
        log("\nExpected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        log("");

        this.assertXMLIdentical(controlDocument, testDocument);
    }

    public void testUnmarshalEmployeeDOM() throws Exception {
        XMLContext xmlContext = new XMLContext(CONTEXT_PATH_DOM);
        xmlUnmarshaller = xmlContext.createUnmarshaller();

        Employee controlEmployee = getControlEmployee();
        InputStream instream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE_EMPLOYEE);
        Employee testEmployee = (Employee)xmlUnmarshaller.unmarshal(instream);

        log("\nExpected:");
        log(controlEmployee.toString());
        log("\nActual:");
        log(testEmployee.toString());
        log("");

        this.assertEquals(controlEmployee, testEmployee);
    }

    private Document getControlDocument(String resourceName) throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        return parser.parse(instream);
    }

    private Customer getControlCustomer() {
        Customer customer = new Customer();
        Address address = new Address();
        address.setStreet(CONTROL_ADDRESS_STREET);
        address.setCity(CONTROL_ADDRESS_CITY);
        customer.setAddress(address);
        customer.getAddresses().add(address);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setValue(CONTROL_PHONE_NUMBER_VALUE);
        customer.setAny(phoneNumber);
        customer.getAnyCollection().add(phoneNumber);
        return customer;
    }

    private Employee getControlEmployee() {
        Employee employee = new Employee();
        Address address = new Address();
        address.setStreet(CONTROL_ADDRESS_STREET);
        address.setCity(CONTROL_ADDRESS_CITY);
        employee.setAddress(address);
        employee.getAddresses().add(address);
        return employee;
    }
}
