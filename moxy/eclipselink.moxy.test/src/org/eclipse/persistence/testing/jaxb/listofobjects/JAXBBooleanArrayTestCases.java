/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith  June 05, 2009 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBBooleanArrayTestCases extends JAXBListOfObjectsTestCases {
	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/booleanArray.xml";
	private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/booleanArrayNoXsiType.xml";

	public JAXBBooleanArrayTestCases(String name) throws Exception {
		super(name);
		init();
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		Class[] classes = new Class[1];
		classes[0] = boolean[].class;
		setClasses(classes);
	}


    public List< InputStream> getControlSchemaFiles(){			 		   
		InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/booleanArray.xsd");
		
		List<InputStream> controlSchema = new ArrayList<InputStream>();
		controlSchema.add(instream);
		return controlSchema;
	}

	protected Type getTypeToUnmarshalTo() {
		return boolean[].class;
	}

	protected Object getControlObject() {
		boolean[] booleans = new boolean[4];
		booleans[0] = Boolean.FALSE.booleanValue();
		booleans[1] = Boolean.TRUE.booleanValue();
		booleans[2] = Boolean.FALSE.booleanValue();
		booleans[3] = Boolean.TRUE.booleanValue();

		QName qname = new QName("examplenamespace", "root");
		JAXBElement jaxbElement = new JAXBElement(qname, Object.class,null);
		jaxbElement.setValue(booleans);

		return jaxbElement;
	}

	protected void comparePrimitiveArrays(Object controlValue, Object testValue) {
		boolean[] controlArray = (boolean[]) controlValue;
		boolean[] testArray = (boolean[]) testValue;

		assertEquals(controlArray.length, testArray.length);
		for (int i = 0; i < controlArray.length; i++) {
			assertEquals(controlArray[i], testArray[i]);
		}
	}

	protected String getNoXsiTypeControlResourceName() {
		return XML_RESOURCE_NO_XSI_TYPE;
	}

}
