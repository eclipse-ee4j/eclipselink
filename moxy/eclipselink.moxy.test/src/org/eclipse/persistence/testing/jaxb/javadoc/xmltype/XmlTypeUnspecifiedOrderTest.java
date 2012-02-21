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
 *     Praba Vijayaratnam - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

// Example 2

import java.io.IOException;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlTypeUnspecifiedOrderTest extends JAXBWithJSONTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmltype/xmltypeexample2.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmltype/xmltypeexample2.json";

	public XmlTypeUnspecifiedOrderTest(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		Class[] classes = new Class[1];
		classes[0] = Address2.class;
		setClasses(classes);
	}

	protected Object getControlObject() {
		Address2 addr = new Address2();
		addr.setName("JOE");
		addr.setCity("OTTAWA");
		addr.setState("ON");
		addr.setStreet("123 ONE WAY STREET NORTH");
		return addr;
	}

}
