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
 *     Blaise Doughan - 2.3 - initial implementation
 *     Praba Vijayaratnam - 2.3 - test automation
 *     Praba Vijayaratnam - 2.4 - added JSON support testing 
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.jaxrs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.*;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.jaxrs.Address;
import org.eclipse.persistence.testing.models.jpa.jaxrs.Customer;
import org.eclipse.persistence.testing.models.jpa.jaxrs.Customers;
import org.eclipse.persistence.testing.models.jpa.jaxrs.JAXRSPopulator;
import org.eclipse.persistence.testing.models.jpa.jaxrs.JAXRSTableCreator;
import org.eclipse.persistence.testing.models.jpa.jaxrs.PhoneNumber;

public class DefaultJAXBContextTestCases extends JUnitTestCase {

	private JAXBContext jc;

	public DefaultJAXBContextTestCases(String name) throws Exception {
		super(name);
		jc = JAXBContext.newInstance(Customer.class, Customers.class);
	}

	public void testSetup() {
		DatabaseSession session = JUnitTestCase.getServerSession();
		new JAXRSTableCreator().replaceTables(session);
		JAXRSPopulator jaxrsPopulator = new JAXRSPopulator();
		jaxrsPopulator.buildExamples();

		// Persist the examples in the database
		jaxrsPopulator.persistExample(session);
	}

	/* READ operation */
	public void testGetCustomer() throws Exception {
		URL url = new URL(getURL() + "/" + getID());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");

		InputStream xml = connection.getInputStream();
		Customer testObject = (Customer) getJAXBContext().createUnmarshaller()
				.unmarshal(xml);
		connection.disconnect();
		assertEquals(getControlObject(), testObject);
	}

	/* READ operation */
	public void testGetCustomerJSON() throws Exception {
		URL url = new URL(getURL() + "/" + getID());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");

		InputStream inputStream = connection.getInputStream();
		StreamSource json = new StreamSource(inputStream);
		Unmarshaller u = getJAXBContext().createUnmarshaller();
                u.setProperty("eclipselink.media-type", "application/json");
                u.setProperty("eclipselink.json.include-root", false);
		Customer testObject = u.unmarshal(json, Customer.class).getValue();
		connection.disconnect();
		assertEquals(getControlObject(), testObject);
	}
	
	/* READ operation: CollectionOfObjects */
	public void testGetCollectionOfObjects() throws Exception {
		URL url = new URL(getURL() + "/" + "findCustomerByCity" + "/"
				+ "Ottawa");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");

		InputStream xml = connection.getInputStream();
		Customers testObject = (Customers) getJAXBContext()
				.createUnmarshaller().unmarshal(xml);
		connection.disconnect();
		assertEquals(getControlObjects(), testObject);

	}

	/* READ operation: CollectionOfObjects */
	public void testGetCollectionOfObjectsJSON() throws Exception {
		URL url = new URL(getURL() + "/" + "findCustomerByCity" + "/"
				+ "Ottawa");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");

		InputStream inputStream = connection.getInputStream();
		StreamSource json = new StreamSource(inputStream);
		Unmarshaller u = getJAXBContext().createUnmarshaller();
                u.setProperty("eclipselink.media-type", "application/json");
                u.setProperty("eclipselink.json.include-root", false);
		List<Customer> customers = (List<Customer>) u.unmarshal(json, Customer.class).getValue();				

		connection.disconnect();
		
		Customers testObject = new Customers();
		testObject.setCustomer(customers);
		assertEquals(getControlObjects(), testObject);

	}	
	/* DELETE operation */
	public void testDeleteCustomer() throws Exception {

		URL url = new URL(getURL() + "/" + 2);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("DELETE");
		int response = connection.getResponseCode();

		connection.disconnect();
		assertEquals(204, response);
	}

	/* INSERT operation */
	public void testPostCustomer() throws Exception {

		URL url = new URL(getURL());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/xml");

		OutputStream os = connection.getOutputStream();
		getJAXBContext().createMarshaller().marshal(getControlObject2(), os);
		os.flush();
		connection.getResponseCode();
		Customer testObject = verifyHelperForPostPut(4);
		connection.disconnect();
		assertEquals(getControlObject2(), testObject);

	}

		/* INSERT operation */
	public void testPostCustomerJSON() throws Exception {

		URL url = new URL(getURL());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");

		OutputStream os = connection.getOutputStream();
		getJAXBContext().createMarshaller().marshal(getControlObject2(), os);
		os.flush();
		connection.getResponseCode();
		Customer testObject = verifyHelperForPostPutJSON(4);
		connection.disconnect();
		assertEquals(getControlObject2(), testObject);

	}
	
	/* UPDATE operation */
	public void testPutCustomer() throws Exception {

		URL url = new URL(getURL());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("PUT");
		connection.setRequestProperty("Content-Type", "application/xml");

		OutputStream os = connection.getOutputStream();

		getJAXBContext().createMarshaller().marshal(getControlObject3(), os);
		os.flush();
		connection.getResponseCode();
		Customer testObject = verifyHelperForPostPut(3);
		connection.disconnect();

		assertEquals(getControlObject3(), testObject);

	}
	
	/* UPDATE operation */
	public void testPutCustomerJSON() throws Exception {

		URL url = new URL(getURL());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("PUT");
		connection.setRequestProperty("Content-Type", "application/json");

		OutputStream os = connection.getOutputStream();

		getJAXBContext().createMarshaller().marshal(getControlObject3(), os);
		os.flush();
		connection.getResponseCode();
		Customer testObject = verifyHelperForPostPutJSON(3);
		connection.disconnect();

		assertEquals(getControlObject3(), testObject);

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

	/* assertion helper method for JSON*/
	public Customer verifyHelperForPostPutJSON(int id) throws Exception {
		URL url = new URL(getURL() + "/" + id);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");

		//InputStream xml = connection.getInputStream();
		//Customer testObject = (Customer) getJAXBContext().createUnmarshaller().unmarshal(xml);
		
		InputStream inputStream = connection.getInputStream();
		StreamSource json = new StreamSource(inputStream);
		Unmarshaller u = getJAXBContext().createUnmarshaller();
                u.setProperty("eclipselink.media-type", "application/json");
                u.setProperty("eclipselink.json.include-root", false);
		Customer testObject = u.unmarshal(json, Customer.class).getValue();
		connection.disconnect();
		
		return testObject;
	}
	
	protected Customer getControlObject() {
		Customer customer = new Customer();
		customer.setId(1);
		customer.setFirstName("Jane");
		customer.setLastName("Doe");

		Address address = new Address();
		address.setId(1);
		address.setStreet("1 A Street");
		address.setCity("Ottawa");
		customer.setAddress(address);

		List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
		PhoneNumber workPhone = new PhoneNumber();
		workPhone.setId(1);
		workPhone.setNum("555-1111");
		workPhone.setType("WORK");
		phoneNumbers.add(workPhone);
		PhoneNumber homePhone = new PhoneNumber();
		homePhone.setId(2);
		homePhone.setNum("555-2222");
		homePhone.setType("HOME");
		phoneNumbers.add(homePhone);
		customer.setPhoneNumbers(phoneNumbers);

		return customer;
	}

	protected Customer getControlObject2() {
		Customer customer = new Customer();
		customer.setId(4);
		customer.setFirstName("John");
		customer.setLastName("Does");

		Address address = new Address();
		address.setId(4);
		address.setStreet("4 A Street");
		address.setCity("AnyTown");
		customer.setAddress(address);

		List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);
		PhoneNumber workPhone = new PhoneNumber();
		workPhone.setId(7);
		workPhone.setNum("555-7777");
		workPhone.setType("WORK");
		workPhone.setCustomer(customer);
		phoneNumbers.add(workPhone);
		PhoneNumber homePhone = new PhoneNumber();
		homePhone.setId(8);
		homePhone.setNum("555-8888");
		homePhone.setType("HOME");
		homePhone.setCustomer(customer);
		phoneNumbers.add(homePhone);
		customer.setPhoneNumbers(phoneNumbers);

		return customer;
	}

	protected Customer getControlObject3() {
		Customer customer = new Customer();
		customer.setId(3);
		customer.setFirstName("Sarah");
		customer.setLastName("Smith");

		Address address = new Address();
		address.setId(3);
		address.setStreet("1 Nowhere Drive");
		address.setCity("Ottawa");
		customer.setAddress(address);

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

	protected Customers getControlObjects() {
		Customers customerList = new Customers();
		List<Customer> customers = new ArrayList<Customer>(2);
		customers.add(getControlObject());
		customers.add(getControlObject3());
		customerList.setCustomer(customers);

		return customerList;
	}

	protected String getID() {
		return "1";
	}

	protected JAXBContext getJAXBContext() {
		return jc;
	}
	/* antbuild.xml will replace %host:port% with values provided in {server}.properties
	 * sample URLs:
	 * 	weblogic: "http://localhost:7001/CustomerWAR/rest/customer_war"
	 *	glassfish: "http://localhost:8080/CustomerWAR/rest/customer_war"
	 */
	protected String getURL() {
		return "http://%%host:port%%/CustomerWAR/rest/customer_war";
	}
}
