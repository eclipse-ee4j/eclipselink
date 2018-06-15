/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3 - initial implementation
//     Praba Vijayaratnam - 2.3 - test automation
//     Praba Vijayaratnam - 2.4 - added JSON support testing
package org.eclipse.persistence.testing.jaxrs;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.jaxrs.model.Address;
import org.eclipse.persistence.testing.jaxrs.model.Customer;
import org.eclipse.persistence.testing.jaxrs.model.Customers;
import org.eclipse.persistence.testing.jaxrs.model.PhoneNumber;
import org.eclipse.persistence.testing.jaxrs.utils.JAXRSPopulator;
import org.eclipse.persistence.testing.jaxrs.utils.JAXRSTableCreator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DefaultJAXBContextTestCases extends JUnitTestCase {

    private static final String JAXRS_APP_SERVER = "server.url";
    private static final String JAXRS_APP_SERVER_DEFAULT = "localhost";
    private static final String JAXRS_APP_PORT = "server.port";
    private static final String JAXRS_APP_PORT_DEFAULT = "7001";

    private JAXBContext jc;
    protected DatabaseSession session;

    public DefaultJAXBContextTestCases(String name) throws Exception {
        super(name);
        jc = JAXBContext.newInstance(Customer.class, Customers.class);

    }

    public JAXRSPopulator setup() {
        session = JUnitTestCase.getServerSession();
        // new JAXRSTableCreator().replaceTables(session);
        JAXRSTableCreator tableCreator = new JAXRSTableCreator();
        tableCreator.dropTableConstraints(session);
        tableCreator.replaceTables(session);
        JAXRSPopulator jaxrsPopulator = new JAXRSPopulator();
        return jaxrsPopulator;
    }

    public void cleanTables(JAXRSPopulator populator) {
        populator.removeRegisteredObjects();
    }

    /* READ operation : reads in pre-populated Customer 4 */
    public void testGetCustomer() throws Exception {
        JAXRSPopulator jaxrsPopulator = setup();
        jaxrsPopulator.buildExamplesCustomer4();
        jaxrsPopulator.persistExample(session);

        URL url = new URL(getURL() + "/" + "4");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");

        InputStream xml = connection.getInputStream();
        Customer testObject = (Customer) getJAXBContext().createUnmarshaller()
                .unmarshal(xml);
        int response = connection.getResponseCode();
        connection.disconnect();

        assertTrue((response < 300) && (response >= 200));
        assertEquals(getControlObject4(), testObject);
    }

    /* JSON - READ operation : reads in pre-populated Customer 5 */
    public void testGetCustomerJSON() throws Exception {
        JAXRSPopulator jaxrsPopulator = setup();
        jaxrsPopulator.buildExamplesCustomer5();
        jaxrsPopulator.persistExample(session);

        URL url = new URL(getURL() + "/" + "5");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        InputStream inputStream = connection.getInputStream();
        StreamSource json = new StreamSource(inputStream);
        Unmarshaller u = getJAXBContext().createUnmarshaller();
        u.setProperty("eclipselink.media-type", "application/json");
        u.setProperty("eclipselink.json.include-root", false);
        Customer testObject = u.unmarshal(json, Customer.class).getValue();
        int response = connection.getResponseCode();
        connection.disconnect();

        assertTrue((response < 300) && (response >= 200));
        assertEquals(getControlObject5(), testObject);
    }

    /*
     * READ operation: CollectionOfObjects - reads in pre-populated Customers 1
     * & 3
     */
    // PASSED - by itself + setup();
    public void testGetCollectionOfObjects() throws Exception {
        JAXRSPopulator jaxrsPopulator = setup();
        jaxrsPopulator.buildExamplesCustomer1and3and10();
        jaxrsPopulator.persistExample(session);

        URL url = new URL(getURL() + "/" + "findCustomerByCity" + "/"
                + "Ottawa");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");

        InputStream xml = connection.getInputStream();
        Customers testObject = (Customers) getJAXBContext()
                .createUnmarshaller().unmarshal(xml);
        int response = connection.getResponseCode();
        connection.disconnect();

        assertTrue((response < 300) && (response >= 200));
        assertEquals(getControlObjects(), testObject);
    }

    /*
     * JSON - READ operation: CollectionOfObjects - reads in pre-populated
     * Customers 1 & 3
     */
    // PASSED - by itself + setup();
    public void testGetCollectionOfObjectsJSON() throws Exception {
        JAXRSPopulator jaxrsPopulator = setup();
        jaxrsPopulator.buildExamplesCustomer13and14and15();
        jaxrsPopulator.persistExample(session);

        URL url = new URL(getURL() + "/" + "findCustomerByCity" + "/"
                + "Montreal");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        InputStream inputStream = connection.getInputStream();
        StreamSource json = new StreamSource(inputStream);
        Unmarshaller u = getJAXBContext().createUnmarshaller();
        u.setProperty("eclipselink.media-type", "application/json");
        u.setProperty("eclipselink.json.include-root", false);
        List<Customer> customers = (List<Customer>) u.unmarshal(json, Customer.class).getValue();
        int response = connection.getResponseCode();
        connection.disconnect();

        Customers testObject = new Customers();
        testObject.setCustomer(customers);

        assertTrue((response < 300) && (response >= 200));
        assertEquals(getControlObjectsJSON(), testObject);
    }

    /* DELETE operation - deletes Customer 4 */
    public void testDeleteCustomer() throws Exception {
        JAXRSPopulator jaxrsPopulator = setup();
        jaxrsPopulator.buildExamplesCustomer10();
        jaxrsPopulator.persistExample(session);

        URL url = new URL(getURL() + "/" + 10);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        int response = connection.getResponseCode();
        connection.disconnect();

        assertEquals(204, response);
    }

    /* INSERT operation : inserts Customer 6 */
    public void testPostInsertCustomer() throws Exception {
        JAXRSPopulator jaxrsPopulator = setup();
        jaxrsPopulator.buildEmptyTables();
        jaxrsPopulator.persistExample(session);

        URL url = new URL(getURL());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/xml");

        OutputStream os = connection.getOutputStream();
        getJAXBContext().createMarshaller().marshal(getControlObject6(), os);
        os.flush();
        int response = connection.getResponseCode();
        Customer testObject = verifyHelperForPostPut(6);
        connection.disconnect();

        assertTrue((response < 300) && (response >= 200));
        assertEquals(getControlObject6(), testObject);
    }

    /* JSON - INSERT operation : inserts Customer 7 */
    public void testPostInsertCustomerJSON() throws Exception {
        JAXRSPopulator jaxrsPopulator = setup();
        jaxrsPopulator.buildEmptyTables();
        jaxrsPopulator.persistExample(session);

        URL url = new URL(getURL());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        OutputStream os = connection.getOutputStream();
        Marshaller m = getJAXBContext().createMarshaller();
        m.setProperty("eclipselink.media-type", "application/json");
        m.setProperty("eclipselink.json.include-root", false);
        m.marshal(getControlObject7(), os);
        os.flush();
        int response = connection.getResponseCode();
        Customer testObject = verifyHelperForPostPutJSON(7);
        connection.disconnect();

        assertTrue((response < 300) && (response >= 200));
        assertEquals(getControlObject7(), testObject);
    }

    /* UPDATE operation : updates Customer 8 */
    public void testPutUpdateCustomer() throws Exception {
        JAXRSPopulator jaxrsPopulator = setup();
        jaxrsPopulator.buildExamplesCustomer8();
        jaxrsPopulator.persistExample(session);

        URL url = new URL(getURL());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/xml");

        OutputStream os = connection.getOutputStream();

        getJAXBContext().createMarshaller().marshal(getControlObject8(), os);
        os.flush();
        int response = connection.getResponseCode();
        Customer testObject = verifyHelperForPostPut(8);
        connection.disconnect();

        assertTrue((response < 300) && (response >= 200));
        assertEquals(getControlObject8(), testObject);
    }

    /* JSON - UPDATE operation : updates Customer 9 */
    public void testPutUpdateCustomerJSON() throws Exception {
        JAXRSPopulator jaxrsPopulator = setup();
        jaxrsPopulator.buildExamplesCustomer9();
        jaxrsPopulator.persistExample(session);

        URL url = new URL(getURL());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");

        OutputStream os = connection.getOutputStream();

        Marshaller m = getJAXBContext().createMarshaller();
        m.setProperty("eclipselink.media-type", "application/json");
        m.setProperty("eclipselink.json.include-root", false);
        m.marshal(getControlObject9(), os);
        os.flush();
        int response = connection.getResponseCode();
        Customer testObject = verifyHelperForPostPutJSON(9);
        connection.disconnect();

        assertTrue((response < 300) && (response >= 200));
        assertEquals(getControlObject9(), testObject);
    }

    /* assertion helper method */
    public Customer verifyHelperForPostPut(int id) throws Exception {
        URL url = new URL(getURL() + "/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");

        InputStream xml = connection.getInputStream();
        Customer testObject = (Customer) getJAXBContext().createUnmarshaller()
                .unmarshal(xml);
        connection.disconnect();
        return testObject;
    }

    /* assertion helper method for JSON */
    public Customer verifyHelperForPostPutJSON(int id) throws Exception {
        URL url = new URL(getURL() + "/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        // InputStream xml = connection.getInputStream();
        // Customer testObject = (Customer)
        // getJAXBContext().createUnmarshaller().unmarshal(xml);

        InputStream inputStream = connection.getInputStream();
        StreamSource json = new StreamSource(inputStream);
        Unmarshaller u = getJAXBContext().createUnmarshaller();
        u.setProperty("eclipselink.media-type", "application/json");
        u.setProperty("eclipselink.json.include-root", false);
        Customer testObject = u.unmarshal(json, Customer.class).getValue();
        connection.disconnect();

        return testObject;
    }

    protected Address addressControlObject(int id, String street, String city) {
        Address address = new Address();
        address.setId(id);
        address.setStreet(street);
        address.setCity(city);
        return address;
    }

    protected PhoneNumber phoneControlObject(int id, String num, String type,
            Customer customer) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setId(id);
        phoneNumber.setNum(num);
        phoneNumber.setType(type);
        phoneNumber.setCustomer(customer);
        return phoneNumber;
    }

    protected Customer getControlObject() {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setFirstName("Jane");
        customer.setLastName("Doe");

        customer.setAddress(addressControlObject(1, "1 A Street", "Ottawa"));

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
        phoneNumbers.add(phoneControlObject(1, "555-1111", "WORK", customer));
        phoneNumbers.add(phoneControlObject(2, "555-2222", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    protected Customer getControlObject9() {
        Customer customer = new Customer();
        customer.setId(9);
        customer.setFirstName("Johnys");
        customer.setLastName("Here");

        customer.setAddress(addressControlObject(9, "9 Route", "ManyTown"));

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);

        phoneNumbers.add(phoneControlObject(17, "555-1717", "WORK", customer));
        phoneNumbers.add(phoneControlObject(18, "555-1818", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    protected Customer getControlObject8() {
        Customer customer = new Customer();
        customer.setId(8);
        customer.setFirstName("John");
        customer.setLastName("Does");

        customer.setAddress(addressControlObject(8, "8 A Street", "AnyTown"));

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
        phoneNumbers.add(phoneControlObject(15, "555-1515", "WORK", customer));
        phoneNumbers.add(phoneControlObject(16, "555-1616", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    protected Customer getControlObject7() {
        Customer customer = new Customer();
        customer.setId(7);
        customer.setFirstName("Sophia");
        customer.setLastName("Angelina");
        customer.setAddress(addressControlObject(7, "701 kanata lakes", "kanata"));

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
        phoneNumbers.add(phoneControlObject(13, "555-1313", "WORK", customer));
        phoneNumbers.add(phoneControlObject(14, "555-1414", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    protected Customer getControlObject6() {
        Customer customer = new Customer();
        customer.setId(6);
        customer.setFirstName("Sera");
        customer.setLastName("Quesera");
        customer.setAddress(addressControlObject(6, "101 espanol route", "Barcelona"));

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
        phoneNumbers.add(phoneControlObject(11, "555-1111", "WORK", customer));
        phoneNumbers.add(phoneControlObject(12, "555-1212", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    protected Customer getControlObject5() {
        Customer customer = new Customer();
        customer.setId(5);
        customer.setFirstName("Jack");
        customer.setLastName("Daniel");
        customer.setAddress(addressControlObject(5, "5 B Street", "YourTown"));

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
        phoneNumbers.add(phoneControlObject(9, "555-9999", "WORK", customer));
        phoneNumbers.add(phoneControlObject(10, "555-1010", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    protected Customer getControlObject4() {
        Customer customer = new Customer();
        customer.setId(4);
        customer.setFirstName("John");
        customer.setLastName("Does");
        customer.setAddress(addressControlObject(4, "4 A Street", "AnyTown"));

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
        phoneNumbers.add(phoneControlObject(7, "555-7777", "WORK", customer));
        phoneNumbers.add(phoneControlObject(8, "555-8888", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    protected Customer getControlObject3() {
        Customer customer = new Customer();
        customer.setId(3);
        customer.setFirstName("Sarah");
        customer.setLastName("Smith");
        customer.setAddress(addressControlObject(3, "1 Nowhere Drive", "Ottawa"));

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
        PhoneNumber workPhone = new PhoneNumber();
        workPhone.setId(5);
        workPhone.setNum("555-5555");
        workPhone.setType("WORK");
        workPhone.setCustomer(customer);
        phoneNumbers.add(workPhone);
        PhoneNumber homePhone = new PhoneNumber();
        homePhone.setId(6);
        homePhone.setNum("555-6666");
        homePhone.setType("HOME");
        homePhone.setCustomer(customer);
        phoneNumbers.add(homePhone);
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    protected Customer getControlObject13() {
        Customer customer = new Customer();
        customer.setId(13);
        customer.setFirstName("Larry");
        customer.setLastName("Robinson");
        customer.setAddress(addressControlObject(13, "1 Querbes Avenue", "Montreal"));

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
        phoneNumbers.add(phoneControlObject(25, "555-2525", "WORK", customer));
        phoneNumbers.add(phoneControlObject(26, "555-2626", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    protected Customer getControlObject15() {
        Customer customer = new Customer();
        customer.setId(15);
        customer.setFirstName("Bob");
        customer.setLastName("Gainey");
        customer.setAddress(addressControlObject(15, "15th Avenue", "Montreal"));

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
        phoneNumbers.add(phoneControlObject(29, "555-2929", "WORK", customer));
        phoneNumbers.add(phoneControlObject(30, "555-3030", "HOME", customer));
        customer.setPhoneNumbers(phoneNumbers);

        return customer;
    }

    protected Customers getControlObjects() {
        Customers customerList = new Customers();
        List<Customer> customers = new ArrayList<Customer>(2);
        customers.add(getControlObject());
        customers.add(getControlObject3());
        customerList.setCustomer(customers);

        return customerList;
    }

    protected Customers getControlObjectsJSON() {
        Customers customerList = new Customers();
        List<Customer> customers = new ArrayList<Customer>(2);
        customers.add(getControlObject13());
        customers.add(getControlObject15());
        customerList.setCustomer(customers);

        return customerList;
    }

    protected String getID() {
        return "1";
    }

    protected JAXBContext getJAXBContext() {
        return jc;
    }

    /**
     * Server URL and port are passed through system properties.
     * In case not found default localhost:7001 is used.
     * weblogic: http://localhost:7001/CustomerWAR/rest/customer_war
     * glassfish: http://localhost:8080/CustomerWAR/rest/customer_war
     */
    protected String getURL() {
        final String server = System.getProperty(JAXRS_APP_SERVER, JAXRS_APP_SERVER_DEFAULT);
        final String port = System.getProperty(JAXRS_APP_PORT, JAXRS_APP_PORT_DEFAULT);
        return "http://" + server + ":" + port + "/CustomerWAR/rest/customer_war";
    }
}
