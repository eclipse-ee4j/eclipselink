/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

// Example 1

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlTypeCustomizedOrderingTest extends JAXBWithJSONTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmltype/xmltypecustomizeorder.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmltype/xmltypecustomizeorder.json";
	
	public XmlTypeCustomizedOrderingTest(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		Class[] classes = new Class[1];
		classes[0] = Address.class;
		setClasses(classes);
	}

	protected Object getControlObject() {
		Address addr = new Address();
		addr.setName("JOE");
		addr.setCity("OTTAWA");
		addr.setState("ON");
		addr.setStreet("123 ONE WAY STREET NORTH");
		return addr;
	}



}
