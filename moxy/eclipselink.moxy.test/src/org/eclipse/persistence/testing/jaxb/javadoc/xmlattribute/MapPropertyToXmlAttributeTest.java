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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlattribute;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class MapPropertyToXmlAttributeTest extends JAXBTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlattribute/propertyxmlattribute.xml";
	
	public MapPropertyToXmlAttributeTest(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		Class[] classes = new Class[1];
		classes[0] = USPrice.class;
		setClasses(classes);
	}

	protected Object getControlObject() {
		USPrice p = new USPrice();
		p.setPrice(123.99);
		return p;
	}

}
