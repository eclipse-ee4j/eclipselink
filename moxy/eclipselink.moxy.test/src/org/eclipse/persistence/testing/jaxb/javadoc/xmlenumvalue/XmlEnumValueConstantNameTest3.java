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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlenumvalue;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlEnumValueConstantNameTest3 extends JAXBWithJSONTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlenumvalue/xmlenumvalueconstanttest3.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlenumvalue/xmlenumvalueconstanttest3.json";

	public XmlEnumValueConstantNameTest3(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		Class[] classes = new Class[2];
		classes[0] = MyEnum.class;
		classes[1] = MyEnumModel.class;
		setClasses(classes);
	}

	protected Object getControlObject() {
		MyEnumModel example = new MyEnumModel();
		example.setMyEnum(MyEnum.TWO);
		return example;
	}

}
