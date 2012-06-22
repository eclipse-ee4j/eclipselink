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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlenumvalue;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlEnumValueConstantNameValueTest extends JAXBTestCases {


	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlenumvalue/xmlenumvalueconstantname.xml";
	
	public XmlEnumValueConstantNameValueTest(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		Class[] classes = new Class[3];
		classes[0] = Coin.class;
		classes[1] = Card.class;
		classes[2] = XmlEnumValueConstantNameValue.class;
		setClasses(classes);
	}

	protected Object getControlObject() {
		XmlEnumValueConstantNameValue example = new XmlEnumValueConstantNameValue(); 
		example.setCoin(Coin.QUARTER);
		example.setCard(Card.HEARTS);
		return example;
	}


}
