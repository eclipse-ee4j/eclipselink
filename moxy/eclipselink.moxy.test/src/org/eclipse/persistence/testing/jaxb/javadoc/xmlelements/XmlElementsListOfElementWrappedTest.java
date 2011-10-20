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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelements;

import java.util.List;
import java.util.ArrayList;
//import java.util.Calendar;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlElementsListOfElementWrappedTest extends JAXBTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlelements/xmlelementslistwrapped.xml";
	private final static int CONTROL_ID = 10;

	public XmlElementsListOfElementWrappedTest(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		Class[] classes = new Class[1];
		classes[0] = XmlElementsListOfElementWrapped.class;
		setClasses(classes);
	}

	protected Object getControlObject() {
		XmlElementsListOfElementWrapped example = new XmlElementsListOfElementWrapped();
		example.items = new ArrayList();
		example.items.add(new Integer(1));
		example.items.add(new Float(2.5));
		return example;
	}
}
