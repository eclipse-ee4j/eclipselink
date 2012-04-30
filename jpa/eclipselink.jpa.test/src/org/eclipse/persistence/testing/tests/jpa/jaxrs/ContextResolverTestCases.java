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

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.jaxrs.Address;
import org.eclipse.persistence.testing.models.jpa.jaxrs.Customer;

public class ContextResolverTestCases extends JUnitTestCase {

	private JAXBContext jc;

	public ContextResolverTestCases(String name) throws Exception {
		super(name);
		Map<String, Object> props = new HashMap<String, Object>(1);
		props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY,
				"META-INF/binding-address.xml");
		jc = JAXBContext.newInstance(new Class[] { Address.class }, props);
	}

	protected Address getControlObject() {
		Address address = new Address();
		address.setId(1);
		address.setStreet("1 A Street");
		address.setCity("Ottawa");
		return address;
	}

	protected String getID() {
		return "1";
	}

	protected JAXBContext getJAXBContext() {
		return jc;
	}

	/* antbuild.xml will replace %host:port% with values provided in {server}.properties
	 * sample URLs:
	 * 	weblogic: "http://localhost:7001/CustomerWAR/rest/address_war"
	 *	glassfish: "http://localhost:8080/CustomerWAR/rest/address_war"
	 */
	protected String getURL() {
		return "http://%%host:port%%/CustomerWAR/rest/address_war";
	}

	/* READ operation */
	public void testGetAddress() throws Exception {
		URL url = new URL(getURL() + "/" + getID());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");

		InputStream xml = connection.getInputStream();
		Address testObject = (Address) getJAXBContext().createUnmarshaller()
				.unmarshal(xml);
		connection.disconnect();

		assertEquals(getControlObject(), testObject);
	}
	
		/* READ operation */
	public void testGetAddressJSON() throws Exception {
		URL url = new URL(getURL() + "/" + getID());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");

		InputStream inputStream = connection.getInputStream();
		StreamSource json = new StreamSource(inputStream);
		Unmarshaller u = getJAXBContext().createUnmarshaller();
                u.setProperty("eclipselink.media-type", "application/json");
                u.setProperty("eclipselink.json.include-root", false);
		Address testObject = u.unmarshal(json, Address.class).getValue();
		connection.disconnect();		

		assertEquals(getControlObject(), testObject);
	}

}
