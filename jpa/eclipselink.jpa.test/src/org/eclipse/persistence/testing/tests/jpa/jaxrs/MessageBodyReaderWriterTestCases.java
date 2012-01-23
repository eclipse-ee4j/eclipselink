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
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.jaxrs;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.jaxrs.Address;
import org.eclipse.persistence.testing.models.jpa.jaxrs.PhoneNumber;

public class MessageBodyReaderWriterTestCases extends JUnitTestCase {

	private JAXBContext jc;

	public MessageBodyReaderWriterTestCases(String name) throws Exception {
		super(name);
		Map<String, Object> properties = new HashMap<String, Object>(1);

		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY,
				"META-INF/binding-phonenumber.xml");
		jc = JAXBContext.newInstance(new Class[] { PhoneNumber.class },
				properties);
	}

	protected PhoneNumber getControlObject() {
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumber.setId(2);
		phoneNumber.setType("HOME");
		phoneNumber.setNum("555-2222");
		return phoneNumber;
	}

	protected String getID() {
		return "2";
	}

	protected JAXBContext getJAXBContext() {
		return jc;
	}

	/* antbuild.xml will replace %host:port% with values provided in {server}.properties
	 * sample URLs:
	 * 	weblogic: "http://localhost:7001/CustomerWAR/rest/phonenumber_war"
	 *	glassfish: "http://localhost:8080/CustomerWAR/rest/phonenumber_war"
	 */
	protected String getURL() {
		return "http://%%host:port%%/CustomerWAR/rest/phonenumber_war";
	}

	/* READ operation */
	public void testGetPhoneNumber() throws Exception {
		URL url = new URL(getURL() + "/" + getID());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");

		InputStream xml = connection.getInputStream();
		PhoneNumber testObject = (PhoneNumber) getJAXBContext()
				.createUnmarshaller().unmarshal(xml);
		connection.disconnect();

		assertEquals(getControlObject(), testObject);
	}

}
