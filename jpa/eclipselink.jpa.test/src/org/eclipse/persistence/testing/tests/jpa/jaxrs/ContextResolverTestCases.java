/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import javax.xml.bind.JAXBContext;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.jaxrs.Address;
import org.eclipse.persistence.testing.models.jpa.jaxrs.Customer;
import org.eclipse.persistence.testing.models.jpa.jaxrs.Customers;
import org.eclipse.persistence.testing.models.jpa.jaxrs.JAXRSPopulator;
import org.eclipse.persistence.testing.models.jpa.jaxrs.JAXRSTableCreator;
import org.eclipse.persistence.testing.models.jpa.jaxrs.PhoneNumber;

public class ContextResolverTestCases extends JUnitTestCase {

	private JAXBContext jc;
	protected DatabaseSession session;

	public ContextResolverTestCases(String name) throws Exception {
		super(name);
		Map<String, Object> props = new HashMap<String, Object>(1);
		props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY,
				"META-INF/binding-address.xml");
		jc = JAXBContext.newInstance(new Class[] { Address.class }, props);
	}

	public JAXRSPopulator setup() {
		session = JUnitTestCase.getServerSession();
		//new JAXRSTableCreator().replaceTables(session);
		JAXRSTableCreator tableCreator = new JAXRSTableCreator();
		tableCreator.dropTableConstraints(session);
		tableCreator.replaceTables(session);
		JAXRSPopulator jaxrsPopulator = new JAXRSPopulator();
		return jaxrsPopulator;
	}
		
	protected Address getControlObject2() {
		Address address = new Address();
		address.setId(2);
		address.setStreet("1111 Moose Rd.");
		address.setCity("Calgary");
		return address;
	}

	protected Address getControlObject11() {
		Address address = new Address();
		address.setId(11);
		address.setStreet("11 Nowhere Drive");
		address.setCity("Orleans");
		return address;
	}
	
	protected String getID() {
		return "2";
	}

	protected JAXBContext getJAXBContext() {
		return jc;
	}

	/* antbuild.xml will replace %%host:port%% with values provided in {server}.properties
	 * sample URLs:
	 * 	weblogic: "http://localhost:7001/CustomerWAR/rest/address_war"
	 *	glassfish: "http://localhost:8080/CustomerWAR/rest/address_war"
	 */
	protected String getURL() {
		return "http://%%host:port%%/CustomerWAR/rest/address_war";
	}

	/* READ operation  - Uses Customer 2 */
  	public void testGetAddress() throws Exception {
		JAXRSPopulator jaxrsPopulator = setup();
		jaxrsPopulator.buildExamplesCustomer2();
		jaxrsPopulator.persistExample(session);
		
		URL url = new URL(getURL() + "/" + "2");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");

		InputStream xml = connection.getInputStream();
		Address testObject = (Address) getJAXBContext().createUnmarshaller()
				.unmarshal(xml);
		int response = connection.getResponseCode();		
		connection.disconnect();
		
		assertTrue (( response < 300) && ( response >= 200));
		assertEquals(getControlObject2(), testObject);
	}  
	
		/* READ operation  - Uses Customer 11 */
 	public void testGetAddressJSON() throws Exception {
		JAXRSPopulator jaxrsPopulator = setup();
		jaxrsPopulator.buildExamplesCustomer11();
		jaxrsPopulator.persistExample(session);
		
		URL url = new URL(getURL() + "/" + "11");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");

		InputStream inputStream = connection.getInputStream();
		StreamSource json = new StreamSource(inputStream);
		Unmarshaller u = getJAXBContext().createUnmarshaller();
                u.setProperty("eclipselink.media-type", "application/json");
                u.setProperty("eclipselink.json.include-root", false);
		Address testObject = u.unmarshal(json, Address.class).getValue();
		int response = connection.getResponseCode();
		connection.disconnect();
		
		assertTrue (( response < 300) && ( response >= 200));
		assertEquals(getControlObject11(), testObject);
	} 

}
